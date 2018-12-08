package com.bars.orders;

import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bars.orders.http.SimpleHttpClient;
import com.bars.orders.json.Order;
import com.bars.orders.mongo.MyMongoClient;
import com.bars.orders.operations.FieldsRemapper;
import com.bars.orders.operations.SetSplitter;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    private final HttpRequestMessage<Optional<String>> request;
    private final ExecutionContext context;
    private final Logger logger;

    private MyMongoClient myMongoClient;
    private SimpleHttpClient httpClient;

    private Order order;

    public Function(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        this.request = request;
        this.context = context;

        this.logger = context.getLogger();

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

    public HttpResponseMessage run() {

        logger.info("Received new HTTP request");

        String body = request.getBody().orElse(null);
        if (body == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error: Empty body received").build();
        }

        try {
            logger.info("Incoming body: " + body);
            String decodedBody = URLDecoder.decode(body, "UTF-8");

            order = new Order(decodedBody, context);
            String orderId = order.getOrderId();

            List<String> orderIds = myMongoClient.getOrderIds();
            if (! orderIds.contains(orderId)) {
                logger.info("Received new order " + orderId);
                processOrder();

                myMongoClient.storeOrder(order);

                httpClient.sendZapier(order.toJson());

//                SmsAero sms = new SmsAero(order, context);
//                httpClient.sendSmsAero(sms.toJson());

            } else {
                logger.log(Level.WARNING, "Received the same order " + orderId + ". Will skip");
            }

        } catch (Exception e) {

            logger.log(Level.WARNING, "Couldn't process request. Error msg: " + e.getMessage(), e);
            logger.log(Level.WARNING, "Request body: " + request.getBody());

            return request.createResponseBuilder(HttpStatus.OK).body("Dummy done").build();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("Done").build();
    }

    public void processOrder() {
        new SetSplitter(context).splitSets(order);
        FieldsRemapper fieldsRemapper = new FieldsRemapper(context);

        fieldsRemapper.remapDelivery(order);
        fieldsRemapper.remapProductNames(order);

        fieldsRemapper.setOrderDescription(order);
    }
}
