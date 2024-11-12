package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.CreativeDAL;
import net.media.autotemplate.dal.blocking.TemplateBlockingDal;
import net.media.autotemplate.dal.configs.AutoTemplateConfigMaster;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidFilterBean;
import net.media.autotemplate.util.ResponseUtil;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.UserActionLogging;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.BadPayloadException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/*
    Created by shubham-ar
    on 18/10/17 2:58 PM   
*/
public class EntityService {

    private static final Gson GSON = Util.getGson();
    private static final Logger LOG = LogManager.getLogger(EntityService.class);
    private Admin admin;
    private Entity entity;
    private BusinessUnit businessUnit;
    private AclStatus aclStatus;

    public EntityService(Entity entity, Admin admin, BusinessUnit businessUnit) throws DatabaseException, ExecutionException, Authentication.Failed {
        this.entity = entity;
        this.admin = admin;
        this.businessUnit = businessUnit;
        this.aclStatus = AclService.validate(entity, admin, this.businessUnit);
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean setHeaderCustomization(String headerText) throws UnsupportedEncodingException, DatabaseException {
        if (Util.isStringSet(headerText)) {
            headerText = URLDecoder.decode(headerText, "UTF-8");
            AutoTemplateDAL.insertAutoTemplateConfigMaster(String.valueOf(entity.getEntityId()), ConfigConstants.HEADER_TEXT_PROPERTY, headerText, (int) admin.getAdminId());
            AutoTemplateDAL.insertAutoTemplateConfigMaster(CreativeConstants.Level.ENTITY.name() + "_" + String.valueOf(entity.getEntityId()), ConfigConstants.HEADER_TEXT_PROPERTY, headerText, (int) admin.getAdminId());
            UserActionLogging.log(admin, "HEADER_TEXT_UPDATE", entity.getEntityId() + " -> " + headerText);
        }
        return true;
    }

    public boolean setAdAttribution(String adAttributionLink) throws DatabaseException {
        if (Util.isStringSet(adAttributionLink)) {
            String adAttributionConfigValue = String.format("%s%s", ConfigConstants.AD_ATTRIBUTION_SEPARATOR, adAttributionLink);
            AutoTemplateDAL.insertAutoTemplateConfigMaster(String.valueOf(entity.getEntityId()), ConfigConstants.AD_ATTRIBUTION_PROPERTY, adAttributionConfigValue, (int) admin.getAdminId());
            AutoTemplateDAL.insertAutoTemplateConfigMaster(CreativeConstants.Level.ENTITY.name() + "_" + String.valueOf(entity.getEntityId()), ConfigConstants.AD_ATTRIBUTION_PROPERTY, adAttributionConfigValue, (int) admin.getAdminId());
            UserActionLogging.log(admin, "AD_ATTRIBUTION_LINK_UPDATE", entity.getEntityId() + " -> " + adAttributionLink);
        }
        return true;
    }

    public String updateTemplateStatus(String updateJson) throws DatabaseException, Authentication.Failed, ExecutionException {
        TemplateStatus templateStatus = GSON.fromJson(updateJson, TemplateStatus.class);
        BusinessUnit businessUnit = BusinessUnit.getBUFromName(templateStatus.getBuSelected());
        TemplateBlockingDal.insertEntityWisCreativeStatus(entity.getEntityId() + "", CreativeConstants.Level.ENTITY.name(), templateStatus.getBlockingInfoList(entity), admin, businessUnit);
        return new TemplateService(entity, admin, templateStatus.getBuSelected()).updateTemplateStatus(templateStatus);
    }

    public String addManualTemplates(String template, String buSelected) {
        try {
            new TemplateService(entity, admin, buSelected).addTemplate(template);
            return ResponseUtil.getBaseResponse(true, Util.getConcatString("Added Template ", template, " on ", entity.getDomain(), " [ ", entity.getAdtagId(), " ]"));
        } catch (Exception e) {
            throw new BadPayloadException(ResponseUtil.getErrorResponse(e.getMessage(), Util.getConcatString("Failed to add Template ", template, " on ", entity.getDomain(), " [ ", entity.getAdtagId(), " ]")));
        }

    }

    public JsonObject getStateWiseData(DateRange dateRange) throws Exception {
        DruidFilterBean[] druidFilterBeans = {
                new DruidFilterBean(Dimension.AT_STATE, DruidConstants.ATState.BASE.getVal(), DruidConstants.Filter.EQUAL),
                new DruidFilterBean(Dimension.AT_STATE, DruidConstants.ATState.ENABLED.getVal(), DruidConstants.Filter.EQUAL)
        };

        return DruidDAL.getEntityWiseState(entity, dateRange, druidFilterBeans);

    }

    public JsonObject getTemplateData(DateRange dateRange, String businessUnit) throws Exception {

        List<Template> templates = new ArrayList<Template>();
        TimedTask<JsonObject> druidTask = new TimedTask<>(() -> new AnalyticsService(entity, dateRange).getDruidTemplates());
        JsonObject druidResponse = druidTask.doTask();

        druidResponse.addProperty("analytics-network-time", druidTask.getTimeTaken());
        druidResponse.add("system-error", new JsonArray());

        Map<String, Template> templateMap = CreativeDAL.getTemplateMap(templates);
        CreativeDAL.combineDataFromSources(templateMap, druidResponse);

        TemplateAPIService templateAPIService = new TemplateAPIService(entity, druidResponse);
        templateAPIService.mapPercentages();
        druidResponse = templateAPIService.addUrl();

        BlockingService bs = new BlockingService(entity, businessUnit);
        druidResponse = bs.applyBlocking(druidResponse);

        return druidResponse;

    }

    public EntityCustomization getCustomization() throws Exception {
        AutoTemplateConfigMaster AutoTemplateConfigMasterObj = AutoTemplateConfigMaster.getInstance();
        EntityCustomization entityCustomization = null;
        if (Util.isSet(entity) && Util.isSet(entity.getAdtagInfo())) {
            AdtagInfo adtagInfo = entity.getAdtagInfo();
            String headerText = AutoTemplateConfigMasterObj.getHeaderText(adtagInfo);
            AdAttribution adAttribution = AutoTemplateConfigMasterObj.getAdAttribution(adtagInfo);
            entityCustomization = new EntityCustomization(headerText, adAttribution);
        }
        return entityCustomization;
    }
}
