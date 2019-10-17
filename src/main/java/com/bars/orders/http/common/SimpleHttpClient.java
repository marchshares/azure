package com.bars.orders.http.common;

import com.bars.orders.FunctionEntryPoint;

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

import static com.bars.orders.GlobalLogger.glogger;

public class SimpleHttpClient {
    private String requestContentType;
    private String authTokenEncoded;

    public SimpleHttpClient() {
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public void setAuthToken(String authToken) {
        this.authTokenEncoded = getEncodedToken(authToken);
    }

    public SimpleHttpResponse sendPost(String url, String body) {
        if (url == null) {
            glogger.warning("URL is null. Request wasn't send");
            return SimpleHttpResponse.createBad();
        }

        try {
            URLConnection con = new URL(url).openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            byte[] out = body.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);

            if (requestContentType != null) {
                http.setRequestProperty("Content-Type", requestContentType);
            }
            if (authTokenEncoded != null) {
                http.setRequestProperty("Authorization", "Basic " + authTokenEncoded);
            }
            http.setRequestProperty("charset", "utf-8");

            glogger.info("connect to: " + url);
            glogger.info("body: " + body);
            http.connect();

            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
                os.flush();

                String content = readFully(http);
                glogger.info("responseContent: " + content);

                if (http.getResponseCode() == 200) {
                    glogger.info("Received OK!");
                } else {
                    glogger.log(Level.WARNING, "Received bad response code: " + http.getResponseCode() + ", msg: " + http.getResponseMessage());
                }

                return new SimpleHttpResponse(http.getResponseCode(), content);
            } finally {
                http.disconnect();
            }
        } catch (Exception ex) {
            glogger.log(Level.WARNING, "ERROR msg: " + ex.getMessage(), ex);
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

        public String getContent() {
            return content;
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


    public String getEncodedToken(String authToken) {
        try {

            return Base64.getEncoder().encodeToString(authToken.getBytes("UTF-8"));
        } catch (Exception e) {
            glogger.log(Level.WARNING, "cound't encode auth token " + authToken + ", cause " + e.getMessage());
            return null;
        }
    }
}
