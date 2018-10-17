package com.bars.orders.json;


import com.microsoft.azure.functions.ExecutionContext;
import org.json.JSONObject;

import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractObject {
    final Logger log;

    final ExecutionContext context;

    protected final JSONObject head;

    AbstractObject(JSONObject head, ExecutionContext context) {
        this.head = head;

        this.context = context;
        this.log = context.getLogger();
    }

    public JSONObject getHead() {
        return head;
    }

    public Logger getLogger() {
        return log;
    }

    public Map<String, Object> toMap() {
        return head.toMap();
    }

    public String toJson() {
        return head.toString();
    }

    @Override
    public String toString() {
        return head.toString(1);
    }
}
