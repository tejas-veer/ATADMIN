package net.media.keywordlearning.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by autoopt/rohit.aga.
 */
public class PropertyManager {
//    private static final Logger log = Logger.getLogger(PropertyManager.class);
    private static Properties properties = new Properties();
    private static PropertyManager instance;
    private PropertyManager() throws IOException {
        InputStream inputStream = PropertyManager.class.getClassLoader().getResourceAsStream("learning.application.properties");
        properties.load(inputStream);
        inputStream.close();
    }

    public static Properties getProperties() {
        if(instance == null) {
            try {
                instance = new PropertyManager();
            } catch (IOException e) {
//                Logging.errorLogging(log,"PropertyManager::ERROR", Arrays.toString(e.getStackTrace()));
            }
        }
        return instance.properties;
    }
}
