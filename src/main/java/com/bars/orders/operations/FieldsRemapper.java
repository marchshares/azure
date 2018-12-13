package com.bars.orders.operations;

import com.bars.orders.json.Order;
import com.bars.orders.json.Product;
import com.google.common.collect.Maps;
import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONArray;
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
    public static final String CDEK_NAME_LABEL = "CDEK";

    public static final String NANOPRESSO_NAME = "Nanopresso";
    public static final String NANOPRESSO_PATROL_NAME = "Nanopresso Patrol";
    public static final String BLACK_COLOR_NAME = "Black";

    private final Logger log;

    public static final Map<String, String> mapProductNames = Maps.newHashMap();

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

    public void remapDelivery(Order order) {
        String deliveryType;
        String trelloDeliveryType;
        String deliveryClientCost = UNDEFINED_VALUE;
        String deliveryCompany    = UNDEFINED_VALUE;
        String fullDeliveryCost   = UNDEFINED_VALUE;

        log.info("Remap delivery for " + order.getOrderId());
        JSONObject jsonObject = order.getHead();

        String originalDeliveryType = jsonObject.getString("deliveryType");

        if (originalDeliveryType.contains(DELIVERY_COURIER_NAME_LABEL)) {
            deliveryType = DELIVERY_COURIER_NAME_LABEL;
            trelloDeliveryType = jsonObject.getString("city");

            Integer preDefDeliveryCost = tryToFindDeliveryCost(originalDeliveryType);
            if (preDefDeliveryCost != null) {
                JSONObject payment = order.getPayment();

                int amountWithDelivery = payment.getInt("amount");
                payment.put("amount", valueOf(amountWithDelivery - preDefDeliveryCost));

                deliveryClientCost = preDefDeliveryCost.toString();
            }

        } else if (originalDeliveryType.contains(SAMOVIVOZ_NAME_LABEL)){
            deliveryType = SAMOVIVOZ_NAME_LABEL;
            trelloDeliveryType = SAMOVIVOZ_NAME_LABEL;
            deliveryClientCost = "0";
            deliveryCompany = SAMOVIVOZ_NAME_LABEL;
            fullDeliveryCost = "0";

        } else if (originalDeliveryType.contains(DELIVERY_ON_RUSSIA_NAME_LABEL)){
            deliveryType = DELIVERY_ON_RUSSIA_NAME_LABEL;
            trelloDeliveryType = CDEK_NAME_LABEL + " " + jsonObject.getString("city");
            deliveryCompany = CDEK_NAME_LABEL;

        } else {
            log.log(Level.WARNING, "Unknown delivery type: " + originalDeliveryType);
            return;
        }

        jsonObject.put("deliveryType",       deliveryType);
        jsonObject.put("trelloDeliveryType", trelloDeliveryType);
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
