package com.bars.orders.http;

import com.microsoft.azure.functions.ExecutionContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@Ignore
public class SimpleHttpClientTest {

    private SimpleHttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        httpClient = new SimpleHttpClient(Logger.getGlobal());
    }

    @Test
    public void sendPost() {
        String url = "https://gate.smsaero.ru/v2/sms/send";
        String body = "{\n" +
                "  \"number\" : \"79160709365\",\n" +
                "  \"text\" : \"youtextXXX\",\n" +
                "  \"sign\" : \"8Bars\",\n" +
                "  \"channel\" : \"DIRECT\"\n" +
                "}";

        httpClient.sendPost(url, body);
    }

    public static void main(String[] args) throws ClientProtocolException, IOException {

        String encoding = Base64.getEncoder().encodeToString("hello@8bars.ru:Yof4oaIJVrL0y8AioX5sNQr8U".getBytes("UTF-8"));

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost("https://gate.smsaero.ru/v2/sms/testsend");
        post.setHeader("Authorization", "Basic " + encoding);
        List nameValuePairs = new ArrayList(1);
        nameValuePairs.add(new BasicNameValuePair("number", "79160709365")); //you can as many name value pair as you want in the list.
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
        }
    }
}