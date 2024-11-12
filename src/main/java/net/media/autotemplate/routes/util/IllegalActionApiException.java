package net.media.autotemplate.routes.util;

/*
    Created by shubham-ar
    on 19/4/18 4:49 PM   
*/
public class IllegalActionApiException extends ApiException {

    public IllegalActionApiException(String reason, Object source) {
        super(IllegalActionApiException.class.getSimpleName(), reason, source);
    }
}
