package com.bars.orders.http.common;

import com.bars.orders.TestHelper;
import com.microsoft.azure.functions.ExecutionContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.bars.orders.PropertiesHelper.getSystemProp;

@Ignore
public class SimpleHttpClientTest {
    private SimpleHttpClient httpClient;

    private String testUrl;

    @Before
    public void setUp() throws Exception {
        TestHelper.setTestProperties();

        testUrl = getSystemProp("TestWebhookUrl");
        httpClient = new SimpleHttpClient();
    }

    @Test
    public void sendPost() {
        String testBody = "\"test\": \"test\"";
        httpClient.sendPost(testUrl, testBody);
    }

}