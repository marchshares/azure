package com.bars.orders.mongo;

import com.bars.orders.TestHelper;
import com.bars.orders.order.Order;
import com.bars.orders.order.OrderProcessorTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.bars.orders.GlobalLogger.glogger;
import static org.junit.Assert.assertTrue;


@Ignore
public class MyMongoClientTest {
    private MyMongoClient myMongoClient;

    @Before
    public void setUp() throws Exception {
        TestHelper.setTestProperties();

        myMongoClient = new MyMongoClient();
        myMongoClient.setOrdersCollection("test-orders");

        myMongoClient.init();
    }

    @Test
    public void shouldPrintAllOrderIds() {
        List<String> orderIds = myMongoClient.getOrderIds();
        glogger.info(orderIds.toString());
    }

    @Test
    public void testInsert() {
        Order testOrder = OrderProcessorTest.createTestOrder();

        myMongoClient.storeOrder(testOrder);
        List<String> orderIds = myMongoClient.getOrderIds();

        assertTrue(orderIds.contains(testOrder.getOrderId()));
    }
}