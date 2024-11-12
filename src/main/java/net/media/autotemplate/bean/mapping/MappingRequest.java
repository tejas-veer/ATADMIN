package net.media.autotemplate.bean.mapping;

import com.google.gson.annotations.SerializedName;
import net.media.autotemplate.bean.TemplateData;
import net.media.autotemplate.constants.CreativeConstants;

import java.util.List;

public class MappingRequest {

    @SerializedName("mappingType")
    private final CreativeConstants.MappingType mappingType;
    @SerializedName("templates")
    private final List<TemplateData> templateDataList;
    @SerializedName("buSelected")
    private final String buSelected;

    public MappingRequest(CreativeConstants.MappingType mappingType, List<TemplateData> templateDataList, String buSelected) {
        this.mappingType = mappingType;
        this.templateDataList = templateDataList;
        this.buSelected = buSelected;
    }

    public List<TemplateData> getTemplateDataList() {
        return templateDataList;
    }

    public CreativeConstants.MappingType getMappingType() {
        return mappingType;
    }

    public String getBuSelected() {
        return buSelected;
    }
}
