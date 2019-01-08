package com.bars.orders;

import com.bars.orders.functions.NewOrderFunction;
import com.bars.orders.functions.SendSmsCdekTrackCodeFunction;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class FunctionEntryPoint {

    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        NewOrderFunction func = new NewOrderFunction(request, context);
        func.init();

        return func.run();
    }

    @FunctionName("SendSms_CdekTrackCode")
    public HttpResponseMessage runSendSms_CdekTrackCode(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        SendSmsCdekTrackCodeFunction func = new SendSmsCdekTrackCodeFunction(request, context);

        return func.run();
    }
}
