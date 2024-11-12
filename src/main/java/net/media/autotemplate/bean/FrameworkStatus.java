package net.media.autotemplate.bean;

import com.google.gson.Gson;

public class FrameworkStatus {
    private String entity;
    private String framework;
    private String size;
    private String status;

    public FrameworkStatus(String entity, String framework, String size, String status) {
        this.entity = entity;
        this.framework = framework;
        this.size = size;
        this.status = status;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
