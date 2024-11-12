package net.media.autotemplate.dal.druid.bean;

import com.google.gson.JsonObject;

/*
    Created by shubham-ar
    on 3/10/18 3:26 PM   
*/
public class DruidMeta {
    private final String id;
    private final String type;
    private final String name;


    public DruidMeta(String id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public DruidMeta(JsonObject attribute) {
        this.id = attribute.get("id").getAsString();
        this.type= attribute.get("type").getAsString();
        this.name = attribute.get("name").getAsString();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DruidMeta{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
