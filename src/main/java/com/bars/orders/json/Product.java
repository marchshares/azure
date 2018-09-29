package com.bars.orders.json;


import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONArray;
import org.json.JSONObject;

public class Product extends AbstractObject {

    private JSONArray options;

    protected Product(JSONObject head, ExecutionContext context) {
        super(head, context);

        if (head.has("options")) {
            this.options = head.getJSONArray("options");
        }
    }

    public String getName() {
        return head.getString("name");
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

    public Product createMainSetComponent(String componentName) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("isSet", true);
        jsonObject.put("mainSetComponent", true);

        jsonObject.put("name",      componentName);
        jsonObject.put("quantity",  getQuantity());
        jsonObject.put("price",     getPrice());
        jsonObject.put("amount",    getAmount());

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
