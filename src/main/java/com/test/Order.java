package com.test;

import org.json.JSONObject;

public class Order {

    private final JSONObject jsonObject;

    public Order(String bodyLine) {
        jsonObject = new Converter().bodyLineToJsonObject(bodyLine);
        System.out.println(jsonObject);
    }
}
