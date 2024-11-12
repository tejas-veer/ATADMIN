package net.media.autotemplate.bean.blocking;

import com.google.gson.Gson;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.SupplyDemandHierarchy;
import net.media.autotemplate.bean.TemplateData;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.util.Util;

public class BlockingTemplate extends MappingTemplate {
    private static final Gson GSON = Util.getGson();
    private final CreativeConstants.Type type;

    public BlockingTemplate(TemplateData templateData, SupplyDemandHierarchy supplyDemandHierarchy, String buSelected, String systemPageType, CreativeConstants.Type type) {
        super(templateData, CreativeConstants.MappingType.MANUAL, supplyDemandHierarchy, buSelected, systemPageType);
        this.type = type;
    }

    public CreativeConstants.Type getType() {
        return type;
    }

    @Override
    public String getXML() throws Exception {
        BusinessUnit businessUnit = getBusinessUnit();
        if (!Util.isSet(getSystemPageType())) {
            throw new Exception("Invalid_SystemPageType_Name::" + getSystemPageType().getName());
        }
        return String.format("<e>%s</e><hl>%s</hl><f_id>%s</f_id><f_size>%s</f_size><f_status>%s</f_status><f_type>%s</f_type><spt>%s</spt><bu>%s</bu>", getEntityValue(), getHierarchyLevel(), getTemplateId(), getTemplateSize(), CreativeConstants.Status.B.name(), this.type.name(), getSystemPageType().getId(), businessUnit.getId());
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    @Override
    public String getKey() {
        return getEntityValue()+ "$" +
                getHierarchyLevel() + "$" +
                getTemplateId().trim() + "$" +
                getTemplateSize() + "$" +
                CreativeConstants.Status.B.name() + "$" +
                this.type.name() + "$" +
                getBusinessUnit().getId() + "$" +
                getSystemPageType().getId();
    }
}
