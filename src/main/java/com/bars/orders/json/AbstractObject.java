package com.bars.orders.json;


import com.bars.orders.FunctionEntryPoint;
import org.json.JSONObject;

import java.util.Map;

public abstract class AbstractObject {

    protected final JSONObject head;

    protected AbstractObject() {
        this(new JSONObject());
    }

    protected AbstractObject(JSONObject head) {
        this.head = head;
    }

    public JSONObject getHead() {
        return head;
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
