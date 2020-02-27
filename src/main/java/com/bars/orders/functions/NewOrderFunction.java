package com.bars.orders.functions;

import java.net.*;
import java.util.*;
import java.util.logging.Level;

import com.bars.orders.http.ZapierHttpClient;
import com.bars.orders.json.Converter;
import com.bars.orders.order.Order;
import com.bars.orders.order.OrderProcessor;
import com.bars.orders.mongo.MyMongoClient;
import com.bars.orders.product.desc.ProductDescManager;
import com.microsoft.azure.functions.*;
import org.json.JSONObject;

import static com.bars.orders.GlobalLogger.glogger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class NewOrderFunction extends AbstractFunction{

    private MyMongoClient myMongoClient;
    private ZapierHttpClient zapierHttpClient;
    private OrderProcessor orderProcessor;
    private ProductDescManager productDescManager;

    private Order order;

    public NewOrderFunction(HttpRequestMessage<Optional<String>> request) {
        super(request);

        this.myMongoClient = new MyMongoClient();
        this.zapierHttpClient = new ZapierHttpClient();
        this.productDescManager = new ProductDescManager();

        this.orderProcessor = new OrderProcessor();
        orderProcessor.setDescManager(productDescManager);
    }

    public void init() throws Exception {
        myMongoClient.init();
        productDescManager.init();
    }

    public void setMyMongoClient(MyMongoClient myMongoClient) {
        this.myMongoClient = myMongoClient;
    }

    public void setZapierHttpClient(ZapierHttpClient zapierHttpClient) {
        this.zapierHttpClient = zapierHttpClient;
    }

    public MyMongoClient getMyMongoClient() {
        return myMongoClient;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    String processRequest(String body, Map<String, String> headers) throws Exception{
        String decodedBody = URLDecoder.decode(body, "UTF-8");
        JSONObject orderJson = Converter.bodyLineToJsonObject(decodedBody);

        order = new Order(orderJson);
        String orderId = order.getOrderId();

        List<String> orderIds = myMongoClient.getOrderIds();
        if (! orderIds.contains(orderId)) {
            glogger.info("Received new order " + orderId);
            orderProcessor.process(order, headers);

            myMongoClient.storeOrder(order);

            zapierHttpClient.sendOrderToZapier(order);
        } else {
            String msg = "Received the same order " + orderId + ". Will skip";
            glogger.log(Level.WARNING, msg);
            return msg;
        }

        return "Done";
    }
}
