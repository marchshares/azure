package com.bars.orders.operations;

import com.bars.orders.json.Order;
import com.bars.orders.json.Product;
import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.bars.orders.operations.SetSplitter.NANO_SET_CASES;
import static com.google.common.collect.ImmutableMap.of;
import static java.lang.String.valueOf;

public class FieldsRemapper {

    public static final String UNDEFINED_VALUE = "_???";

    public static final String DELIVERY_COURIER_NAME_LABEL = "Доставка курьером";
    public static final String SAMOVIVOZ_NAME_LABEL = "Самовывоз";
    public static final String DELIVERY_ON_RUSSIA_NAME_LABEL = "Доставка по России";

    public static final String NANOPRESSO_NAME = "Nanopresso";
    public static final String BLACK_COLOR_NAME = "Black";

    private final Logger log;

    public static final Map<String, String> mapProductNames = of(
            "nanopresso ns-адаптер", "NS-адаптер",
            "nanopresso barista kit", "Barista Kit",
            "nanopresso s-чехол", "S-Чехол",
            "nanopresso m-чехол", "M-Чехол",
            "nanopresso l-чехол", "L-Чехол"
    );

    public static final Map<String, String> mapRUColorOnEngColor = of(
            "Желтый",      "Yellow",
            "Оранжевый",   "Orange",
            "Красный",     "Red",
            "Черный",      "Black"
    );

    public FieldsRemapper(ExecutionContext context) {
        log = context.getLogger();
    }

    public void setOrderDescription(Order order) {
        String orderDescription = order.getProducts().stream()
                .filter(product -> !product.isSetComponent() || (product.isSetComponent() && product.isMainSetComponent()))
                .map(product -> {
                    if (product.isSetComponent()) {
                        return product.getSetName();
                    } else {
                        return product.getName();
                    }
                }).collect(Collectors.joining(", "));


        order.setOrderDescription(orderDescription);
    }

    public void remapProductNames(Order order) {
        order.getProducts()
            .forEach(product -> {
                String productName = product.getName();

                String simplyfiedName = productName
                        .replaceAll("(?i)wacaco", "")
                        .replaceAll("(?i)для nanopresso", "")
                        .trim();

                String mappedName = mapProductNames.getOrDefault(simplyfiedName.toLowerCase(), simplyfiedName);

                if (product.hasColor()) {
                    mappedName = remapColored(product.getColor(), mappedName);
                }

                if (product.isCase()) {
                    mappedName = remapCase(product, mappedName);
                }

                if (!productName.equals(mappedName)) {
                    product.setName(mappedName);
                    log.info("Remap name: " + productName + " -> " + mappedName);
                }
            });
    }

    private String remapCase(Product product, String mappedName) {
        JSONArray options = product.getOptions();

        if (options == null || options.isEmpty()) {
            return mappedName;
        }

        JSONObject optionSize = options.getJSONObject(0);
        if (optionSize.getString("option").equals("Размер")) {
            String variant = optionSize.getString("variant");

            return NANO_SET_CASES.stream()
                    .filter(variant::contains)
                    .peek(sizeName -> log.info(variant + " -> " + sizeName))
                    .findFirst()
                    .orElse(mappedName);
        }

        return mappedName;
    }

    private String remapColored(String color, String mappedName) {
        if (! (mappedName.equals(NANOPRESSO_NAME) && color.equals(BLACK_COLOR_NAME)) ) {
            if (!mappedName.contains(color)) {
                mappedName = mappedName + " " + color;
            }
        }

        return mappedName;
    }

    public void remapDelivery(Order order) {
        String deliveryType       = UNDEFINED_VALUE;
        String deliveryClientCost = UNDEFINED_VALUE;
        String deliveryCompany    = UNDEFINED_VALUE;
        String fullDeliveryCost   = UNDEFINED_VALUE;

        log.info("Remap delivery for " + order.getOrderId());
        JSONObject jsonObject = order.getHead();

        String originalDeliveryType = jsonObject.getString("deliveryType");

        if (originalDeliveryType.contains(DELIVERY_COURIER_NAME_LABEL)) {
            deliveryType = DELIVERY_COURIER_NAME_LABEL;

            Integer preDefDeliveryCost = tryToFindDeliveryCost(originalDeliveryType);
            if (preDefDeliveryCost != null) {
                JSONObject payment = order.getPayment();

                int amountWithDelivery = payment.getInt("amount");
                payment.put("amount", valueOf(amountWithDelivery - preDefDeliveryCost));

                deliveryClientCost = preDefDeliveryCost.toString();
            }

        } else if (originalDeliveryType.contains(SAMOVIVOZ_NAME_LABEL)){
            deliveryType = SAMOVIVOZ_NAME_LABEL;
            deliveryClientCost = "0";
            deliveryCompany = SAMOVIVOZ_NAME_LABEL;
            fullDeliveryCost = "0";

        } else if (originalDeliveryType.contains(DELIVERY_ON_RUSSIA_NAME_LABEL)){
            deliveryType = DELIVERY_ON_RUSSIA_NAME_LABEL;

        } else {
            log.log(Level.WARNING, "Unknown delivery type: " + originalDeliveryType);
            return;
        }

        jsonObject.put("deliveryType",       deliveryType);
        jsonObject.put("deliveryClientCost", deliveryClientCost);
        jsonObject.put("deliveryCompany",    deliveryCompany);
        jsonObject.put("fullDeliveryCost",   fullDeliveryCost);
    }

    private static Integer tryToFindDeliveryCost(String deliveryType) {
        String[] split = deliveryType.split("=");

        if (split.length > 1) {
            String deliveryCost = split[split.length - 1].trim();
            try {
                return Integer.parseInt(deliveryCost);
            } catch (NumberFormatException e) {
                //ignore
            }
        }

        return null;
    }
}
