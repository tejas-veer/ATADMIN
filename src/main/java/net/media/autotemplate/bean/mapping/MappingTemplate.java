package net.media.autotemplate.bean.mapping;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.SupplyDemandHierarchy;
import net.media.autotemplate.bean.SystemPageType;
import net.media.autotemplate.bean.TemplateData;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.util.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
    Created by shubham-ar
    on 27/3/18 4:19 PM   
*/
public class MappingTemplate {
    private static final Gson GSON = Util.getGson();
    private final TemplateData templateData;
    protected final CreativeConstants.MappingType mappingType;
    private final SupplyDemandHierarchy hierarchyLevel;
    @SerializedName("businessUnit")
    private final BusinessUnit businessUnit;
    @SerializedName("systemPageType")
    private final SystemPageType systemPageType;
    private String updationDate;
    private String adminName;

    public MappingTemplate(TemplateData templateData, CreativeConstants.MappingType mappingType, SupplyDemandHierarchy supplyDemandHierarchy, String buSelected, String systemPageTypeString) {
        this.templateData = templateData;
        this.mappingType = mappingType;
        this.hierarchyLevel = supplyDemandHierarchy;
        businessUnit = BusinessUnit.getBUFromName(buSelected);
        this.systemPageType = SystemPageType.getSPTFromName(systemPageTypeString);
    }

    public MappingTemplate(String entity, String hierarchyLevel, String templateId, String templateSize, CreativeConstants.MappingType mapping, int businessUnitId, String systemPageTypeString, String updationDate, String adminName) {
        this(new TemplateData(templateId, templateSize), mapping, new SupplyDemandHierarchy(hierarchyLevel, entity), BusinessUnit.getBUFromId(businessUnitId).getName(), systemPageTypeString);
        this.updationDate = updationDate;
        this.adminName = adminName;
    }

    public MappingTemplate(String entity, String hierarchyLevel, String templateId, String templateSize, int businessUnitId, int systemPageTypeId, String updationDate, String adminName) {
        this(entity, hierarchyLevel, templateId, templateSize, CreativeConstants.MappingType.MANUAL, businessUnitId, SystemPageType.getSPTFromId(systemPageTypeId).getName(), updationDate, adminName);
    }

    public MappingTemplate(String entity, String hierarchyLevel, String templateId, String templateSize, int businessUnitId, int systemPageTypeId) {
        this(entity, hierarchyLevel, templateId, templateSize, CreativeConstants.MappingType.MANUAL, businessUnitId, SystemPageType.getSPTFromId(systemPageTypeId).getName(), "", "");
    }

    public String getTemplateId() {
        return this.templateData.getTemplateId();
    }

    public CreativeConstants.MappingType getMappingType() {
        return mappingType;
    }

    public String getTemplateSize() {
        return this.templateData.getTemplateSize();
    }

    public String getHierarchyLevel() {
        return this.hierarchyLevel.getHierarchyLevel();
    }

    public String getEntityValue() {
        return this.hierarchyLevel.getEntityValue();
    }

    public BusinessUnit getBusinessUnit() {
        return businessUnit;
    }

    public SystemPageType getSystemPageType() {
        return systemPageType;
    }

    public String getUpdationDate() {
        return updationDate;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getXML() throws Exception {
        if (!Util.isSet(getSystemPageType())) {
            throw new Exception("Invalid_SystemPageType_Name::" + getSystemPageType().getName());
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return String.format("<e>%s</e><hl>%s</hl><tid>%s</tid><tsize>%s</tsize><sid>%s</sid><sd>%s</sd><xd>%s</xd><bu>%s</bu><spt>%s</spt>", this.hierarchyLevel.getEntityValue(), getHierarchyLevel(), getTemplateId().trim(), getTemplateSize(), this.mappingType.getSetId(), dtf.format(now), dtf.format(now), getBusinessUnit().getId(), getSystemPageType().getId());
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    public String getKey() {
        return getEntityValue()+ "$" +
                getHierarchyLevel() + "$" +
                getTemplateId().trim() + "$" +
                getTemplateSize() + "$" +
                this.mappingType.getSetId() + "$" +
                getBusinessUnit().getId() + "$" +
                getSystemPageType().getId();
    }
}
