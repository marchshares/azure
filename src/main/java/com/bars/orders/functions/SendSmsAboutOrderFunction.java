package com.bars.orders.functions;

import com.bars.orders.http.CdekHttpClient;
import com.bars.orders.http.SmsAeroHttpClient;
import com.bars.orders.mongo.MyMongoClient;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bars.orders.Utils.checkGood;
import static com.bars.orders.Utils.checkPlaceHolders;

public class SendSmsAboutOrderFunction extends AbstractFunction {
    public static final String YES = "yes";
    private final SmsAeroHttpClient smsAeroHttpClient;

    public SendSmsAboutOrderFunction(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        super(request, context);

        this.smsAeroHttpClient = new SmsAeroHttpClient(logger);
    }

    @Override
    String processBody(String body) throws Exception{
        BasicDBObject jsonBody = (BasicDBObject) JSON.parse(body);

        String paymentId = jsonBody.getString("paymentId");
        String phone = jsonBody.getString("phone");
        String sendSmsAboutOrder = jsonBody.getString("sendSmsAboutOrder");
        String msgTemplate = jsonBody.getString("msgTemplate");

        if(! YES.equals(sendSmsAboutOrder)) {
            String msg = "sendSmsAboutOrder=" + sendSmsAboutOrder + ", skip sending";
            logger.log(Level.INFO, msg);
            return msg;
        }

        String msgText = msgTemplate.replace("${paymentId}", paymentId);

        smsAeroHttpClient.sendSms(phone, msgText);
        return "Sms sent";
    }
}
