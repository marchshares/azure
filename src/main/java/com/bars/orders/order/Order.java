package com.bars.orders.order;


import com.bars.orders.json.AbstractObject;
import com.bars.orders.product.Product;
import com.google.common.annotations.VisibleForTesting;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static com.bars.orders.mongo.MyMongoClient.ORDER_ID_KEY;

public class Order extends AbstractObject {

    private final JSONObject payment;
    private List<Product> products;

    public Order(JSONObject orderJson) {
        super(orderJson);
        this.payment = head.getJSONObject("payment");
        head.put(ORDER_ID_KEY, payment.getString("orderid"));
    }

    public JSONObject getPayment() {
        return payment;
    }

    public String getPhone() {
        return head.getString("Phone");
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> newProducts) {
        this.products = newProducts;

        JSONArray jsonArray = new JSONArray(
                newProducts.stream()
                        .map(AbstractObject::getHead)
                        .collect(Collectors.toList())
        );

        payment.put("products", jsonArray);
    }

    @VisibleForTesting
    void setOrderId(String orderId) {
        head.put(ORDER_ID_KEY, orderId);
    }

    public String getOrderId() {
        return head.getString(ORDER_ID_KEY);
    }

    public void setOrderDescription(String orderDescription) {
        head.put("orderDescription", orderDescription);
    }

    public String getOrderDescription() {
        return head.getString("orderDescription");
    }

    public void setReferer(@Nullable String referer) {
        if (referer != null) {
            head.put("referer", referer);
        }
    }

    public String toArrayJson(){
        JSONArray arr = new JSONArray();
        products.stream()
                .map(Product::makeCopy)
                .forEach(product -> {
                    JSONObject jsonObject = product.getHead();

                    head.names().forEach(name -> {
                        String nameStr = (String) name;
                        if (! nameStr.equals("payment")) {
                            jsonObject.put(nameStr, head.getString(nameStr));
                        } else {
                            JSONObject payment = head.getJSONObject(nameStr);

                            payment.names().forEach(name1 -> {
                                String name1Str = (String) name1;
                                if (! name1Str.equals("products")) {

                                    if (name1Str.equals("amount")) {
                                        jsonObject.put("fullAmount", payment.getString("amount"));

                                    } else {
                                        jsonObject.put(name1Str, payment.getString(name1Str));
                                    }
                                }
                            });

                        }
                    });

                    arr.put(jsonObject);
                });


        return arr.toString();
    }
}
