package com.bars.orders.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.bars.orders.PropertiesHelper.getSystemProp;

public class SimpleHttpClient {
    final Logger logger;

    private String zapierProductsUrl;

    String requestContentType;

    public SimpleHttpClient(Logger logger) {
        this.logger = logger;

        this.zapierProductsUrl = getSystemProp("ZapierProductsWebhookUrl");

        this.requestContentType = "application/json";
    }

    public void sendZapier(String body) {
        sendPost(zapierProductsUrl, body);
    }

    SimpleHttpResponse sendPost(String url, String body) {
        return sendPost(url, body, null);
    }

    SimpleHttpResponse sendPost(String url, String body, String encodedAuthToken) {
        if (url == null) {
            logger.warning("URL is null. Request wasn't send");
            return SimpleHttpResponse.createBad();
        }

        try {
            URLConnection con = new URL(url).openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            if (encodedAuthToken != null) {
                http.setRequestProperty("Authorization", "Basic " + encodedAuthToken);
            }
            byte[] out = body.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", requestContentType);
            http.setRequestProperty("charset", "utf-8");

            logger.info("connect to: " + url);
            logger.info("body: " + body);
            http.connect();

            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
                os.flush();

                String content = readFully(http);
                logger.info("responseContent: " + content);

                if (http.getResponseCode() == 200) {
                    logger.info("Received OK!");
                } else {
                    logger.log(Level.WARNING, "Received bad response code: " + http.getResponseCode() + ", msg: " + http.getResponseMessage());
                }

                return new SimpleHttpResponse(http.getResponseCode(), content);
            } finally {
                http.disconnect();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "ERROR msg: " + ex.getMessage(), ex);
            return SimpleHttpResponse.createBad();
        }

    }

    public static class SimpleHttpResponse {
        int responseCode;
        String content;

        public SimpleHttpResponse(int responseCode, String content) {
            this.responseCode = responseCode;
            this.content = content;
        }

        public static SimpleHttpResponse createBad() {
            return new SimpleHttpResponse(-1, null);
        }

        @Override
        public String toString() {
            return "SimpleHttpResponse{" +
                    "responseCode=" + responseCode +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    private String readFully(HttpURLConnection http) throws IOException {
        StringBuffer content = new StringBuffer();

        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        return content.toString();
    }


    String getEncodedToken(String authToken) {
        try {
            return Base64.getEncoder().encodeToString(authToken.getBytes("UTF-8"));
        } catch (Exception e) {
            logger.log(Level.WARNING, "cound't encode auth token " + authToken + ", cause " + e.getMessage());
            return null;
        }
    }
}
