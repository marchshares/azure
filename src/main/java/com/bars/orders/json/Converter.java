package com.bars.orders.json;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Converter {
    private static final Set<String> arrayNames = new HashSet<>();
    static {
        arrayNames.add("products");
        arrayNames.add("options");
    }

    public static JSONObject bodyLineToJsonObject(String bodyLine) {
        JSONObject jsonObject = new JSONObject();

        String[] params = bodyLine.split("&");

        Arrays.stream(params).forEach(param -> {
            String[] split = param.split("=", 2);

            String name = split[0];
            String value = split[1];

            func(jsonObject, name, value);
        });

        convertArrays(jsonObject);

        return jsonObject;
    }

    private static void func(JSONObject jsonObject, String name, String value) {
        if (! name.contains("[")) {
            jsonObject.put(name, value);
        } else {
            String[] split1 = name.split("(\\[|\\])", 3);

            String entryName = split1[0];
            String fieldName = split1[1] + split1[2];

            if (! jsonObject.has(entryName)) {
                jsonObject.put(entryName, new JSONObject());
            }

            JSONObject entry = jsonObject.getJSONObject(entryName);

            func(entry, fieldName, value);
        }
    }

    private static void convertArrays(JSONObject jsonObject) {
        jsonObject.names().forEach(nameObj -> {
            String name = (String) nameObj;
            Object value = jsonObject.get(name);

            if (value instanceof JSONObject) {
                JSONObject jsonObjectValue = (JSONObject) value;

                convertArrays(jsonObjectValue);

                if (arrayNames.contains(name)) {
                    jsonObject.put(name, convertEntryToArray(jsonObjectValue));
                }
            }
        });
    }

    private static JSONArray convertEntryToArray(JSONObject entryArray) {
        JSONArray jsonArray = new JSONArray();

        entryArray.names()
                .forEach(name -> {
                    String idx = (String) name;
                    jsonArray.put(Integer.parseInt(idx), entryArray.getJSONObject(idx));
                });

        return jsonArray;
    }

}
