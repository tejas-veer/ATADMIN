package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.config.ConfigLine;
import net.media.autotemplate.bean.config.ConfigMeta;
import net.media.autotemplate.bean.config.ConfigUpdate;
import net.media.autotemplate.constants.ConfigProperties;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.configs.AutoTemplateConfigMaster;
import net.media.autotemplate.dal.configs.ConfigDal;
import net.media.autotemplate.dal.configs.ConfigMetaDal;
import net.media.autotemplate.dal.configs.FeatureMappingConfigDal;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.factory.BeanFactory;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.autotemplate.util.logging.UserActionLogging;
import net.media.database.DatabaseException;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/*
    Created by shubham-ar
    on 14/3/18 4:26 PM   
*/
public class ConfigsService {
    private static final Logger LOG = LogManager.getLogger(ConfigsService.class);

    public static final Gson GSON = Util.getGson();
    private final String entity;
    private final CreativeConstants.Level level;
    private final AdtagInfo adtagInfo;
    private final Admin admin;
    private final ConfigDal configDal;
    private final ConfigMetaDal configMetaDal;
    private final String entityIdentifier;
    private final BusinessUnit businessUnit;
    private final AclStatus aclStatus;

    public ConfigsService(String entity, CreativeConstants.Level level, Admin admin, ConfigDal configDal, ConfigMetaDal configMetaDal, BusinessUnit businessUnit) throws ExecutionException, DatabaseException {
        this.entity = entity;
        this.level = level;
        this.admin = admin;
        this.configDal = configDal;
        this.configMetaDal = configMetaDal;
        this.adtagInfo = BeanFactory.makeAdtagInfo(entity, level);
        this.entityIdentifier = level.name() + "|" + entity;
        this.businessUnit = businessUnit;
        this.aclStatus = AclService.checkAccess(entity, level, admin, this.businessUnit);
    }

    public ConfigsService(String entity, CreativeConstants.Level level, Admin admin, BusinessUnit businessUnit) throws ExecutionException, DatabaseException {
        this(entity, level, admin, AutoTemplateConfigMaster.getInstance(), new ConfigMetaDal(ConfigMeta.GENERATOR_META), businessUnit);
    }

    public String getEntity() {
        return entity;
    }

    public CreativeConstants.Level getLevel() {
        return level;
    }

    public Admin getAdmin() {
        return admin;
    }

    private static boolean isGeneratorAttribute(String name) {
        for (ConfigProperties configProperties : ConfigProperties.values()) {
            if (configProperties.name().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public JsonObject getConfigs() throws Exception {
        List<ConfigMeta> generatorMetas = configMetaDal.getMetaList();
        List<ConfigLine> configLines = new ArrayList<>();
        Boolean featureMapping = null;
        JsonObject modalData = null;
        try {
            featureMapping = (
                    level.equals(CreativeConstants.Level.CUSTOMER)
                            || level.equals(CreativeConstants.Level.PARTNER)
                            || level.equals(CreativeConstants.Level.ADTAG)
            ) ? FeatureMappingConfigDal.getFeatureMappingStatus(adtagInfo) : true;
            //todo: move to another class : GLOBAL_CONFIG ;
            String property = "ADMIN_INTERFACE_PROPERTY";
            String entity = "FEATURE_MAPPING_MODAL";
            QueryTuple featureMappingModalJson = AutoTemplateDAL.getAutoTemplateConfigMaster(entity, property);
            modalData = GSON.fromJson(featureMappingModalJson.getValue(), JsonObject.class);
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "Database Data Error", e);
        }

        generatorMetas.forEach(generatorMeta -> {
            try {
                ConfigLine configLine = configDal.getConfig(adtagInfo, generatorMeta.getEntity());
                if (Util.isSet(configLine)) {
                    if (isGeneratorAttribute(configLine.getProperty())) {
                        ConfigProperties config = ConfigProperties.valueOf(configLine.getProperty());
                        configLine.setValue(config.getParser().apply(configLine.getValue()));
                    }
                    configLines.add(configLine);
                }

            } catch (Exception e) {
                LoggingService.log(LOG, Level.error, "Error in fetching Configs", e);
            }
        });

        JsonArray configsJson = GSON.toJsonTree(configLines).getAsJsonArray();
        JsonObject configObject = new JsonObject();
        configObject.addProperty("featureMapping", featureMapping);
        configObject.add("modalData", modalData);
        configObject.add("data", configsJson);
        configObject.add("meta", GSON.toJsonTree(generatorMetas).getAsJsonArray());
        configObject.addProperty("aclStatus", this.aclStatus.getStatusCode());
        return configObject;
    }

    public ApiResponse writeUpdate(ConfigUpdate updates) throws DatabaseException {

        UserActionLogging.log(admin, this.configDal.getClass().getSimpleName() + "|CONFIG_UPDATE|" + entityIdentifier, GSON.toJson(updates, ConfigUpdate.class));
        updates.getAdditions().forEach(item -> {
            ConfigProperties property = ConfigProperties.valueOf(item.getProperty());
            item.setValue(property.getFormatter().apply(item.getValue()));
            item.setLevel(level);
            item.setEntity(this.entity);
        });

        Pair<List<ConfigLine>, List<ConfigLine>> updateStatus = configDal.updateConfigs(updates.getAdditions(), admin);

        JsonObject response = new JsonObject();
        response.add("success", GSON.toJsonTree(updateStatus.first));
        response.add("errors", GSON.toJsonTree(updateStatus.second));

        if (updates.isEnableFeatureMapping()) {
            Pair<Long, String> featureMappingResult = FeatureMappingConfigDal.enableFeatureMappingStatus(entity, level, admin.getAdminId());
            response.add("featureMappingUpdate", GSON.toJsonTree(featureMappingResult));
            UserActionLogging.log(admin, this.configDal.getClass().getSimpleName() + "|FEATURE_MAPPING_UPDATES|" + entityIdentifier, "Enabled feature mapping for " + level.name() + " " + entity);
        }

        if (updates.isReset()) {
            if (level.equals(CreativeConstants.Level.GLOBAL))
                return new ApiResponse(HttpStatus.SC_UNPROCESSABLE_ENTITY, "Resetting Global will Reset Entire Auto Template");

            List<String> entities = AutoTemplateDAL.getEntityIds(level, entity);
            AutoTemplateDAL.deleteTemplatesForEntity(entities, admin);
            UserActionLogging.log(admin, this.configDal.getClass().getSimpleName() + "|CONFIG_UPDATE_ENTITY_DELETES|" + entityIdentifier, GSON.toJson(entities));
        }

        return new ApiResponse(response);
    }
}
