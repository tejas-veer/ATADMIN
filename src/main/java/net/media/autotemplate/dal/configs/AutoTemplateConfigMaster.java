package net.media.autotemplate.dal.configs;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.config.ConfigLine;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.DbConstants;
import net.media.autotemplate.dal.db.RowMappers;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.XmlUtil;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/*
    Created by shubham-ar
    on 12/3/18 12:52 PM   
*/
public class AutoTemplateConfigMaster extends Database implements ConfigDal {
    private static final AutoTemplateConfigMaster DB = new AutoTemplateConfigMaster(DbConstants.AUTO_TEMPLATE_ADC);
    private static final Logger LOG = LogManager.getLogger(AutoTemplateConfigMaster.class);

    private AutoTemplateConfigMaster(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static AutoTemplateConfigMaster getInstance() {
        return DB;
    }

    public ConfigLine getConfig(String entity, String property, CreativeConstants.Level level) throws DatabaseException {
        StoredProcedureCall<ConfigLine> sp = new StoredProcedureCall<>("GET_AUTO_TEMPLATE_CONFIG_MASTER", RowMappers.configLineRowMapper(level));
        sp.addParameter("entity", entity, Types.VARCHAR);
        sp.addParameter("property", property, Types.VARCHAR);
        sp.addParameter("env", null, Types.VARCHAR);

        List<ConfigLine> results = DB.executeQuery(sp);

        return results.size() > 0 ? results.get(0) : null;
    }


    public Pair<List<ConfigLine>, List<ConfigLine>> updateConfigs(List<ConfigLine> updates, Admin admin) {
        List<ConfigLine> errors = new ArrayList<>(), success = new ArrayList<>();
        updates.forEach(item -> {
            try {
                AutoTemplateDAL.insertAutoTemplateConfigMaster(item.getEntity(), item.getProperty(), item.getValue(), admin.getAdminId());
                AutoTemplateDAL.insertAutoTemplateConfigMaster(item.getLevel().name()+"_"+item.getEntity(), item.getProperty(), item.getValue(), admin.getAdminId());
                success.add(item);
            } catch (DatabaseException e) {
                errors.add(item);
                LoggingService.log(LOG, Level.error, "Error in updating Configs");
            }
        });
        return new Pair<>(success, errors);
    }


    public Table<String, String, ConfigLine> getConfigXml(List<Pair<String, String>> entityPropertyPairs) throws Exception {
        StoredProcedureCall<ConfigLine> sp = new StoredProcedureCall<>("GET_AUTO_TEMPLATE_CONFIG_MASTER_XML", RowMappers.configLineRowMapper());
        sp.addParameter("xml", XmlUtil.getEntityPropertyXml(entityPropertyPairs), Types.VARCHAR);
        List<ConfigLine> configValues = DB.executeQuery(sp);
        Table<String, String, ConfigLine> configTable = HashBasedTable.create();
        for (ConfigLine configValue : configValues) {
            configTable.put(configValue.getEntity(), configValue.getProperty(), configValue);
        }
        return configTable;
    }

    public ConfigLine getConfig(AdtagInfo adtagInfo, String property) throws Exception {
        List<Pair<String, CreativeConstants.Level>> entityPropertyPairs = new ArrayList<>();
        entityPropertyPairs.add(new Pair<>(Util.isSet(adtagInfo.entityId()) ? String.valueOf(adtagInfo.entityId()) : null, CreativeConstants.Level.ENTITY));
        entityPropertyPairs.add(new Pair<>(adtagInfo.getDomain(), CreativeConstants.Level.DOMAIN));
        entityPropertyPairs.add(new Pair<>(Util.isSet(adtagInfo.getAdtagId()) ? String.valueOf(adtagInfo.getAdtagId()) : null, CreativeConstants.Level.ADTAG));
        entityPropertyPairs.add(new Pair<>(adtagInfo.getCustomerId(), CreativeConstants.Level.CUSTOMER));
        entityPropertyPairs.add(new Pair<>(adtagInfo.getPartnerId(), CreativeConstants.Level.PARTNER));

        entityPropertyPairs.removeIf(next -> Util.isStringEmpty(next.first));

        ConfigLine configLine = null;

        for (Pair<String, CreativeConstants.Level> entityPropertyPair : entityPropertyPairs) {
            configLine = getConfig(entityPropertyPair.first, property, entityPropertyPair.second);
            if (Util.isSet(configLine))
                break;
        }
        return configLine;
    }


    //TODO : refactor these properly
    public AdAttribution getAdAttribution(AdtagInfo adtagInfo) throws Exception {
        EntityConfig entityConfig = getEntityConfig(adtagInfo, ConfigConstants.AD_ATTRIBUTION_PROPERTY);
        return Util.isSet(entityConfig) ? parseAdAttribution(entityConfig.getValue()) : null;
    }

    public String getHeaderText(AdtagInfo adtagInfo) throws Exception {
        EntityConfig entityConfig = getEntityConfig(adtagInfo, ConfigConstants.HEADER_TEXT_PROPERTY);
        return Util.isSet(entityConfig) ? entityConfig.getValue() : null;
    }

    public static AdAttribution parseAdAttribution(String string) {
        String[] split = string.split(ConfigConstants.AD_ATTRIBUTION_SEPARATOR_SPLIT);
        if (!Util.empty(split) && split.length == 2) {
            return new AdAttribution(split[0], split[1]);
        } else {
            return null;
        }
    }
}
