package net.media.autotemplate.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.EntityCustomization;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;

/*
    Created by shubham-ar
    on 3/10/17 4:30 PM   
*/
public class ResponseUtil {
    private static final Logger LOG = LogManager.getLogger(ResponseUtil.class);
    private static Gson gson = new Gson();
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();


    public static String getBaseResponse(boolean status, String message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", status);
        res.addProperty("response", message);
        return gson.toJson(res);
    }

    //todo accept jsonObject instead jsonElement
    public static String getBaseResponse(boolean status, JsonElement message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", status);
        res.add("response", message);
        return gson.toJson(res);
    }

    public static String getErrorResponse(String name, String reason) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("reason", reason);
        return getBaseResponse(false, jsonObject);
    }

    public static String getErrorResponse(Exception e, Logger log, Request request) {
        log.error(request.requestMethod() + "\t" + request.pathInfo(), e);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", e.getClass().getSimpleName());
        jsonObject.addProperty("reason", e.getMessage());
        jsonObject.addProperty("stacktrace", ExceptionUtils.getStackTrace(e));
        return getBaseResponse(false, jsonObject);
    }

    public static String getResponse(EntityCustomization entityCustomization) {
        JsonObject response = new JsonObject();
        JsonElement entityCustomizationJson = GSON.toJsonTree(entityCustomization, EntityCustomization.class);
        response.add("entityCustomization", entityCustomizationJson);
        return GSON.toJson(response);
    }
}
