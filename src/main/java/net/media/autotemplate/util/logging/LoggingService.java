//  Copyright (C) 2016 Media.net Advertising FZ-LLC All Rights Reserved

package net.media.autotemplate.util.logging;


import net.media.autotemplate.bean.RequestGlobal;
import net.media.autotemplate.services.FlockService;
import net.media.autotemplate.util.Util;
import org.apache.logging.log4j.Logger;

/**
 * Created by sumeet
 * on 30/5/16.
 */
public class LoggingService {

    private final String requestID;
    public static final String messageSeparator = "|";

    private LoggingService() {
        this.requestID = RequestGlobal.getRequestId();
    }

    private static LoggingService get() {
        if (RequestGlobal.getLoggingService() == null) {
            RequestGlobal.setLoggingService(new LoggingService());
        }
        return RequestGlobal.getLoggingService();
    }

    public static void log(Logger log, Level level, String message) {
        log(new Message(log, level).setMessage(message));
    }

    public static void log(Logger log, Level level, String... message) {
        log(new Message(log, level).setMessage(Util.concatenate(messageSeparator, message)));
    }

    public static void log(Logger log, Level level, String message, Throwable exception) {
        log(new Message(log, level).setMessage(message).setThrowable(exception));
    }

    public static void log(Logger log, Level level, String key, Object value) {
        log(new Message(log, level).setMessage(key + "|" + value));
    }

    private static void log(Message loggingMessage) {
        get().logMessage(loggingMessage);
    }

    private void logMessage(Message message) {
        Throwable throwable = message.getThrowable();
        if (throwable != null) {
            if (Util.isStringSet(message.getMessage())) {
                message.setMessage(message.getMessage() + messageSeparator + throwable.getMessage());
            } else {
                message.setMessage("EXCEPTION_CAUGHT");
            }
        }

        Logger log = message.getLog();
        Level level = message.getLevel();
        logStandardMessage(log, level, message.getMessage(), throwable);

    }

    private void logStandardMessage(Logger log, Level level, String message, Throwable throwable) {

        StringBuilder logMessage = new StringBuilder(requestID);
        if (Util.isStringSet(message)) {
            logMessage.append(messageSeparator).append(message);
        }

        if (level == Level.fatal || level == Level.warn || level == Level.error) {
            FlockService.postFlockException(logMessage.toString(), throwable);
        }

        level.logMessage(log, logMessage.toString(), throwable);
    }
}
