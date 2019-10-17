package com.bars.orders.functions;

import com.bars.orders.http.CdekHttpClient;
import com.bars.orders.http.SmsAeroHttpClient;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bars.orders.GlobalLogger.glogger;
import static com.bars.orders.Utils.checkGood;

public class SendSmsCdekTrackCodeFunction extends AbstractFunction {
    public static final String PAYMENT_ID_PATTERN = "^[0-9]{3,}$";
    public static final Pattern PHONE_PATTERN = Pattern.compile("\\+.*$");

    private final CdekHttpClient cdekHttpClient;
    private final SmsAeroHttpClient smsAeroHttpClient;

    public SendSmsCdekTrackCodeFunction(HttpRequestMessage<Optional<String>> request) {
        super(request);

        this.cdekHttpClient = new CdekHttpClient();
        this.smsAeroHttpClient = new SmsAeroHttpClient();
    }

    @Override
    String processRequest(String body, Map<String, String> headers) throws Exception{
        BasicDBObject jsonBody = (BasicDBObject) JSON.parse(body);

        String trelloCardDesc = jsonBody.getString("trelloCardDesc");
        String trelloCardName = jsonBody.getString("trelloCardName");
        String msgTemplate = jsonBody.getString("msgTemplate");

        String phone = getPhone(trelloCardDesc);
        String paymentId = getPaymentId(trelloCardName);
        String cdekOrderId = getCdekOrderId(trelloCardName);

        String msgText = msgTemplate.replace("${cdekOrderId}", cdekOrderId);

        smsAeroHttpClient.sendSms(phone, msgText);

        return "paymentId=" + paymentId + " -> cdekOrderId=" + cdekOrderId + ". Sms sent";
    }

    public String getPhone(String trelloCardDesc) {
        String[] descLines = trelloCardDesc.split("\n");

        String phone = Arrays.stream(descLines)
                .filter(line -> line.contains("Phone") && PHONE_PATTERN.matcher(line).find())
                .map(line -> {
                    Matcher matcher = PHONE_PATTERN.matcher(line);
                    matcher.find();


                    return matcher.group();
                }).findFirst().orElse(null);

        checkGood(phone, "phone");

        return phone;
    }
    
    public String getPaymentId(String trelloCardName) {
        String paymentId = trelloCardName.split(":")[0];

        if (! paymentId.matches(PAYMENT_ID_PATTERN)) {
            throw new RuntimeException("Not a paymentId: " + paymentId);
        }

        glogger.info("paymentId:" + paymentId);

        return paymentId;
    }

    public String getCdekOrderId(String trelloCardName) {
        String paymentId = getPaymentId(trelloCardName);

        return cdekHttpClient.getCdekOrderId(paymentId);
    }
}
