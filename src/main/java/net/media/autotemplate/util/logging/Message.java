//  Copyright (C) 2016 Media.net Advertising FZ-LLC All Rights Reserved

package net.media.autotemplate.util.logging;

import org.apache.logging.log4j.Logger;

/**
 * Created by sumeet
 * on 31/5/16.
 */
public class Message {
    private Logger log;
    private Level level;
    private String message;
    private Throwable throwable;

    public Message(Logger log, Level level) {
        this.log = log;
        this.level = level;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public Message setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public Logger getLog() {
        return log;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}
