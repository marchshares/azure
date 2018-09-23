package com.bars.orders;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.logging.Logger;

import com.bars.orders.json.Order;
import com.bars.orders.json.SetSplitter;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    private Logger log;

    private Order order;

    /**
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTrigger-Java
     * 2. curl {your host}/api/HttpTrigger-Java?name=HTTP%20Query
     */
    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws UnsupportedEncodingException {
        setLogger(context);
        log.info("Java HTTP trigger processed a request.");

        String body = request.getBody().orElse(null);
        if (body == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error: Empty body received").build();
        }

        String decodedBody = URLDecoder.decode(body, "UTF-8");

        order = new Order(decodedBody, context);
        System.out.println(order);

        SetSplitter splitter = new SetSplitter(context);

        splitter.splitSets(order);


        System.out.println(order);
        return request.createResponseBuilder(HttpStatus.OK).body("Hello!").build();
    }

    private void setLogger(ExecutionContext context) {
        log = context.getLogger();
    }

    public Order getOrder() {
        return order;
    }
}
