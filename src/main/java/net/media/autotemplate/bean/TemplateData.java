package net.media.autotemplate.bean;

import com.google.gson.annotations.SerializedName;

public class TemplateData {
    @SerializedName("templateId")
    private final String templateId;
    @SerializedName("templateSize")
    private final String templateSize;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("endDate")
    private String endDate;

    public TemplateData(String templateId, String templateSize) {
        this.templateId = templateId;
        this.templateSize = templateSize;
    }

    public TemplateData(String templateId, String templateSize, String startDate, String endDate) {
        this.templateId = templateId;
        this.templateSize = templateSize;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getTemplateSize() {
        return templateSize;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
