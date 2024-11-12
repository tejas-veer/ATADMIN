package net.media.autotemplate.bean;

public class QueryTuple {

    private String entity;
    private String property;
    private String value;

    public QueryTuple(String entity, String property, String value) {
        this.entity = entity;
        this.property = property;
        this.value = value;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return entity + "," + property + "," + value + " -> " + super.toString();
    }
}