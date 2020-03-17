package com.bars.orders.product;

import com.bars.orders.TestHelper;
import com.bars.orders.product.desc.ProductDescManager;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ProductProcessorTest {

    private ProductProcessor processor;

    @Before
    public void setUp() throws Exception {
        TestHelper.setTestProperties();

        ProductDescManager descManager = new ProductDescManager();
        descManager.init();

        processor = new ProductProcessor();
        processor.setDescManager(descManager);
    }

    @Test
    public void testProd() {
        JSONObject jsonObject = new JSONObject("{\n" +
                "    \"amount\": \"1990\",\n" +
                "    \"quantity\": \"1\",\n" +
                "    \"price\": \"1990\",\n" +
                "    \"name\": \"Nanovessel\",\n" +
                "    \"sku\": \"WCCNNVSL  \"\n" +
                "   }");

        List<Product> res = processor.process(jsonObject);

        assertEquals(res.size(), 1);
        assertEquals(res.get(0).getName(), "Nanovessel");
        assertEquals(res.get(0).getSku(), "WCCNNVSL");
    }

    @Test
    public void testProdWithUnknownSku() {
        JSONObject jsonObject = new JSONObject("{\n" +
                "    \"amount\": \"1490\",\n" +
                "    \"quantity\": \"1\",\n" +
                "    \"price\": \"1490\",\n" +
                "    \"name\": \"Nanopresso Чехол\",\n" +
                "    \"sku\": \"WCCC_X\"\n" +
                "   }");

        List<Product> res = processor.process(jsonObject);

        assertEquals(res.size(), 1);
        assertEquals(res.get(0).getName(), "Nanopresso Чехол");
        assertEquals(res.get(0).getSku(), "WCCC_X");
    }

    @Test
    public void testProdWithColor() {
        JSONObject jsonObject = new JSONObject("{\n" +
                "    \"amount\": \"3000\",\n" +
                "    \"quantity\": \"1\",\n" +
                "    \"price\": \"3000\",\n" +
                "    \"name\": \"Nano\",\n" +
                "    \"options\": [{\n" +
                "     \"variant\": \"Черный\",\n" +
                "     \"option\": \"Цвет\"\n" +
                "    }],\n" +
                "    \"sku\": \"WCCN80\"\n" +
                "   }");

        List<Product> res = processor.process(jsonObject);

        assertEquals(res.size(), 1);
        assertEquals(res.get(0).getName(), "Nanopresso");
        assertEquals(res.get(0).getSku(), "WCCN80");
    }

    @Test
    public void testLineSet() {
        JSONObject jsonObject = new JSONObject("{\n" +
                "    \"amount\": \"7190\",\n" +
                "    \"quantity\": \"1\",\n" +
                "    \"price\": \"7190\",\n" +
                "    \"name\": \"Nanopresso+Ns-адаптер\",\n" +
                "    \"sku\": \"WCCN80+WCCNANS\"\n" +
                "   }");

        List<Product> res = processor.process(jsonObject);

        assertEquals(res.size(), 2);

        assertEquals(res.get(0).getName(), "Nanopresso");
        assertEquals(res.get(0).getSku(), "WCCN80");

        assertEquals(res.get(1).getName(), "NS-адаптер");
        assertEquals(res.get(1).getSku(), "WCCNANS");
    }

    @Test
    public void testSet() {
        JSONObject jsonObject = new JSONObject("{\n" +
                "    \"amount\": \"10220\",\n" +
                "    \"quantity\": \"1\",\n" +
                "    \"price\": \"10220\",\n" +
                "    \"name\": \"Nanopresso Set\",\n" +
                "    \"options\": [\n" +
                "     {\n" +
                "      \"variant\": \"Черный\",\n" +
                "      \"option\": \"Цвет\"\n" +
                "     },\n" +
                "     {\n" +
                "      \"variant\": \"NS-адаптер+Nanovessel\",\n" +
                "      \"option\": \"Аксессуар\"\n" +
                "     },\n" +
                "     {\n" +
                "      \"variant\": \"M-Чехол (для NS-адаптера)\",\n" +
                "      \"option\": \"Чехол\"\n" +
                "     }\n" +
                "    ],\n" +
                "    \"sku\": \"WCCSET\"\n" +
                "   }");

        List<Product> res = processor.process(jsonObject);

        assertEquals(4, res.size());

        assertEquals("Nanopresso", res.get(0).getName());
        assertEquals("WCCN80", res.get(0).getSku());

        assertEquals("NS-адаптер", res.get(1).getName());
        assertEquals("WCCNANS", res.get(1).getSku());

        assertEquals("Nanovessel", res.get(2).getName());
        assertEquals("WCCNNVSL", res.get(2).getSku());

        assertEquals("M-Чехол", res.get(3).getName());
        assertEquals("WCCNMC", res.get(3).getSku());
    }

    @Test
    public void testSetWithNanopressoCase() {
        JSONObject jsonObject = new JSONObject("{\n" +
                "    \"amount\": \"10220\",\n" +
                "    \"quantity\": \"1\",\n" +
                "    \"price\": \"10220\",\n" +
                "    \"name\": \"Nanopresso Set\",\n" +
                "    \"options\": [\n" +
                "     {\n" +
                "      \"variant\": \"Elements, Journey, Tattoo + Чехол (в комментарии)\",\n" +
                "      \"option\": \"Цвет\"\n" +
                "     },\n" +
                "     {\n" +
                "      \"variant\": \"NS-адаптер+Nanovessel\",\n" +
                "      \"option\": \"Аксессуар\"\n" +
                "     },\n" +
                "     {\n" +
                "      \"variant\": \"M-Чехол (для NS-адаптера)\",\n" +
                "      \"option\": \"Чехол\"\n" +
                "     }\n" +
                "    ],\n" +
                "    \"sku\": \"WCCSET\"\n" +
                "   }");

        List<Product> res = processor.process(jsonObject);

        assertEquals(4, res.size());

        assertEquals("Nanopresso X (elements, journey, tattoo + чехол)", res.get(0).getName());
        assertEquals("WCCN_X", res.get(0).getSku());

        assertEquals("NS-адаптер", res.get(1).getName());
        assertEquals("WCCNANS", res.get(1).getSku());

        assertEquals("Nanovessel", res.get(2).getName());
        assertEquals("WCCNNVSL", res.get(2).getSku());

        assertEquals("M-Чехол", res.get(3).getName());
        assertEquals("WCCNMC", res.get(3).getSku());
    }

}