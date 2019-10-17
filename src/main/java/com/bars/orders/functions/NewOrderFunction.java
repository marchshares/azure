package com.bars.orders.functions;

import java.net.*;
import java.util.*;
import java.util.logging.Level;

import com.bars.orders.http.ZapierHttpClient;
import com.bars.orders.json.Order;
import com.bars.orders.mongo.MyMongoClient;
import com.bars.orders.operations.FieldsRemapper;
import com.bars.orders.operations.SetSplitter;
import com.microsoft.azure.functions.*;

import static com.bars.orders.GlobalLogger.glogger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class NewOrderFunction extends AbstractFunction{

    private MyMongoClient myMongoClient;
    private ZapierHttpClient zapierHttpClient;

    private Order order;

    public NewOrderFunction(HttpRequestMessage<Optional<String>> request) {
        super(request);

        this.myMongoClient = new MyMongoClient();
        this.zapierHttpClient = new ZapierHttpClient();
    }

    public void setMyMongoClient(MyMongoClient myMongoClient) {
        this.myMongoClient = myMongoClient;
    }

    public MyMongoClient getMyMongoClient() {
        return myMongoClient;
    }

    public void setZapierHttpClient(ZapierHttpClient zapierHttpClient) {
        this.zapierHttpClient = zapierHttpClient;
    }

    public Order getOrder() {
        return order;
    }

    public void init() {
        myMongoClient.init();
    }

    @Override
    String processRequest(String body, Map<String, String> headers) throws Exception{
        String decodedBody = URLDecoder.decode(body, "UTF-8");

        order = new Order(decodedBody);
        enrichFromHeaders(headers);

        String orderId = order.getOrderId();

        List<String> orderIds = myMongoClient.getOrderIds();
        if (! orderIds.contains(orderId)) {
            glogger.info("Received new order " + orderId);
            processOrder();

            myMongoClient.storeOrder(order);

            zapierHttpClient.sendOrderToZapier(order);
        } else {
            String msg = "Received the same order " + orderId + ". Will skip";
            glogger.log(Level.WARNING, msg);
            return msg;
        }

        return "Done";
    }

    private void enrichFromHeaders(Map<String, String> headers) {
        String referer = headers.get("referer");
        order.setReferer(referer);
    }

    public void processOrder() {
        new SetSplitter().splitSets(order);
        FieldsRemapper fieldsRemapper = new FieldsRemapper();

        fieldsRemapper.remapDelivery(order);
        fieldsRemapper.setOrderDescription(order);
    }
}
