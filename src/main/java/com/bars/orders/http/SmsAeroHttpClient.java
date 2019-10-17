package com.bars.orders.http;

import com.bars.orders.http.common.SimpleHttpClient;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import static com.bars.orders.PropertiesHelper.getSystemProp;
import static com.bars.orders.Utils.checkPlaceHolders;

public class SmsAeroHttpClient extends SimpleHttpClient {

    private String smsAeroUrl;

    public SmsAeroHttpClient() {
        super();
        setRequestContentType("application/json");
        setAuthToken(getSystemProp("SmsAeroToken"));

        this.smsAeroUrl = getSystemProp("SmsAeroWebhookUrl");
    }

    public void sendSms(String msgPhone, String msgText) {
        checkPlaceHolders(msgText);

        String body = smsBodyJson
                .replace("{phone}",msgPhone)
                .replace("{text}",msgText);

        SimpleHttpResponse response = sendPost(smsAeroUrl, body);

        try {
            BasicDBObject jsonResponseBody = (BasicDBObject) JSON.parse(response.getContent());

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
