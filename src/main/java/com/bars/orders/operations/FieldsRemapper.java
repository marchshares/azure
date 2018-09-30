package com.bars.orders.operations;

import com.bars.orders.json.Order;
import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONObject;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static java.lang.String.valueOf;

public class FieldsRemapper {

    public static final String UNDEFINED_VALUE = "_???";

    public static final String DELIVERY_COURIER_NAME_LABEL = "Доставка курьером";
    public static final String SAMOVIVOZ_NAME_LABEL = "Самовывоз";
    public static final String DELIVERY_ON_RUSSIA_NAME_LABEL = "Доставка по России";
    private final Logger log;

    public static final Map<String, String> mapProductNames = of(
            "nanopresso ns-адаптер", "NS-адаптер",
            "nanopresso barista kit", "Barista Kit"
    );

    public static final Map<String, String> mapRUColorOnEngColor = of(
            "Желтый",      "Yellow",
            "Оранжевый",   "Orange",
            "Красный",     "Red",
            "Темно-серый", "Grey"
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
                    String color = product.getColor();

                    if (!mappedName.contains(color)) {
                        mappedName = mappedName + " " + color;
                    }
                }

                if (!productName.equals(mappedName)) {
                    product.setName(mappedName);
                    log.info("Remap name: " + productName + " -> " + mappedName);
                }
            });
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
