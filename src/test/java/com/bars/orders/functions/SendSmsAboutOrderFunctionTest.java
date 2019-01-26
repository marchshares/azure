package com.bars.orders.functions;

import com.bars.orders.TestHelper;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.bars.orders.TestHelper.invokeContext;
import static com.bars.orders.TestHelper.invokeRequest;
import static org.junit.Assert.*;

public class SendSmsAboutOrderFunctionTest {

    @Before
    public void setUp() {
        TestHelper.setTestProperties();
    }

    @Ignore
    @Test
    public void testFunc() {
        SendSmsAboutOrderFunction func = new SendSmsAboutOrderFunction(
                invokeRequest(testJsonBody),
                invokeContext()
        );
        HttpResponseMessage res = func.run();

        System.out.println(res.getBody());
        assertEquals(res.getStatus(), HttpStatus.OK);
    }

    private String testJsonBody = "{" +
            "\"paymentId\" : \"1164522304\", " +
            "\"sendSmsAboutOrder\" : \"yes\", " +
            "\"phone\" : \"" + phone + "\", " +
            "\"msgTemplate\" : \"" + msgTemplate + "\"" +
            "}";

    private static final String phone = "+7 (900) 500-9000";
    private static final String msgTemplate = "Ваш заказ #${paymentId} принят! Скоро мы свяжемся с вами для уточнения деталей." +
            "Спасибо, что выбрали 8bars.ru (+74994990000)";

}