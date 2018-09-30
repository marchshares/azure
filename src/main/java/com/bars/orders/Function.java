package com.bars.orders;

import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private String zapierProductsUrl;
    private MyMongoClient myMongoClient;

    private Order order;

    public Function(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        this.request = request;
        this.context = context;

        this.logger = context.getLogger();

        this.zapierProductsUrl = System.getenv("ZapierProductsWebhookUrl");
        this.myMongoClient = new MyMongoClient(logger);
    }

    public void setMyMongoClient(MyMongoClient myMongoClient) {
        this.myMongoClient = myMongoClient;
    }


    public void setZapierProductsUrl(String zapierProductsUrl) {
        this.zapierProductsUrl = zapierProductsUrl;
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
            String decodedBody = URLDecoder.decode(body, "UTF-8");

            order = new Order(decodedBody, context);
            String orderId = order.getOrderId();

            List<String> orderIds = myMongoClient.getOrderIds();
            if (! orderIds.contains(orderId)) {
                logger.info("Received new order " + orderId);
                myMongoClient.addOrder(orderId);

                processOrder();

                sendPost(zapierProductsUrl, order.toJson());

            } else {
                logger.log(Level.WARNING, "Received the same order " + orderId + ". Will skip");
            }

        } catch (Exception e) {

            logger.log(Level.WARNING, "Couldn't process request. Error msg: " + e.getMessage(), e);

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

    public void sendPost(String url, String body) {
        if (url == null) {
            logger.warning("URL is null. Request wasn't send");
            return;
        }

        try {
            URLConnection con = new URL(url).openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            byte[] out = body.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("charset", "utf-8");

            logger.info("connect to: " + url);
            logger.info("body: " + body);
            http.connect();

            OutputStream os = null;
            try {
                os = http.getOutputStream();
                os.write(out);
                os.flush();

                if (http.getResponseCode() == 200) {
                    logger.info("Received OK!");
                } else {
                    logger.log(Level.WARNING, "Received bad response code: " + http.getResponseCode() + ", msg: " + http.getResponseMessage());
                }
            } finally {
                if (os != null) {
                    os.close();
                }

                http.disconnect();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "ERROR msg: " + ex.getMessage(), ex);
        }

        logger.info("POST request has been finished");
    }
}
