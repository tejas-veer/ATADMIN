package net.media.autotemplate.routes.util;

/*
    Created by shubham-ar
    on 19/4/18 4:45 PM   
*/
public class ApiException extends Exception {
    private final String name;
    private final String reason;
    private final Object source;

    public ApiException(String name, String reason, Object source) {
        this.name = name;
        this.reason = reason;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

    public Object getSource() {
        return source;
    }
}
