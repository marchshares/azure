package com.bars.orders;


import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class PropertiesHelper {

    public static final String PROPERTIES_FILE_PATH = "../../classes/app.properties";

    public static void main(String[] args) {
        loadAppProperties();
    }
    public static void loadAppProperties() {
        File file = new File(PROPERTIES_FILE_PATH);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Properties properties = new Properties();
                properties.load(reader);

                System.out.println(properties);
                System.setProperties(properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static String getSystemProp(String propName) {
        String envProp = System.getenv(propName);

        return envProp != null ? envProp : System.getProperty(propName);
    }
}
