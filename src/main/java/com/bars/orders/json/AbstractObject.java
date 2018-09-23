package com.bars.orders.json;


import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONObject;

import java.util.logging.Logger;

public abstract class AbstractObject {
    protected final Logger log;
    protected final ExecutionContext context;

    protected final JSONObject head;

    protected AbstractObject(JSONObject head, ExecutionContext context) {
        this.head = head;

        this.context = context;
        this.log = context.getLogger();
    }

    public Logger getLogger() {
        return log;
    }

    @Override
    public String toString() {
        return head.toString(1);
    }
}
