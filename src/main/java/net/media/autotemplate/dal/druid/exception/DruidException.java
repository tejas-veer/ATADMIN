package net.media.autotemplate.dal.druid.exception;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.util.Util;

/*
    Created by shubham-ar
    on 11/10/17 2:18 PM   
*/
public class DruidException extends Exception {
    public static final Gson GSON = Util.getGson();
    String identifier;
    JsonObject errorJson;

    public DruidException(String message, String errorJson) {
        super(errorJson);
        this.identifier = message;
        this.errorJson = GSON.fromJson(errorJson, JsonObject.class);
    }

    public String getIdentifier() {
        return identifier;
    }

    public JsonObject getErrorJson() {
        return errorJson;
    }
}
