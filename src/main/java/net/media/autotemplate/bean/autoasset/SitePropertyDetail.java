package net.media.autotemplate.bean.autoasset;

import com.google.gson.annotations.SerializedName;

public class SitePropertyDetail {
    @SerializedName("entity_name")
    String entityName;
    @SerializedName("entity_value")
    String entityValue;
    @SerializedName("site_name")
    String siteName;
    @SerializedName("property")
    String property;
    @SerializedName("value")
    String propertyValue;
    @SerializedName("is_active")
    int isActive;

    public SitePropertyDetail(String siteName, String property, String propertyValue, int isActive) {
        this.siteName = siteName;
        this.property = property;
        this.propertyValue = propertyValue;
        this.isActive = isActive;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getProperty() {
        return property;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public String getEntityName() {
        return entityName;
    }
}
