package net.media.autotemplate.dal.configs;

import com.google.common.collect.Table;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.AdtagInfo;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.bean.config.ConfigLine;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.util.Util;
import net.media.database.DatabaseException;

import java.util.ArrayList;
import java.util.List;

/*
    Created by shubham-ar
    on 23/4/18 1:04 PM   
*/
public interface ConfigDal {
    ConfigLine getConfig(String entity, String property, CreativeConstants.Level level) throws DatabaseException;

    Pair<List<ConfigLine>, List<ConfigLine>> updateConfigs(List<ConfigLine> updates, Admin admin);

    Table<String, String, ConfigLine> getConfigXml(List<Pair<String, String>> entityPropertyPairs) throws Exception;

    ConfigLine getConfig(AdtagInfo adtagInfo, String property) throws Exception;

    default ConfigLine getEntityConfig(AdtagInfo adtagInfo, String property) throws Exception {

        List<Pair<String, String>> entityPropertyPairs = new ArrayList<>();
        entityPropertyPairs.add(new Pair<>(String.valueOf(adtagInfo.entityId()), property));
        entityPropertyPairs.add(new Pair<>(String.valueOf(adtagInfo.getAdtagId()), property));
        entityPropertyPairs.add(new Pair<>(adtagInfo.getCustomerId(), property));
        entityPropertyPairs.add(new Pair<>(adtagInfo.getPartnerId(), property));

        Table<String, String, ConfigLine> configXmlTable = getConfigXml(entityPropertyPairs);

        ConfigLine entityConfig;
        entityConfig = configXmlTable.get(String.valueOf(adtagInfo.entityId()), property);

        if (!Util.isSet(entityConfig)) {
            entityConfig = configXmlTable.get(adtagInfo.getDomain(), property);
        }

        if (!Util.isSet(entityConfig)) {
            entityConfig = configXmlTable.get(String.valueOf(adtagInfo.getAdtagId()), property);
        }

        if (!Util.isSet(entityConfig)) {
            entityConfig = configXmlTable.get(adtagInfo.getCustomerId(), property);
        }

        if (!Util.isSet(entityConfig)) {
            entityConfig = configXmlTable.get(adtagInfo.getPartnerId(), property);
        }
        return entityConfig;
    }
}
