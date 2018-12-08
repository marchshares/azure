package com.bars.orders.json;


import com.google.common.collect.Streams;
import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.bars.orders.operations.FieldsRemapper.mapRUColorOnEngColor;
import static com.bars.orders.operations.SetSplitter.COLOR_NAME_MARK;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class Product extends AbstractObject {

    private JSONArray options;

    protected Product(JSONObject head, ExecutionContext context) {
        super(head, context);

        if (head.has("options")) {
            this.options = head.getJSONArray("options");

            Streams.stream(options)
                    .map(option -> (JSONObject) option)
                    .filter(option -> option.has("option") && option.getString("option").equals(COLOR_NAME_MARK))
                    .map(option -> option.getString("variant"))
                    .map(mapRUColorOnEngColor::get)
                    .findFirst()
                    .ifPresent(color -> head.put("color", color));
        }

        if (containsIgnoreCase(getName(), "Чехол")) {
            setIsCase(true);
        }
    }

    public String getName() {
        return head.getString("name");
    }

    public void setName(String name) {
        head.put("name", name);
    }

    public boolean isCase() {
        return head.has("isCase") && head.getBoolean("isCase");
    }

    public void setIsCase(boolean isCase) {
        head.put("isCase", isCase);
    }

    public boolean hasColor() {
        return head.has("color");
    }

    public String getColor() {
        if (hasColor()) {
            return head.getString("color");
        }

        return null;
    }

    public String getAmount() {
        return head.getString("amount");
    }

    public String getQuantity() {
        return head.getString("quantity");
    }

    public String getPrice() {
        return head.getString("price");
    }

    public JSONArray getOptions() {
        return options;
    }

    public boolean isSetComponent() {
        return head.has("isSet") && head.getBoolean("isSet");
    }

    public boolean isMainSetComponent() {
        return head.has("mainSetComponent") && head.getBoolean("mainSetComponent");
    }

    public void setSetName(String setName) {
        head.put("setName", setName);
    }

    public String getSetName() {
        return head.has("setName") ? head.getString("setName") : "";
    }

    public Product createMainSetComponent(String componentName) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("isSet", true);
        jsonObject.put("mainSetComponent", true);

        jsonObject.put("name",      componentName);
        jsonObject.put("quantity",  getQuantity());
        jsonObject.put("price",     getPrice());
        jsonObject.put("amount",    getAmount());

        if (hasColor()) {
            jsonObject.put("color", getColor());
        }

        return new Product(jsonObject, context);
    }

    public Product createSetComponent(String componentName) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("isSet", true);
        jsonObject.put("mainSetComponent", false);

        jsonObject.put("name",      componentName);
        jsonObject.put("quantity",  getQuantity());
        jsonObject.put("price",     "0");
        jsonObject.put("amount",    "0");

        return new Product(jsonObject, context);
    }

    public Product makeCopy() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("name",      getName());
        jsonObject.put("quantity",  getQuantity());
        jsonObject.put("price",     getPrice());
        jsonObject.put("amount",    getAmount());

        return new Product(jsonObject, context);
    }
}
