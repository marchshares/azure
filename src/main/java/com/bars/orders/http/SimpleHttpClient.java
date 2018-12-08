package com.bars.orders.http;

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
    private final Logger logger;

    private String zapierProductsUrl;

    private String smsAeroAuthTokenEncoded;
    private String smsAeroUrl;

    public SimpleHttpClient(Logger logger) {
        this.logger = logger;

        this.zapierProductsUrl = getSystemProp("ZapierProductsWebhookUrl");

        this.smsAeroUrl = getSystemProp("SmsAeroWebhookUrl");
        this.smsAeroAuthTokenEncoded = getEncodedToken(getSystemProp("SmsAeroToken"));
    }

    public void sendZapier(String body) {
        sendPost(zapierProductsUrl, body, null);
    }

    public void sendSmsAero(String body) {
        sendPost(smsAeroUrl, body, smsAeroAuthTokenEncoded);
    }

    private void sendPost(String url, String body, String encodedAuthToken) {
        if (url == null) {
            logger.warning("URL is null. Request wasn't send");
            return;
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
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("charset", "utf-8");

            logger.info("connect to: " + url);
            logger.info("body: " + body);
            http.connect();

            OutputStream os = null;
            try {
                os = http.getOutputStream();
                os.write(out);
                os.flush();

                if (http.getResponseCode() == 200) {
                    logger.info("Received OK!");
                } else {
                    logger.log(Level.WARNING, "Received bad response code: " + http.getResponseCode() + ", msg: " + http.getResponseMessage());
                }
            } finally {
                if (os != null) {
                    os.close();
                }

                http.disconnect();
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "ERROR msg: " + ex.getMessage(), ex);
        }

        logger.info("POST request has been finished");
    }

    private String getEncodedToken(String authToken) {
        try {
            return Base64.getEncoder().encodeToString(authToken.getBytes("UTF-8"));
        } catch (Exception e) {
            logger.log(Level.WARNING, "cound't encode auth token " + authToken + ", cause " + e.getMessage());
            return null;
        }
    }
}
