package com.bars.orders;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;

public class TestHelper {

    public static void setTestProperties() {
        try {
            try (FileInputStream logFIS = new FileInputStream("src/test/resources/logging.properties")) {
                LogManager.getLogManager().readConfiguration(logFIS);
            }

            try (FileInputStream propFIS = new FileInputStream("src/test/resources/app.properties")) {
                Properties properties = new Properties(System.getProperties());
                properties.load(propFIS);

                System.setProperties(properties);
            }

        } catch (IOException e) {
            //ignore
        }
    }
}
