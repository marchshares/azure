package com.bars.orders.operations;


import com.bars.orders.FunctionEntryPoint;
import com.bars.orders.Utils;
import com.bars.orders.json.Order;
import com.bars.orders.json.Product;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.bars.orders.GlobalLogger.glogger;
import static com.bars.orders.operations.FieldsRemapper.BLACK_COLOR_NAME;
import static com.bars.orders.operations.FieldsRemapper.NANOPRESSO_NAME;
import static com.bars.orders.operations.FieldsRemapper.NANOPRESSO_PATROL_NAME;
import static com.google.common.collect.ImmutableMap.of;

public class SetSplitter {
    public static final String SET_NAME_MARK = "Set";

    public static final String NO_MARK = "Нет";
    public static final String ACCESSORY_NAME_MARK = "Аксессуар";
    public static final String CASE_NAME_MARK = "Чехол";

    public static final String NANO_SET_NAME = "Nanopresso Set";

    public SetSplitter() {
    }

    public void splitSets(Order order) {
        glogger.info("Split sets for " + order.getOrderId());
        List<Product> result = Lists.newArrayList();

        order.getProducts().forEach(product -> {
            String name = product.getName();

            if (StringUtils.isEmpty(name)) {
                glogger.log(Level.WARNING, "Product has empty name: " + product);
                return;
            }

            if (Utils.isContainsSubName(name, SET_NAME_MARK)) {
                result.addAll(processSet(product));
            } else {
                result.add(product);
            }
        });

        order.setProducts(result);
    }


    private List<Product> processSet(Product product) {
        String shortSetName = product.getName();

        List<Product> setComponents;
        switch (shortSetName) {
            case NANO_SET_NAME:
                setComponents = processNanopressoSet(product);
                break;

            default:
                glogger.log(Level.WARNING, shortSetName + " is unknown Set!");
                return Lists.newArrayList(product);
        }

        putSetName(shortSetName, setComponents);

        return setComponents;
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

    private List<Product> processNanopressoSet(Product setAsProduct) {
        String setName = setAsProduct.getName();
        glogger.info("Process " + setName);

        if (!setAsProduct.hasColor()) {
            glogger.warning(setName + " should has color");
            return Collections.emptyList();
        }

        String mainProductInSet = BLACK_COLOR_NAME.equals(setAsProduct.getColor())
                        ? NANOPRESSO_NAME
                        : NANOPRESSO_PATROL_NAME;
        glogger.info("Set: mainProduct: " + mainProductInSet);

        Product mainSetComponent = setAsProduct.createMainSetComponent(mainProductInSet);
        List<Product> result = Lists.newArrayList(mainSetComponent);

        JSONArray options = setAsProduct.getOptions();
        Utils.jsonArrayToStream(options)
                .map(obj -> (JSONObject) obj)
                .forEach((JSONObject option) -> {
                    String optionType = option.getString("option");
                    String variant = option.getString("variant");

                    if (StringUtils.isEmpty(variant)) {
                        glogger.log(Level.WARNING, "Variant is empty for option: " + Utils.toString(option));
                        return;
                    }
                    String variantTrim = variant.trim();

                    if (StringUtils.containsIgnoreCase(optionType, ACCESSORY_NAME_MARK) ||
                        StringUtils.containsIgnoreCase(optionType, CASE_NAME_MARK)) {
                        List<Product> subComponents = processSubComponents(setAsProduct, variantTrim);
                        result.addAll(subComponents);
                    }
                });

        return result;
    }

    private List<Product> processSubComponents(Product setAsProduct, String variant) {
        return Arrays.stream(variant.split("\\+"))
                .filter(componentName -> !NO_MARK.equalsIgnoreCase(componentName))
                .map(componentName -> {
                    glogger.info("Set: " + variant + " -> " + componentName);
                    return setAsProduct.createSetComponent(componentName);
                }).collect(Collectors.toList());
    }

}
