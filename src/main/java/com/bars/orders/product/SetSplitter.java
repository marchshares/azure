package com.bars.orders.product;


import com.bars.orders.product.desc.ProductDesc;
import com.bars.orders.product.desc.ProductDescManager;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static com.bars.orders.GlobalLogger.glogger;
import static com.bars.orders.Utils.extTrimLower;
import static com.bars.orders.product.ProductProcessor.*;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Streams.stream;

public class SetSplitter {
    private ProductDescManager descManager;

    public SetSplitter() {
    }

    public void setDescManager(ProductDescManager descManager) {
        this.descManager = descManager;
    }

    public List<Product> processLineSet(Product inpurProduct) {
        List<Product> result = Lists.newArrayList();

        String[] skuies = inpurProduct.getSku().split("\\+");

        boolean isFirst = true;
        for (String itemSku : skuies) {
            ProductDesc desc = descManager.getDescBySku(itemSku.toUpperCase());

            if (desc == null) {
                throw new RuntimeException(inpurProduct.info() + " Not found desc for sku=" + itemSku);
            }

            String itemName = desc.getName();


            Product product;
            if (isFirst) {
                product = inpurProduct.createLineSetComponent(desc);
                isFirst = false;
            } else {
                product = inpurProduct.createLineSetComponent(desc);
            }

            glogger.info(inpurProduct.info() + ": added " + desc.info());

            result.add(product);
        }

        return result;
    }

    public List<Product> processSet(Product product) {
        String shortSetName = product.getName();

        List<Product> setComponents = processNanopressoSet(product);

        putSetName(shortSetName, setComponents);

        return setComponents;
    }

    private List<Product> processNanopressoSet(Product setAsProduct) {
        List<Product> result = Lists.newArrayList();

        JSONArray options = setAsProduct.getOptions();
        stream(options)
                .map(option -> (JSONObject) option)
                .filter(option -> option.has("option"))
                .forEach(option -> {
                    String optionType = extTrimLower(option.getString("option"));
                    String optionValue = extTrimLower(option.getString("variant"));

                    ProductDesc productDesc;
                    switch (optionType) {
                        case COLOR_OPTION_MARK:
                            productDesc = descManager.getNanopressoByColor(optionValue);

                            if (productDesc != null) {
                                result.add(setAsProduct.createMainSetComponent(productDesc));
                                glogger.info(setAsProduct.info() + ": by " + optionType + "=" + optionValue + " added " + productDesc.info());
                            } else {
                                throw new RuntimeException(setAsProduct.info() + " Not found Nanopresso with " + optionType + "=" + optionValue);
                            }

                            return;


                        case ACCESSORY_OPTION_MARK:
                        case CASE_OPTION_MARK:

                            Arrays.stream(optionValue.split("\\+"))
                                    .filter(componentName -> !NO_MARK.equals(componentName))
                                    .forEach(componentName -> {

                                        ProductDesc productDesc1 = descManager.getDescBySearchName(componentName);

                                        if (productDesc1 != null) {
                                            result.add(setAsProduct.createSetComponent(productDesc1));
                                            glogger.info(setAsProduct.info() + ": by " + optionType + "=" + componentName + "(" + optionValue + ")" + " added " + productDesc1.info());
                                        } else {
                                            throw new RuntimeException(setAsProduct.info() + " Not found " + optionType + "=" + componentName + "(" + optionValue + ")");
                                        }
                                    });

                            return;
                    }
                });

        return result;
    }

    private void putSetName(String setShortName, List<Product> setComponents) {
        List<String> collect = setComponents.stream()
                .map(Product::getName)
                .collect(Collectors.toList());

        String joinNames = String.join(", ", collect);

        String setName = setShortName + " (" + joinNames + ")";

        setComponents
                .forEach(component -> component.setSetName(setName));
    }
}
