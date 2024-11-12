package net.media.autotemplate.bean;

/**
 * Created by sumeet
 * on 10/11/17.
 */
public class EntityConfig {

    protected String entity;
    protected String property;
    protected String value;

    public EntityConfig(String entity, String property, String value) {
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

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
