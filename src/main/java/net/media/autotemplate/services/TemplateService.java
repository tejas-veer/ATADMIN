package net.media.autotemplate.services;

import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.bean.mapping.MappingUpdate;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.FrameworkSizeService;
import net.media.autotemplate.factory.BeanFactory;
import net.media.autotemplate.factory.ServiceFactory;
import net.media.autotemplate.services.mapping.MappingService;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.UserActionLogging;
import net.media.database.DatabaseException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.BadPayloadException;

import java.util.List;
import java.util.concurrent.ExecutionException;

/*
    Created by shubham-ar
    on 12/2/18 11:18 PM   
*/
public class TemplateService {
    private final Entity entity;
    private final Admin admin;
    private final String buSelected;
    private AclStatus aclStatus;

    public TemplateService(Entity entity, Admin admin, String buSelected) throws ExecutionException, DatabaseException, Authentication.Failed {
        this.entity = entity;
        this.admin = admin;
        this.buSelected = buSelected;
        this.aclStatus = AclService.validate(entity, admin, BusinessUnit.getBUFromName(this.buSelected));
    }

    public void addTemplate(String template) throws Exception {
        addTemplateSanityCheck(template);
        MappingService mappingService = ServiceFactory.getMappingServiceForEntity(entity, admin, buSelected);
        MappingUpdate<? extends MappingTemplate> update = BeanFactory.makeMappingUpdateForEntity(entity, template, buSelected);
        mappingService.insertMappings(update);
        UserActionLogging.log(admin, "ADDED_MANUAL_TEMPLATE", "ENTITY -> " + entity.getEntityId() + " | TEMPLATE ->" + template);
    }

    private void addTemplateSanityCheck(String template) throws Exception {
        String frameworkId;
        try {
            frameworkId = String.valueOf(AutoTemplateDAL.getCachedFrameworkId(template));
        }catch (Exception e){
            throw new BadPayloadException("Invalid FrameworkId for Template");
        }

        String adtagSize = entity.getSize();

        if (!Util.isStringSet(adtagSize)) {
            throw new BadPayloadException("No Size found on the template");
        }

        List<Template> templatesForEntity = AutoTemplateDAL.getCMTemplatesForEntity(entity);
        boolean templateExists = templatesForEntity.stream().map(Template::getTemplateId).anyMatch(s -> s.equals(template));
        if (templateExists) {
            throw new BadPayloadException("Template Id already Exists on Entity");
        }

        if (!FrameworkSizeService.isValidSize(Long.valueOf(frameworkId), adtagSize)) {
            throw new BadPayloadException("Size mismatch on Framework");
        }
    }

    public String updateTemplateStatus(TemplateStatus templateStatus) throws DatabaseException {
        List<String> enabled = templateStatus.getTemplatesToEnable();
        List<String> disabled = templateStatus.getTemplatesToDisable();

        AutoTemplateDAL.updateAutoTemplateStatus(entity, enabled, disabled, admin);

        String enabledStr = StringUtils.join(enabled, ',');
        String disabledStr = StringUtils.join(disabled, ',');
        if (!Util.isStringSet(enabledStr))
            enabledStr = "null";
        if (!Util.isStringSet(disabledStr))
            disabledStr = "null";
        String updateStr = " | ENABLED -> " + enabledStr + " | DISABLED -> " + disabledStr;
        UserActionLogging.log(admin, "UPDATE_TEMPLATE_STATUS", "ENTITY -> " + entity.getEntityId() + updateStr);
        return updateStr;
    }
}
