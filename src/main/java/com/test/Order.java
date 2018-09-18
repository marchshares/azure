package com.test;

import org.json.JSONObject;

import java.util.Arrays;

public class Order {

    private final JSONObject jsonObject;

    public Order(String bodyLine) {
        jsonObject = new JSONObject();

        String[] params = bodyLine.split("&");

        Arrays.stream(params).forEach(param -> {
            String[] split = param.split("=", 2);

            String name = split[0];
            String value = split[1];

            if (! name.contains("[")) {
                jsonObject.put(name, value);
            } else {
                String[] split1 = param.split("(\\[|\\])", 3);

                String entryName = split1[0];
                String fieldName = split1[1];

                String[] split2 = split1[2].split("=");
                String fieldAddName = split2[0];
                String fieldValue = split2[1];

                if (! jsonObject.has(entryName)) {
                    jsonObject.put(entryName, new JSONObject());
                }
                JSONObject entry = jsonObject.getJSONObject(entryName);

                entry.put(fieldName+fieldAddName, fieldValue);
            }
        });

        System.out.println(jsonObject);
    }
}
