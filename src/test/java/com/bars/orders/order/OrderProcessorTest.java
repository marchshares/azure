package com.bars.orders.order;

import com.bars.orders.TestHelper;
import com.bars.orders.json.Converter;
import com.bars.orders.order.Order;
import com.bars.orders.order.OrderProcessor;
import com.bars.orders.product.desc.ProductDescManager;
import com.google.common.collect.Maps;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderProcessorTest {

    private static final int MIN_ORDER_ID = 1_000_000_000;
    private OrderProcessor orderProcessor;

    @Before
    public void setUp() throws Exception {
        TestHelper.setTestProperties();

        ProductDescManager descManager = new ProductDescManager();
        descManager.init();

        orderProcessor = new OrderProcessor();
        orderProcessor.setDescManager(descManager);
    }

    @Test
    public void shouldProcessProducts() {
        Order testOrder = createTestOrder();

        orderProcessor.process(testOrder, Maps.newHashMap());

        assertNotNull(testOrder.getOrderId());
        assertEquals(2, testOrder.getProducts().size());
        assertEquals("Minipresso GR", testOrder.getProducts().get(0).getName());
        assertEquals("Minipresso GR, Nanopresso Patrol Orange", testOrder.getOrderDescription());

        System.out.println(testOrder.toString());
    }

    public static Order createTestOrder() {
        String decodedBody = "Form=Cart&Name=Имя Фамилия&Phone=+7 (999) 111-2233&Email=email@yandex.ru&deliveryType=Доставка курьером (Москва, Спб) 300 руб. = 300&city=Москва&deliveryAddress=адрес такой-то&comment=коммент&payment[sys]=none&payment[systranid]=0&payment[orderid]=1557604691&payment[products][0][name]=Wacaco Minipresso GR&payment[products][0][sku]=WCCMP&payment[products][0][quantity]=1&payment[products][0][amount]=4500&payment[products][0][price]=4500&payment[products][1][name]=Nanopresso Patrol Orange&payment[products][1][sku]=WCCN84&payment[products][1][quantity]=1&payment[products][1][amount]=5200&payment[products][1][price]=5200&payment[amount]=24450&formid=form42581206&formname=Cart";
        JSONObject orderJson = Converter.bodyLineToJsonObject(decodedBody);

        Order testOrder = new Order(orderJson);

        // hack to avoid drop the same orderId in Mongo
        String newOrderId = Long.toString(MIN_ORDER_ID + Math.abs(new Random().nextInt(MIN_ORDER_ID)));
        testOrder.setOrderId(newOrderId);

        return testOrder;
    }
}