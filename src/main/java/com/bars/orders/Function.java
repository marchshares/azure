package com.bars.orders;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

import com.bars.orders.json.Order;
import com.bars.orders.operations.FieldsRemapper;
import com.bars.orders.operations.SetSplitter;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.oracle.jrockit.jfr.DataType.UTF8;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

//    public static final String POST_URL = "https://webhook.site/d230a41e-8ea7-4bde-9a9b-5a1515ddab98";
    public static final String POST_URL = "https://hooks.zapier.com/hooks/catch/3017336/lya2vy/";
    private Logger log;

    private Order order;

    /**
     * https://webhook.site/992e3ccc-3584-4835-bd08-12118ee0f2f0
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTrigger-Java
     * 2. curl {your host}/api/HttpTrigger-Java?name=HTTP%20Query
     */
    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws UnsupportedEncodingException {
        setLogger(context);
        log.info("Java HTTP trigger processed a request.");

        String body = request.getBody().orElse(null);
        if (body == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error: Empty body received").build();
        }

        String decodedBody = URLDecoder.decode(body, "UTF-8");

        order = new Order(decodedBody, context);
        log.info("Received order " + order.getOrderId());

        new SetSplitter(context).splitSets(order);
        new FieldsRemapper(context).remapDelivery(order);

        String body1 = order.toArrayJson();
//        String body1 = "{\"city\":\"Москва\"}";
        System.out.println(body1);

        sendPost2(POST_URL, body1);
//        sendPost(POST_URL, URLEncoder.encode(order.toArrayJson(), "UTF-8"));

        return request.createResponseBuilder(HttpStatus.OK).body("Done").build();
    }

    public void sendPost2(String url, String body) {
        try {
            URLConnection con = new URL(url).openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            byte[] out = body.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendPost(String url, String body) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            BasicHttpContext httpCtx = new BasicHttpContext();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(CONTENT_TYPE, "application/json;charset=utf-8");
//            httpPost.setHeader("content-length", "23");

            StringEntity entity = new StringEntity(body);

            httpPost.setEntity(entity);
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost, httpCtx);
            HttpEntity httpEntity = httpResponse.getEntity();

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200 && httpEntity != null) {
                log.info("Send POST request successful!");
                EntityUtils.consumeQuietly(httpEntity);

            } else {
                throw new IOException("client connection to " + url + " fail: no connection");
            }



        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setLogger(ExecutionContext context) {
        log = context.getLogger();
    }

    public Order getOrder() {
        return order;
    }
}
