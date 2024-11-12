package net.media.autotemplate.dal.configs;

import net.media.autotemplate.bean.config.ConfigLine;
import net.media.autotemplate.bean.config.ConfigMeta;
import net.media.autotemplate.dal.db.DbConstants;
import net.media.autotemplate.dal.db.RowMappers;
import net.media.database.Database;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/*
    Created by shubham-ar
    on 23/4/18 5:31 PM   
*/
public class ConfigMetaDal extends Database {

    private final String metaKey;

    public ConfigMetaDal(String metaKey) {
        super(DbConstants.AUTO_TEMPLATE_ADC);
        this.metaKey = metaKey;
    }

    private List<ConfigLine> getAllProperties() throws Exception {
        StoredProcedureCall<ConfigLine> sp = new StoredProcedureCall<ConfigLine>("GET_AUTO_TEMPLATE_CONFIG_MASTER_V2", RowMappers.configLineRowMapper());
        sp.addParameter("property", this.metaKey, Types.VARCHAR);
        return executeQuery(sp);
    }

    public List<ConfigMeta> getMetaList() throws Exception {
        List<ConfigLine> metas = getAllProperties();
        List<ConfigMeta> configMetas = new ArrayList<>();
        for (ConfigLine meta : metas) {
            configMetas.add(new ConfigMeta(meta.getEntity(), meta.getValue()));
        }
        return configMetas;
    }
}
