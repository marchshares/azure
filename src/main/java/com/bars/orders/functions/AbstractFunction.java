package com.bars.orders.functions;

import com.bars.orders.http.SimpleHttpClient;
import com.bars.orders.json.Order;
import com.bars.orders.mongo.MyMongoClient;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractFunction {
    final HttpRequestMessage<Optional<String>> request;
    final ExecutionContext context;
    final Logger logger;

    public AbstractFunction(HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        this.request = request;
        this.context = context;

        this.logger = context.getLogger();
    }

    public HttpResponseMessage run() {
        String body = request.getBody().orElse(null);
        if (body == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error: Empty body received").build();
        }

        try {
            logger.info("Incoming body: " + body);

            String msgSuccess = processBody(body);

            return request.createResponseBuilder(HttpStatus.OK).body(msgSuccess).build();
        } catch (Exception e) {

            logger.log(Level.WARNING, "Couldn't process request. Error " + e.toString() + ", msg: " + e.getMessage(), e);
            logger.log(Level.WARNING, "Request body: " + request.getBody());

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error " + e.toString() + ", msg: " + e.getMessage()).build();
        }
    }

    abstract String processBody(String body) throws Exception;
}
