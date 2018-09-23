package com.bars.orders.json;


import com.bars.orders.Utils;
import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;

public class Order extends AbstractObject {

    private final JSONObject payment;
    private List<Product> products;

    public Order(String bodyLine, ExecutionContext context) {
        super(new Converter().bodyLineToJsonObject(bodyLine), context);

        this.payment = head.getJSONObject("payment");

        this.products = Utils.jsonArrayToStream(payment.getJSONArray("products"))
                .map(obj -> (JSONObject) obj)
                .map(jsonObject -> new Product(jsonObject, context))
                .collect(Collectors.toList());;
    }

    public JSONObject getPayment() {
        return payment;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> newProducts) {
        this.products = newProducts;

        JSONArray jsonArray = new JSONArray(
                products.stream()
                        .map(product -> product.head)
                        .collect(Collectors.toList())
        );

        payment.put("products", jsonArray);
    }

    public String getOrderId() {
        return getPayment().getString("orderid");
    }
}
