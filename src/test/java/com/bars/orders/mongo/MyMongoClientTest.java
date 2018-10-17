package com.bars.orders.mongo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.logging.Logger;


@Ignore
public class MyMongoClientTest {

    public static final String TEST_MONGO_URI = "";

    private MyMongoClient myMongoClient;

    @Before
    public void setUp() throws Exception {
        myMongoClient = new MyMongoClient(Logger.getGlobal());
        myMongoClient.setUri(TEST_MONGO_URI);
        myMongoClient.init();
    }

    @Test
    public void name() {
        List<String> orderIds = myMongoClient.getOrderIds();
        System.out.println(orderIds);
    }
}