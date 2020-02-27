package com.bars.orders;

import com.bars.orders.functions.NewOrderFunction;
import com.bars.orders.functions.SendSmsAboutOrderFunction;
import com.bars.orders.functions.SendSmsCdekTrackCodeFunction;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;
import java.util.logging.Logger;

import static com.bars.orders.GlobalLogger.setLoggerFromContext;

public class FunctionEntryPoint {
    public static Logger logger = Logger.getGlobal();

    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws Exception {
        setLoggerFromContext(context);

        NewOrderFunction func = new NewOrderFunction(request);
        func.init();

        return func.run();
    }

    @FunctionName("SendSms_CdekTrackCode")
    public HttpResponseMessage runSendSms_CdekTrackCode(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        setLoggerFromContext(context);

        SendSmsCdekTrackCodeFunction func = new SendSmsCdekTrackCodeFunction(request);

        return func.run();
    }

    @FunctionName("SendSms_AboutOrder")
    public HttpResponseMessage runSendSms_AboutOrder(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        setLoggerFromContext(context);

        SendSmsAboutOrderFunction func = new SendSmsAboutOrderFunction(request);

        return func.run();
    }
}
