package net.media.autotemplate.bean.mapping;

import com.google.gson.Gson;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.SupplyDemandHierarchy;
import net.media.autotemplate.bean.SystemPageType;
import net.media.autotemplate.bean.TemplateData;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.util.Util;

/*
    Created by shubham-ar
    on 27/3/18 3:07 PM   
*/
public class SeasonalTemplate extends MappingTemplate {

    private static final Gson GSON = Util.getGson();
    private final String startDate;
    private final String endDate;

    public SeasonalTemplate(String entity, String hierarchyLevel, String templateId, String templateSize, String startDate, String endDate, int businessUnitId, int systemPageTypeId, String updationDate, String adminName) {
        super(entity, hierarchyLevel, templateId, templateSize, CreativeConstants.MappingType.SEASONAL, businessUnitId, SystemPageType.getSPTFromId(systemPageTypeId).getName(), updationDate, adminName);
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public SeasonalTemplate(TemplateData templateData, CreativeConstants.MappingType mappingType, SupplyDemandHierarchy supplyDemandHierarchy, String buSelected, String systemPageType) {
        super(templateData, mappingType, supplyDemandHierarchy, buSelected, systemPageType);
        this.startDate = templateData.getStartDate();
        this.endDate = templateData.getEndDate();
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String getXML() throws Exception {
        BusinessUnit businessUnit = getBusinessUnit();
        return String.format("<e>%s</e><hl>%s</hl><tid>%s</tid><tsize>%s</tsize><sid>%s</sid><sd>%s 00:00:00</sd><xd>%s 23:59:59</xd><bu>%s</bu><spt>%s</spt>", getEntityValue(), getHierarchyLevel(), getTemplateId().trim(), getTemplateSize(), this.mappingType.getSetId(), startDate, endDate, businessUnit.getId(), businessUnit.getDefaultSystemPage().getId());
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
