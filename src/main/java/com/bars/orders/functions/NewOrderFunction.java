package com.bars.orders.functions;

import java.net.*;
import java.util.*;
import java.util.logging.Level;

import com.bars.orders.http.SimpleHttpClient;
import com.bars.orders.json.Order;
import com.bars.orders.mongo.MyMongoClient;
import com.bars.orders.operations.FieldsRemapper;
import com.bars.orders.operations.SetSplitter;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class NewOrderFunction extends AbstractFunction{

    private MyMongoClient myMongoClient;
    private SimpleHttpClient httpClient;

    private Order order;

    public NewOrderFunction(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        super(request, context);

        this.myMongoClient = new MyMongoClient(logger);
        this.httpClient = new SimpleHttpClient(logger);
    }

    public void setMyMongoClient(MyMongoClient myMongoClient) {
        this.myMongoClient = myMongoClient;
    }

    public void setHttpClient(SimpleHttpClient httpClient) {
        this.httpClient = httpClient;
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

        order = new Order(decodedBody, context);
        enrichFromHeaders(headers);

        String orderId = order.getOrderId();

        List<String> orderIds = myMongoClient.getOrderIds();
        if (! orderIds.contains(orderId)) {
            logger.info("Received new order " + orderId);
            processOrder();

            myMongoClient.storeOrder(order);

            httpClient.sendZapier(order.toJson());

        } else {
            String msg = "Received the same order " + orderId + ". Will skip";
            logger.log(Level.WARNING, msg);
            return msg;
        }

        return "Done";
    }

    private void enrichFromHeaders(Map<String, String> headers) {
        String referer = headers.get("referer");
        order.setReferer(referer);
    }

    public void processOrder() {
        new SetSplitter(context).splitSets(order);
        FieldsRemapper fieldsRemapper = new FieldsRemapper(context);

        fieldsRemapper.remapDelivery(order);
        fieldsRemapper.setOrderDescription(order);
    }
}
