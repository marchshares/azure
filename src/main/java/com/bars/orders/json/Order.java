package com.bars.orders.json;


import com.bars.orders.Utils;
import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;

public class Order extends AbstractObject {

    private final JSONObject payment;
    private List<Product> products;
    private final SimpleDateFormat dateFormat;

    public Order(String bodyLine, ExecutionContext context) {
        super(new Converter().bodyLineToJsonObject(bodyLine), context);

        this.payment = head.getJSONObject("payment");
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        head.put("date", dateFormat.format(new Date()));

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


    public String toArrayJson(){
        JSONArray arr = new JSONArray();
        products.stream()
                .map(Product::makeCopy)
                .forEach(product -> {
                    JSONObject jsonObject = product.head;

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
