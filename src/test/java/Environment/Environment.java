package Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Environment {
    private static final Environment instance = new Environment();
    private static String name;
    private static Properties properties;

    private Environment() {
        String env = System.getProperty("env", "").toLowerCase();

        if (env.equals("")) {
            throw new IllegalArgumentException(String.format("Environment must be specified with -Denv", env));
        }

        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            InputStream input = loader.getResourceAsStream("environments/" + env + ".properties");
            properties = new Properties();
            properties.load(input);
            name = env;
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Environment '%s' properties not found.", env));
        }
    }

    public static Environment getInstance() {
        return instance;
    }

    /**
     * Checks if the current environment is one of the specified environments.
     *
     * @param environments A list of environments to check.
     * @return
     */
    public static boolean is(String... environments) {
        String current = getInstance().getName();
        for (String environment : environments) {
            if (environment.equals(current)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return properties.getProperty("url");
    }

    public String getClaimCenterUrl() {
        return properties.getProperty("url_cc");
    }
}
