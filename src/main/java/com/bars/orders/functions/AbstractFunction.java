package com.bars.orders.functions;

import com.bars.orders.http.SimpleHttpClient;
import com.bars.orders.json.Order;
import com.bars.orders.mongo.MyMongoClient;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;

import java.util.Optional;
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

    public abstract HttpResponseMessage run();
}
