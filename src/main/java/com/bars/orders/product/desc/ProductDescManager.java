package com.bars.orders.product.desc;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Maps;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

import static com.bars.orders.GlobalLogger.glogger;
import static com.bars.orders.Utils.extTrimLower;

public class ProductDescManager {

    public static final String PRODUCTS_DESC_FILENAME = "products-desc.csv";

    public static final String NANOPRESSO_SERIES_NAME = "nanopresso";

    public static final String NANOPRESSO_UNDEFINED_NAME = "Nanopresso X";
    public static final String NANOPRESSO_UNDEFINED_SKU = "WCCN_X";

    private final Map<String, ProductDesc> mapSku2desc = Maps.newHashMap();
    private final Map<String, ProductDesc> mapSearchName2desc = Maps.newHashMap();

    private boolean isInitted = false;

    public ProductDescManager() {
    }

    public void init() throws Exception {
        URL inputCsv = ClassLoader.getSystemClassLoader().getResource(PRODUCTS_DESC_FILENAME);

        if (inputCsv == null) {
            throw new Exception(PRODUCTS_DESC_FILENAME + " NOT FOUND in resources");
        }

        CsvMapper mapper = new CsvMapper();

        CsvSchema schema = mapper
                .schemaFor(ProductDesc.class)
                .withHeader()
                .withNullValue("");

        MappingIterator<ProductDesc> objectMappingIterator = mapper
                    .reader(schema)
                    .forType(ProductDesc.class)
                    .readValues(inputCsv.openStream());

        objectMappingIterator.forEachRemaining(desc -> {
            mapSku2desc.put(desc.getSku(), desc);
            mapSearchName2desc.put(extTrimLower(desc.getName()), desc);
        });

        isInitted = true;
        glogger.info("Load " + mapSku2desc.size() + " product descriptions");
    }

    public boolean isInitted() {
        return isInitted;
    }

    public ProductDesc getDescBySku(String sku) {
        return mapSku2desc.get(sku);
    }

    public ProductDesc getDescBySearchName(String searchName) {
        return mapSearchName2desc.get(searchName);
    }

    public String getSkuBySearchName(String searchName) {
        ProductDesc desc = getDescBySearchName(searchName);
        return desc == null ? null : desc.getSku();
    }

    public String getNameBySku(String sku) {
        ProductDesc desc = getDescBySku(sku);
        if (desc != null) {
            return desc.getName();
        }

        return null;
    }

    public Collection<ProductDesc> getAllDescriptions() {
        return mapSku2desc.values();
    }

    public ProductDesc getNanopressoByColor(String color) {
        for (ProductDesc desc : getAllDescriptions()) {
            if (NANOPRESSO_SERIES_NAME.equals(desc.getSeries()) && color.equals(desc.getColor())) {
                return desc;
            }
        }

        return null;
    }

    public ProductDesc createUnknownNanopresso(String color) {
        ProductDesc desc = new ProductDesc();

        desc.setName(NANOPRESSO_UNDEFINED_NAME + " (" + color + ")");
        desc.setSku(NANOPRESSO_UNDEFINED_SKU);
        desc.setSeries(NANOPRESSO_SERIES_NAME);

        return desc;
    }
}
