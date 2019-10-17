package com.bars.orders;

import com.microsoft.azure.functions.ExecutionContext;

import java.util.logging.Logger;

public class GlobalLogger {
    public static Logger glogger = Logger.getGlobal();

    public static void setLoggerFromContext(ExecutionContext context) {
        glogger = context.getLogger();
    }
}
