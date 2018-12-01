package com.bars.orders.http;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleHttpClient {

    private final Logger logger;

    public SimpleHttpClient(Logger logger) {
        this.logger = logger;
    }

    public void sendPost(String url, String body) {
        if (url == null) {
            logger.warning("URL is null. Request wasn't send");
            return;
        }

        try {
            String encoding = Base64.getEncoder().encodeToString("hello@8bars.ru:Yof4oaIJVrL0y8AioX5sNQr8U".getBytes("UTF-8"));
            URLConnection con = new URL(url).openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty  ("Authorization", "Basic " + encoding);
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
}
