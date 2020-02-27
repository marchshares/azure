package com.bars.orders.product;

import com.bars.orders.product.desc.ProductDescManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.bars.orders.GlobalLogger.glogger;
import static com.bars.orders.Utils.extTrimLower;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Streams.stream;

public class ProductProcessor {
    public static final String NO_MARK = "нет";
    public static final String COLOR_OPTION_MARK = "цвет";
    public static final String MODEL_OPTION_MARK = "модель";
    public static final String SIZE_OPTION_MARK  = "размер";
    public static final String ACCESSORY_OPTION_MARK  = "аксессуар";
    public static final String CASE_OPTION_MARK = "чехол";

    public static final String SKU_SET = "WCCSET";

    private SetSplitter setSplitter;
    private ProductDescManager descManager;

    private Product inputProduct;

    public ProductProcessor() {
        this.setSplitter = new SetSplitter();
    }

    public void setDescManager(ProductDescManager descManager) {
        this.descManager = descManager;
        this.setSplitter.setDescManager(descManager);
    }

    public List<Product> process(JSONObject jsonObject) {
        glogger.info("Start >>>>>");
        this.inputProduct = new Product(jsonObject);

        try {
//            if (inputProduct.hasOptions()) {
//                extractOptions();
//            }

            if (!inputProduct.hasSku()) {
                glogger.warning(inputProduct.getName() + ": not SKU");
                return Collections.singletonList(inputProduct);
            }

            String sku = inputProduct.getSku();
            String canonicalName = descManager.getNameBySku(sku);
            if (canonicalName != null) {
                inputProduct.setName(canonicalName);
            }

            if (sku.equals(SKU_SET)) {
                return processSet();
            } else if (sku.contains("+")) {
                return processLineSet();
            } else {
                return processSimpleProduct();
            }

        } catch (Exception e) {
            glogger.warning("Couldn't extract product(s) from json. Cause: " + e.getMessage());
            e.printStackTrace();
            inputProduct.setError("ERROR MSG: " + e.getMessage() + "\n" + "JSON: " + inputProduct.toString());
            return Collections.singletonList(inputProduct);
        } finally {
            glogger.info("End <<<<<");
        }
    }

    private List<Product> processSet() {
        glogger.info(inputProduct.info() + " Set: found");
        return setSplitter.processSet(inputProduct);
    }

    private List<Product> processLineSet() {
        glogger.info(inputProduct.info() + " Line Set: found");
        return setSplitter.processLineSet(inputProduct);
    }

    private List<Product> processSimpleProduct() {
        glogger.info(inputProduct.info() + " found");
        return Collections.singletonList(inputProduct);
    }

    public static final Map<String, String> mapRUColorOnEngColor = of(
            "Желтый",      "Yellow",
            "Оранжевый",   "Orange",
            "Красный",     "Red",
            "Черный",      "Black"
    );

    private void extractOptions() {
        JSONArray options = inputProduct.getOptions();

        stream(options)
                .map(option -> (JSONObject) option)
                .filter(option -> option.has("option"))
                .forEach(option -> {
                    String optionSimplified = extTrimLower(option.getString("option"));

                    switch (optionSimplified) {
                        case COLOR_OPTION_MARK:
                            String ruColor = option.getString("variant");
                            String color = mapRUColorOnEngColor.get(ruColor);
                            if (color != null) {
                                inputProduct.setColor(color);
                            }
                            return;

                        case SIZE_OPTION_MARK:
                            String size = option.getString("variant");
                            if (size != null) {
                                inputProduct.setSize(size);
                            }
                            return;

                        case MODEL_OPTION_MARK:
                            String model = option.getString("variant");
                            if (model != null) {
                                inputProduct.setModel(model);
                            }
                            return;
                    }
                });
    }
}
