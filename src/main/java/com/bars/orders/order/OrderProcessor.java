package com.bars.orders.order;

import com.bars.orders.Utils;
import com.bars.orders.product.Product;
import com.bars.orders.product.ProductProcessor;
import com.bars.orders.product.desc.ProductDescManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bars.orders.GlobalLogger.glogger;

public class OrderProcessor {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd.MM");
    private static final SimpleDateFormat mongoDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    }

    private Order order;
    private ProductProcessor productProcessor;

    public OrderProcessor() {
        this.productProcessor = new ProductProcessor();
    }

    public void setDescManager(ProductDescManager descManager) {
        productProcessor.setDescManager(descManager);
    }

    public void process(Order inputOrder, Map<String, String> headers) {
        this.order = inputOrder;
        glogger.info("Start process order " + order.getOrderId());

        processProducts();
        enrichFromHeaders(headers);
        enrichAdditionalFields();
    }

    private void processProducts() {
        JSONArray jsonProducts = order.getPayment().getJSONArray("products");
        List<Product> products = Utils.jsonArrayToStream(jsonProducts)
                .map(obj -> (JSONObject) obj)
                .flatMap(jsonObject -> productProcessor.process(jsonObject).stream())
                .collect(Collectors.toList());

        order.setProducts(products);
    }

    private void enrichFromHeaders(Map<String, String> headers) {
        String referer = headers.get("referer");
        order.setReferer(referer);
    }

    private void enrichAdditionalFields() {
        JSONObject head = order.getHead();

        enrichOrderDescription();

        String[] dateTimeValue = dateFormat.format(new Date()).split(" ");
        head.put("date", dateTimeValue[0]);
        head.put("time", dateTimeValue[1]);

        head.put("shortDate", shortDateFormat.format(new Date()));
        head.put("mongoDate", mongoDateFormat.format(new Date()));
    }

    private void enrichOrderDescription() {
        String orderDescription = order.getProducts().stream()
                .filter(product -> !product.isSetComponent() || product.isMainSetComponent())
                .map(product -> {
                    if (product.isSetComponent()) {
                        return product.getSetName();
                    } else {
                        return product.getName();
                    }
                }).collect(Collectors.joining(", "));

        order.setOrderDescription(orderDescription);
        glogger.info("Enrich orderDescription=" + orderDescription);
    }
}
