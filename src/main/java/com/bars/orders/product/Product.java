package com.bars.orders.product;


import com.bars.orders.json.AbstractObject;
import com.bars.orders.product.desc.ProductDesc;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.bars.orders.Utils.extTrim;
import static com.google.common.base.Strings.nullToEmpty;

public class Product extends AbstractObject {

    private final String originalName;

    public Product(JSONObject head) {
        super(head);

        this.originalName = head.getString("name");
        head.put("originalName", originalName);

        if (hasSku()) {
            setSku(extTrim(head.getString("sku")));
        }
    }

    public String getName() {
        return head.getString("name");
    }
    public void setName(String name) {
        head.put("name", name);
    }


    public void setSku(String sku) {
        head.put("sku", sku);
    }

    public boolean hasSku() {
        return head.has("sku");
    }

    public String getSku() {
        if (hasSku()){
            return head.getString("sku");
        }

        return null;
    }


    public void setColor(String color) {
        head.put("color", color);
    }

    public boolean hasColor() {
        return head.has("color");
    }

    public String getColor() {
        if (hasColor()){
            return head.getString("color");
        }

        return null;
    }


    public void setSize(String size) {
        head.put("size", size);
    }

    public boolean hasSize() {
        return head.has("size");
    }

    public String getSize() {
        if (hasSize()){
            return head.getString("size");
        }

        return null;
    }


    public void setModel(String model) {
        head.put("model", model);
    }

    public boolean hasModel() {
        return head.has("model");
    }

    public String getModel() {
        if (hasModel()){
            return head.getString("model");
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


    public boolean hasOptions() {
        return head.has("options");
    }

    public JSONArray getOptions() {
        if (hasOptions()) {
            return head.getJSONArray("options");
        }

        return null;
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

    public void setError(String errorMsg) {
        head.put("productError", errorMsg);
    }

    public Product createMainSetComponent(ProductDesc desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("isSet", true);
        jsonObject.put("mainSetComponent", true);

        jsonObject.put("sku",       desc.getSku());
        jsonObject.put("name",      desc.getName());

        jsonObject.put("quantity",  getQuantity());
        jsonObject.put("price",     getPrice());
        jsonObject.put("amount",    getAmount());

        if (hasColor()) {
            jsonObject.put("color", getColor());
        }

        if (hasModel()) {
            jsonObject.put("model", getModel());
        }

        if (hasSize()) {
            jsonObject.put("size", getSize());
        }

        return new Product(jsonObject);
    }

    public Product createSetComponent(ProductDesc desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("isSet", true);
        jsonObject.put("mainSetComponent", false);

        jsonObject.put("name",      desc.getName());
        jsonObject.put("sku",       desc.getSku());
        jsonObject.put("quantity",  getQuantity());
        jsonObject.put("price",     "0");
        jsonObject.put("amount",    "0");

        return new Product(jsonObject);
    }

    public Product createLineSetComponent(ProductDesc desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("isLineSet", true);

        jsonObject.put("name",      desc.getName());
        jsonObject.put("sku",       desc.getSku());
        jsonObject.put("quantity",  getQuantity());
        jsonObject.put("price",     "0");
        jsonObject.put("amount",    "0");

        return new Product(jsonObject);
    }

    public Product makeCopy() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("name",      getName());
        jsonObject.put("quantity",  getQuantity());
        jsonObject.put("price",     getPrice());
        jsonObject.put("amount",    getAmount());

        return new Product(jsonObject);
    }

    public String info() {
        return nullToEmpty(getSku()) + ":" + getName();
    }
}
