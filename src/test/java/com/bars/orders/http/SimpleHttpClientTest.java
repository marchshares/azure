package com.bars.orders.http;

import com.bars.orders.TestHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.logging.Logger;

@Ignore
public class SimpleHttpClientTest {
    private SimpleHttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        TestHelper.setTestProperties();
        httpClient = new SimpleHttpClient(Logger.getGlobal());
    }

    @Test
    public void sendPost() {
        httpClient.sendZapier("\"test\": \"test\"");
    }

}