package net.media.autotemplate.bean.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.util.Util;

/*
    Created by shubham-ar
    on 15/3/18 6:49 PM   
*/
public class ConfigMeta {
    public static final String GENERATOR_META = "GENERATOR_PROPERTY_META";
    public static final String ZERO_COLOR_META = "ZERO_COLOR_PROPERTY_META";
    private static final Gson GSON = Util.getGson();
    private final String entity;
    private final JsonObject attributes;

    public ConfigMeta(String entity, String value) {
        this.entity = entity;
        this.attributes = GSON.fromJson(value, JsonObject.class);
    }

    public String getEntity() {
        return entity;
    }

    public JsonObject getAttributes() {
        return attributes;
    }
}
