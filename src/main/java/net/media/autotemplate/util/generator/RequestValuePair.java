package net.media.autotemplate.util.generator;

import org.apache.http.NameValuePair;

/*
    Created by shubham-ar
    on 14/11/17 4:56 PM   
*/
public class RequestValuePair implements NameValuePair {

    String name;
    String value;

    public RequestValuePair(String name, Object value) {
        this.name = name;
        this.value = String.valueOf(value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }
}
