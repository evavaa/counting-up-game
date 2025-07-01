import java.util.Properties;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/game5.properties";

    public static void main(String[] args) {
        // Use command line argument if provided, otherwise use default
        String propertiesPath = args.length > 0 ? args[0] : DEFAULT_PROPERTIES_PATH;

        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        if (properties == null) {
            System.err.println("Error: Could not load properties file: " + propertiesPath);
            System.exit(1);
        }

        String logResult = new CountingUpGame(properties).runApp();
        System.out.println("logResult = " + logResult);
    }

}
