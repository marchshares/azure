package com.bars.orders.functions;

import com.bars.orders.FunctionEntryPoint;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import static com.bars.orders.GlobalLogger.glogger;

public abstract class AbstractFunction {
    final HttpRequestMessage<Optional<String>> request;

    public AbstractFunction(HttpRequestMessage<Optional<String>> request) {
        this.request = request;

    }

    public HttpResponseMessage run() {
        String body = request.getBody().orElse(null);
        if (body == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error: Empty body received").build();
        }

        try {
            glogger.info("Incoming headers: " + request.getHeaders());
            glogger.info("Incoming body: " + body);

            String msgSuccess = processRequest(body, request.getHeaders());

            return request.createResponseBuilder(HttpStatus.OK).body(msgSuccess).build();
        } catch (Exception e) {

            glogger.log(Level.WARNING, "Couldn't process request. Error " + e.toString() + ", msg: " + e.getMessage(), e);
            glogger.log(Level.WARNING, "Request body: " + request.getBody());

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error " + e.toString() + ", msg: " + e.getMessage()).build();
        }
    }

    abstract String processRequest(String body, Map<String, String> headers) throws Exception;
}
