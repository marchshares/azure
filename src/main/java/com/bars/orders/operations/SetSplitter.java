package com.bars.orders.operations;


import com.bars.orders.Utils;
import com.bars.orders.json.Order;
import com.bars.orders.json.Product;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.microsoft.azure.functions.ExecutionContext;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;

public class SetSplitter {
    public static final String NANO_GREY_SET_NAME = "Nanopresso Set";
    public static final String NANO_PATROL_SET_NAME = "Nanopresso Patrol Set";

    public static final String SET_NAME_MARK = "Set";

    public static final String ACCESSORY_NAME_MARK = "Аксессуар";
    public static final String COLOR_NAME_MARK = "Цвет";

    public static final String NS_ADAPTER_NAME = "NS-адаптер";
    public static final String BARISTA_KIT_NAME = "Barista Kit";

    public static final Set<String> NANO_SET_ACCESSORIES = Sets.newHashSet(NS_ADAPTER_NAME, BARISTA_KIT_NAME);

    public static final Map<String, String> mapRUColorOnPatrolName = of(
            "Желтый",      "Nanopresso Patrol Yellow",
            "Оранжевый",   "Nanopresso Patrol Orange",
            "Красный",     "Nanopresso Patrol Red",
            "Темно-серый", "Nanopresso"
    );
    public static final String NANO_GREY_NAME = "Nanopresso и чехол";

    private final Logger log;

    public SetSplitter(ExecutionContext context) {
        this.log = context.getLogger();
    }

    public void splitSets(Order order) {
        log.info("Split sets for " + order.getOrderId());
        List<Product> result = Lists.newArrayList();

        order.getProducts().forEach(product -> {
            String name = product.getName();

            if (StringUtils.isEmpty(name)) {
                log.log(Level.WARNING, "Product has empty name: " + product);
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
            case NANO_GREY_SET_NAME:
                setComponents = processNanoGreySet(product);
                break;

            case NANO_PATROL_SET_NAME:
                setComponents = processNanoPatrolSet(product);
                break;

            default:
                log.log(Level.WARNING, shortSetName + " is unknown Set!");
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

    private List<Product> processNanoGreySet(Product setAsProduct) {
        String setName = setAsProduct.getName();
        log.info("Process " + setName);

        Product nanoGreyComponent = setAsProduct.createMainSetComponent(NANO_GREY_NAME);

        // put main component
        List<Product> result = Lists.newArrayList(nanoGreyComponent);

        JSONArray options = setAsProduct.getOptions();
        Utils.jsonArrayToStream(options)
                .map(obj -> (JSONObject) obj)
                .forEach((JSONObject option) -> {
                    String optionType = option.getString("option");
                    String variant = option.getString("variant");

                    if (StringUtils.isEmpty(variant)) {
                        log.log(Level.WARNING, "Variant is empty for option: " + Utils.toString(option));
                        return;
                    }

                    if (Utils.isContainsSubName(optionType, ACCESSORY_NAME_MARK)) {
                        List<Product> subComponents = processAccessories(NANO_SET_ACCESSORIES, setAsProduct, variant);
                        result.addAll(subComponents);

                    } else {
                        log.log(Level.WARNING, "Unknown " + setName + " option: " + Utils.toString(option));
                    }

                });

        return result;
    }

    private List<Product> processNanoPatrolSet(Product setAsProduct) {
        String setName = setAsProduct.getName();
        log.info("Process " + setName);

        List<Product> result = Lists.newArrayList();

        JSONArray options = setAsProduct.getOptions();
        Utils.jsonArrayToStream(options)
                .map(obj -> (JSONObject) obj)
                .forEach((JSONObject option) -> {
                    String optionType = option.getString("option");
                    String variant = option.getString("variant");

                    if (StringUtils.isEmpty(variant)) {
                        log.log(Level.WARNING, "Variant is empty for option: " + Utils.toString(option));
                        return;
                    }

                    if (Utils.isContainsSubName(optionType, COLOR_NAME_MARK)) {
                        String nanoPatrolName = mapRUColorOnPatrolName.get(variant);

                        log.info(variant + " -> " + nanoPatrolName);
                        Product nanoPatrolComponent = setAsProduct.createMainSetComponent(nanoPatrolName);

                        // put main component
                        result.add(nanoPatrolComponent);

                    } else if (Utils.isContainsSubName(optionType, ACCESSORY_NAME_MARK)) {
                        List<Product> subComponents = processAccessories(NANO_SET_ACCESSORIES, setAsProduct, variant);
                        result.addAll(subComponents);

                    } else {
                        log.log(Level.WARNING, "Unknown " + setName + " option: " + Utils.toString(option));
                    }

                });

        return result;
    }

    private List<Product> processAccessories(Set<String> possibleAccessories, Product setAsProduct, String variant) {
        return possibleAccessories.stream()
                .filter(variant::contains)
                .map(componentName -> {
                    log.info(variant + " -> " + componentName);
                    return setAsProduct.createSetComponent(componentName);
                }).collect(Collectors.toList());
    }

}
