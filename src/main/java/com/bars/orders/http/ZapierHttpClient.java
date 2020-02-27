package com.bars.orders.http;

import com.bars.orders.http.common.SimpleHttpClient;
import com.bars.orders.order.Order;

import static com.bars.orders.PropertiesHelper.getSystemProp;

public class ZapierHttpClient extends SimpleHttpClient {

    private String zapierProductsUrl;

    public ZapierHttpClient() {
        super();
        setRequestContentType("application/json");

        this.zapierProductsUrl = getSystemProp("ZapierProductsWebhookUrl");
    }

    public void sendOrderToZapier(Order order) {
        sendPost(zapierProductsUrl, order.toJson());
    }
}