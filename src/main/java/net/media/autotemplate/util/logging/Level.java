//  Copyright (C) 2016 Media.net Advertising FZ-LLC All Rights Reserved

package net.media.autotemplate.util.logging;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

/**
 * Created by sumeet
 * on 30/5/16.
 */
public enum Level {
    fatal {
        @Override
        public void logMessage(Logger log, Marker marker, String message, Throwable throwable) {
            log.fatal(marker, message, throwable);
        }
    }, error {
        @Override
        public void logMessage(Logger log, Marker marker, String message, Throwable throwable) {
            log.error(marker, message, throwable);
        }
    }, warn {
        @Override
        public void logMessage(Logger log, Marker marker, String message, Throwable throwable) {
            log.warn(marker, message, throwable);
        }
    }, info {
        @Override
        public void logMessage(Logger log, Marker marker, String message, Throwable throwable) {
            log.info(marker, message, throwable);
        }
    };

    public abstract void logMessage(Logger log, Marker marker, String message, Throwable throwable);

    public void logMessage(Logger log, String message, Throwable throwable) {
        logMessage(log, null, message, throwable);
    }


}
