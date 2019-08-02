package com.bars.orders.json;


import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.bars.orders.Utils.extTrim;
import static com.bars.orders.operations.FieldsRemapper.BLACK_COLOR_NAME;
import static com.bars.orders.operations.FieldsRemapper.mapProductNames;
import static com.bars.orders.operations.FieldsRemapper.mapRUColorOnEngColor;
import static com.google.common.collect.Streams.*;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class Product extends AbstractObject {
    public static final String COLOR_OPTION_MARK = "Цвет";
    public static final String MODEL_OPTION_MARK = "Модель";
    public static final String SIZE_OPTION_MARK  = "Размер";

    private final String originalName;

    protected JSONArray options;

    private boolean isSet;
    private boolean isMinipresso;
    private boolean isNanopresso;
    private boolean isCase;
    private boolean isAccessory;

    protected Product(JSONObject head, ExecutionContext context) {
        super(head, context);
        this.originalName = head.getString("name");
        head.put("originalName", originalName);

        detectProduct();

        if (head.has("options")) {
            this.options = head.getJSONArray("options");
            exctractOptions(options);
        }

        if (!isSet) {
            remapName();
        }
    }

    public String getName() {
        return head.getString("name");
    }

    public void setName(String name) {
        head.put("name", name);
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

    public boolean hasSize() {
        return head.has("size");
    }

    public String getSize() {
        if (hasSize()) {
            return head.getString("size");
        }

        return null;
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

    private void exctractOptions(JSONArray options) {
        stream(options)
                .map(option -> (JSONObject) option)
                .filter(option -> option.has("option"))
                .forEach(option -> {
                    switch (option.getString("option")) {
                        case COLOR_OPTION_MARK:
                            String ruColor = option.getString("variant");
                            String color = mapRUColorOnEngColor.get(ruColor);
                            if (color != null) {
                                head.put("color", color);
                            }
                            return;

                        case SIZE_OPTION_MARK:
                            String size = option.getString("variant");
                            if (size != null) {
                                head.put("size", size);
                            }
                            return;

                        case MODEL_OPTION_MARK:
                            String model = option.getString("variant");
                            if (model != null) {
                                head.put("model", model);
                            }
                            return;

                    }
                });
    }

    private void remapName() {
        String remappedName = mapProductNames.get(originalName.toLowerCase());

        if (remappedName == null) {
            remappedName = extTrim(originalName
                    .replaceAll("(?i)wacaco", "")
                    .replaceAll("(?i)для nanopresso", ""));

            if (!isNanopresso) {
                remappedName = remappedName
                        .replaceAll("(?i)nanopresso", "")
                        .trim();
            }

            if (isMinipresso && hasModel()) {
                remappedName = remappedName + " " + getModel();
            }

            if (isNanopresso && hasColor()) {
                if (!BLACK_COLOR_NAME.equals(getColor())) {
                    remappedName = remappedName + " " + getColor();
                }
            }

            if (isAccessory) {
                //default is enough
            }

            if (isCase) {
                remappedName = hasSize() ? getSize() : remappedName;
            }

            remappedName = extTrim(remappedName
                    .replaceAll("\\(.*\\)", ""));
        }

        if (!originalName.equals(remappedName)) {
            log.info("Remap name: " + originalName + " -> " + remappedName);
            setName(remappedName);
        }
    }

    private void detectProduct() {
        String simplyfiedName = extTrim(originalName
                .replaceAll("(?i)wacaco", ""));

        this.isSet = containsIgnoreCase(simplyfiedName, "Set");
        this.isMinipresso = containsIgnoreCase(simplyfiedName, "Minipresso");
        this.isNanopresso =
                containsIgnoreCase(simplyfiedName, "Nanopresso Patrol") ||
                containsIgnoreCase(simplyfiedName, "Nanopresso Tattoo") ||
                equalsIgnoreCase(simplyfiedName, "Nanopresso");

        this.isCase = containsIgnoreCase(simplyfiedName, "Чехол");
        this.isAccessory = containsIgnoreCase(simplyfiedName, "Barista Kit") ||
                containsIgnoreCase(simplyfiedName, "NS-адаптер");
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

        if (hasModel()) {
            jsonObject.put("model", getModel());
        }

        if (hasSize()) {
            jsonObject.put("size", getSize());
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
