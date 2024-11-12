//  Copyright (C) 2017 Media.net Advertising FZ-LLC All Rights Reserved
package net.media.autotemplate.util;

import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Properties;

/**
 * Created by sumeet
 * on 10/3/16.
 */
public class SysProperties {

    private static SysProperties sysProperties = null;
    private static Properties properties = new Properties();

    private static final String PROPERTY_FILE = "/application.properties";
    private static final Logger log = LogManager.getLogger(SysProperties.class);

    static {
        getInstance();
    }

    private SysProperties() {
        initProperties();
    }

    public static synchronized SysProperties getInstance() {
        if (!Util.isSet(sysProperties)) {
            sysProperties = new SysProperties();
        }
        return sysProperties;
    }

    private static void initProperties() {
        LoggingService.log(log, Level.info, "SYSPROPERITES_INIT_STARTED");
        try {
            properties.load(SysProperties.class.getResourceAsStream(PROPERTY_FILE));
        } catch (Exception e) {
            LoggingService.log(log, Level.error, "SYSPROPERITES_INIT_FAILURE", e);
        }

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            LoggingService.log(log, Level.info, String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }

        LoggingService.log(log, Level.info, "SYSPROPERITES_SIZE|" + properties.size());
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static String getProperty(String key) {
        return (String) properties.get(key);
    }

    public static int getPropertyAsInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public static boolean getPropertyAsBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
}
