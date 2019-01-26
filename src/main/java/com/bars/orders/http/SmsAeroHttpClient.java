package com.bars.orders.http;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.json.JSONObject;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.logging.Logger;

import static com.bars.orders.PropertiesHelper.getSystemProp;
import static com.bars.orders.Utils.checkPlaceHolders;

public class SmsAeroHttpClient extends SimpleHttpClient {

    private String smsAeroUrl;
    private String smsAeroAuthTokenEncoded;

    public SmsAeroHttpClient(Logger logger) {
        super(logger);

        this.smsAeroUrl = getSystemProp("SmsAeroWebhookUrl");
        String smsAeroToken = getSystemProp("SmsAeroToken");
        if (smsAeroToken != null) {
            this.smsAeroAuthTokenEncoded = getEncodedToken(smsAeroToken);
        }
    }

    public void setSmsAeroUrl(String smsAeroUrl) {
        this.smsAeroUrl = smsAeroUrl;
    }


    public void sendSms(String msgPhone, String msgText) {
        checkPlaceHolders(msgText);

        String body = smsBodyJson
                .replace("{phone}",msgPhone)
                .replace("{text}",msgText);

        SimpleHttpResponse response = sendPost(smsAeroUrl, body, smsAeroAuthTokenEncoded);

        try {
            BasicDBObject jsonResponseBody = (BasicDBObject) JSON.parse(response.content);

            boolean success = jsonResponseBody.getBoolean("success");

            if (!success) {
                throw new RuntimeException("Unsuccessful response for smsAero: " + response);
            }

        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse SmsAero response: " + response, e);
        }
    }

    private String smsBodyJson = "{\n" +
            "  \"number\" : \"{phone}\",\n" +
            "  \"text\" : \"{text}\",\n" +
            "  \"sign\" : \"8Bars\",\n" +
            "  \"channel\" : \"DIRECT\"\n" +
            "}";
}
