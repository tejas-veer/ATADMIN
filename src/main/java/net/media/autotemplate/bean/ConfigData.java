package net.media.autotemplate.bean;

public class ConfigData {
    private final String entity;
    private final String property;
    private final String value;

    public ConfigData(String entity, String property, String value) {
        this.entity = entity;
        this.property = property;
        this.value = value;
    }

    public String getEntity() {
        return entity;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }
}
