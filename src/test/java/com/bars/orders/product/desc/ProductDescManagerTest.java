package com.bars.orders.product.desc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductDescManagerTest {

    private ProductDescManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new ProductDescManager();
        manager.init();
    }

    @Test
    public void shouldPrintFile() {
        manager.getAllDescriptions().forEach(System.out::println);
    }

    @Test
    public void shouldGetByName() {
        String name = "nanopresso patrol red";

        ProductDesc desc = manager.getDescBySearchName(name);

        assertEquals("WCCN81", desc.getSku());
        assertEquals("Nanopresso Patrol Red", desc.getName());
        assertEquals("красный", desc.getColor());
        assertEquals(null, desc.getModel());
        assertEquals(null, desc.getSize());
    }

    @Test
    public void shouldGetBySku() {
        String name = "m-чехол";

        ProductDesc desc = manager.getDescBySearchName(name);

        assertEquals("WCCNMC", desc.getSku());
        assertEquals("M-Чехол", desc.getName());
        assertEquals(null, desc.getColor());
        assertEquals(null, desc.getModel());
        assertEquals("m-чехол", desc.getSize());
    }
}