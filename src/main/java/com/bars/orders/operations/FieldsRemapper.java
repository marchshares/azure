package com.bars.orders.operations;

import com.bars.orders.json.Order;
import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.valueOf;

public class FieldsRemapper {

    public static final String UNDEFINED_VALUE = "_???";

    public static final String DELIVERY_COURIER_NAME_LABEL = "Доставка курьером";
    public static final String SAMOVIVOZ_NAME_LABEL = "Самовывоз";
    public static final String DELIVERY_ON_RUSSIA_NAME_LABEL = "Доставка по России";
    private final Logger log;

    public FieldsRemapper(ExecutionContext context) {
        log = context.getLogger();
    }

    public void remapDelivery(Order order) {
        log.info("Remap delivery for " + order.getOrderId());
        JSONObject jsonObject = order.getHead();

        String deliveryType = jsonObject.getString("deliveryType");

        if (deliveryType.contains(DELIVERY_COURIER_NAME_LABEL)) {
            jsonObject.put("deliveryType", DELIVERY_COURIER_NAME_LABEL);

            Integer preDefDeliveryCost = tryToFindDeliveryCost(deliveryType);
            if (preDefDeliveryCost != null) {
                JSONObject payment = order.getPayment();

                int amountWithDelivery = payment.getInt("amount");
                payment.put("amount", valueOf(amountWithDelivery - preDefDeliveryCost));

                jsonObject.put("deliveryClientCost", preDefDeliveryCost.toString());
            } else {
                jsonObject.put("deliveryClientCost", UNDEFINED_VALUE);
            }

        } else if (deliveryType.contains(SAMOVIVOZ_NAME_LABEL)){
            jsonObject.put("deliveryType",       SAMOVIVOZ_NAME_LABEL);
            jsonObject.put("deliveryClientCost", "0");

        } else if (deliveryType.contains(DELIVERY_ON_RUSSIA_NAME_LABEL)){
            jsonObject.put("deliveryType",       DELIVERY_ON_RUSSIA_NAME_LABEL);
            jsonObject.put("deliveryClientCost", UNDEFINED_VALUE);

        } else {
            log.log(Level.WARNING, "Unknown delivery type: " + deliveryType);
            return;
        }
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
