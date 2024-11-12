package net.media.autotemplate.bean.mapping;

import net.media.autotemplate.bean.SystemPageType;
import net.media.autotemplate.constants.CreativeConstants;

/*
    Created by shubham-ar
    on 17/4/18 5:46 PM   
*/
public class ZeroColorTemplate extends MappingTemplate {

    private final String frameworkId;

    public ZeroColorTemplate(String entity, String hierarchyLevel, String templateId, String frameworkId, String templateSize, int businessUnitId, int systemPageTypeId) {
        super(entity, hierarchyLevel, templateId, templateSize, CreativeConstants.MappingType.ZERO_COLOR, businessUnitId, SystemPageType.getSPTFromId(systemPageTypeId).getName(), "", "");
        this.frameworkId = frameworkId;
    }

    @Override
    public String getXML() throws Exception {
        return String.format("%s<fid>%s</fid>", super.getXML(), frameworkId);
    }

    public String getFrameworkId() {
        return frameworkId;
    }
}
