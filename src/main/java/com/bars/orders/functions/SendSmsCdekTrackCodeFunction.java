package com.bars.orders.functions;

import com.bars.orders.http.CdekHttpClient;
import com.bars.orders.http.SmsAeroHttpClient;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bars.orders.Utils.checkGood;

public class SendSmsCdekTrackCodeFunction extends AbstractFunction {
    public static final String PAYMENT_ID_PATTERN = "^[0-9]{3,}$";

    private final CdekHttpClient cdekHttpClient;
    private final SmsAeroHttpClient smsAeroHttpClient;

    public SendSmsCdekTrackCodeFunction(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        super(request, context);

        this.cdekHttpClient = new CdekHttpClient(logger);
        this.smsAeroHttpClient = new SmsAeroHttpClient(logger);
    }

    @Override
    public HttpResponseMessage run() {
        String body = request.getBody().orElse(null);
        if (body == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error: Empty body received").build();
        }

        try {
            logger.info("Incoming body: " + body);

            processBody(body);

        } catch (Exception e) {

            logger.log(Level.WARNING, "Couldn't process request. Error msg: " + e.getMessage(), e);
            logger.log(Level.WARNING, "Request body: " + request.getBody());

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error msg: " + e.getMessage()).build();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("Done").build();
    }

    private void processBody(String body) {
        BasicDBObject jsonBody = (BasicDBObject) JSON.parse(body);

        String trelloCardDesc = jsonBody.getString("trelloCardDesc");
        String trelloCardName = jsonBody.getString("trelloCardName");

        String phone = getPhone(trelloCardDesc);
        String cdekOrderId = getCdekOrderId(trelloCardName);

        String msgText = "Мы уже упаковали ваш заказ и готовим его к отправке. " +
                "Статус заказа доступен по ссылке: cdek.ru/track.html?order_id=" + cdekOrderId;

        smsAeroHttpClient.sendSms(phone, msgText);
    }

    public String getPhone(String trelloCardDesc) {
        String[] descLines = trelloCardDesc.split("\n");

        Pattern pattern = Pattern.compile("\\+.*$");
        String phone = Arrays.stream(descLines)
                .filter(line -> line.contains("Phone") && pattern.matcher(line).find())
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
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

        logger.info("paymentId:" + paymentId);

        return paymentId;
    }

    public String getCdekOrderId(String trelloCardName) {
        String paymentId = getPaymentId(trelloCardName);

        return cdekHttpClient.getCdekOrderId(paymentId);
    }
}
