package com.bars.orders.smsaero;

import com.bars.orders.json.AbstractObject;
import com.bars.orders.json.Order;
import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONObject;

public class SmsAero extends AbstractObject {

    public SmsAero(Order order, ExecutionContext context) {
        super(context);
        buildHead(order);
    }

    public JSONObject buildHead(Order order) {

        head.put("channel", "DIRECT");
        head.put("sign", "8Bars");
        head.put("number", order.getPhone());

        //head.put("text", "youtextXXX");

        return head;
    }
}
