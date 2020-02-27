package com.bars.orders.product.desc;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static com.google.common.base.Strings.nullToEmpty;

@JsonPropertyOrder({"sku", "name", "series", "color", "size", "model"})
public class ProductDesc {
    private String sku;
    private String name;
    private String series;
    private String color;
    private String size;
    private String model;

    public ProductDesc() {
    }

    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSeries() {
        return series;
    }
    public void setSeries(String series) {
        this.series = series;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "ProductDesc{" +
                "sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", series='" + series + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", model='" + model + '\'' +
                '}';
    }

    public String info() {
        return getSku() + ":" + getName();
    }
}
