package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestParameters {
    private static Properties properties = null;

    /**
     * Gets a test parameter either from the command line or from a run properties file.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getParameter(String key, String defaultValue) {
        // Direct command line parameter takes precedence.
        String value = System.getProperty(key, null);
        if (value != null) {
            return value;
        }

        // Xray properties are second.
        if (properties == null) {
            try {
                FileInputStream stream = new FileInputStream("run.properties");
                properties = new Properties();
                properties.load(stream);
            } catch (IOException e) {
                // Run properties not found.
                return defaultValue;
            }
        }
        return properties.getProperty(key, defaultValue);
    }
}
