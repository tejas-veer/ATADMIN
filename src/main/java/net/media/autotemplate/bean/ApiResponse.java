package net.media.autotemplate.bean;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.util.Util;
import org.apache.http.HttpStatus;

/*
    Created by shubham-ar
    on 3/1/18 8:23 PM   
*/
public class ApiResponse {
    public static final Gson GSON = Util.getGson();
    private int statusCode;
    private String response;

    public ApiResponse(int statusCode, String response) {
        this.statusCode = statusCode;
        this.response = response;
    }

    public ApiResponse(String response) {
        this.statusCode = HttpStatus.SC_OK;
        this.response = response;
    }

    public ApiResponse(String param, String response) {
        this.statusCode = HttpStatus.SC_OK;
        JsonObject object = new JsonObject();
        object.addProperty(param, response);
        this.response = GSON.toJson(object);
    }

    public ApiResponse(JsonElement response) {
        this(GSON.toJson(response));
    }

    public ApiResponse(int statusCode, JsonElement response) {
        this(statusCode, GSON.toJson(response));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }
}
