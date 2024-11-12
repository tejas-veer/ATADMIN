package net.media.autotemplate.dal.configs;

import net.media.autotemplate.bean.AdtagInfo;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.DbConstants;
import net.media.autotemplate.factory.BeanFactory;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.utils.CollectionUtils;

import java.sql.Types;
import java.util.List;
import java.util.Objects;

/*
    Created by shubham-ar
    on 26/7/18 3:22 PM   
*/
public class FeatureMappingConfigDal extends Database {
    private static final FeatureMappingConfigDal INSTANCE = new FeatureMappingConfigDal(DbConstants.CM_GLOBAL_MASTER);
    private static final String AT_ATTRIBUTE_NAME = "enable_kbb_auto_template_api";
    private static final Logger LOG = LogManager.getLogger(FeatureMappingConfigDal.class);

    private FeatureMappingConfigDal(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static boolean getFeatureMappingStatus(AdtagInfo adtagInfo) throws DatabaseException {
        CreativeConstants.Level[] levels = CreativeConstants.Level.values();
        for (int i = 0; i < levels.length; i++) {
            String entityId = levels[i].AdtagInfoGetter().apply(adtagInfo);
            if (Objects.isNull(entityId))
                continue;
            Boolean status = getFeatureMappingStatus(entityId);
            if (Objects.nonNull(status) && status.booleanValue())
                return status;
        }
        return false;
    }

    public static Boolean getFeatureMappingStatus(String entityId) throws DatabaseException {
        StoredProcedureCall<Pair<String, String>> sp = new StoredProcedureCall<>("CM_Get_Entity_Attribute_Details", rs -> new Pair<>(rs.getString("entity_id"), rs.getString("attribute_value")));
        sp.addParameter("entity_id", entityId, Types.VARCHAR);
        sp.addParameter("attribute_name", AT_ATTRIBUTE_NAME, Types.VARCHAR);
        List<Pair<String, String>> statusList = INSTANCE.executeQuery(sp);
        if (!CollectionUtils.isEmpty(statusList)) {
            for (Pair<String, String> statusRow : statusList) {
                if (statusRow.first.equals(entityId)) {
                    return statusRow.second.trim().equals("1");
                }
            }
        }
        return null;
    }

    public static Long getFeatureMappingStatusRow(String entityId) throws DatabaseException {
        StoredProcedureCall<Pair<String, Long>> sp = new StoredProcedureCall<>("CM_Get_Entity_Attribute_Details", rs -> new Pair<>(rs.getString("entity_id"), rs.getLong("id")));
        sp.addParameter("entity_id", entityId, Types.VARCHAR);
        sp.addParameter("attribute_name", AT_ATTRIBUTE_NAME, Types.VARCHAR);
        List<Pair<String, Long>> statusList = INSTANCE.executeQuery(sp);
        if (!CollectionUtils.isEmpty(statusList)) {
            for (Pair<String, Long> statusRow : statusList) {
                if (statusRow.first.equals(entityId)) {
                    return statusRow.second;
                }
            }
        }
        return null;
    }

    public static Pair<Long, String> addFeatureMapping(String entityId, CreativeConstants.Level level, int status, long adminId) throws DatabaseException {
        StoredProcedureCall<Pair<Long, String>> getEntityStatus = new StoredProcedureCall<>("CM_Add_Entity_Attribute_Mapping", rs -> new Pair<>(rs.getLong("Error_Number"), rs.getString("Error_Message")));
        getEntityStatus.addParameter("entity_attribute_list", getAddXML(entityId, level, status), Types.VARCHAR);
        getEntityStatus.addParameter("admin_id", adminId, Types.BIGINT);
        List<Pair<Long, String>> result = INSTANCE.executeQuery(getEntityStatus);
        return result.get(0);
    }

    private static String getAddXML(String entityId, CreativeConstants.Level level, int status) {
        return String.format("<header><row entity_id=\"%s\"   attribute_name=\"%s\" attribute_value=\"%d\" entity_type=\"%s\"></row></header>", entityId, AT_ATTRIBUTE_NAME, status, level.name());
    }

    public static Pair<Long, String> updateFeatureMapping(long rowId, int status, long adminId) throws DatabaseException {
        StoredProcedureCall<Pair<Long, String>> getEntityStatus = new StoredProcedureCall<>("CM_Update_Entity_Attribute_Mapping", rs -> new Pair<>(rs.getLong("Error_Number"), rs.getString("Error_Message")));
        getEntityStatus.addParameter("entity_attribute_list", getUpdateXML(rowId, status), Types.VARCHAR);
        getEntityStatus.addParameter("admin_id", adminId, Types.BIGINT);
        List<Pair<Long, String>> result = INSTANCE.executeQuery(getEntityStatus);
        return result.get(0);
    }

    private static String getUpdateXML(long id, int status) {
        return String.format("<header><row id=\"%s\"   attribute_value=\"%s\" is_active =\"1\"></row></header>", id, status);
    }

    public static Pair<Long, String> enableFeatureMappingStatus(String entityId, CreativeConstants.Level level, long adminId) throws DatabaseException {
        adminId = Math.abs(adminId);
        Pair<Long, String> result;
        Long rowId = getFeatureMappingStatusRow(entityId);
        if (Objects.isNull(rowId)) {
            result = addFeatureMapping(entityId, level, 1, adminId);
        } else {
            result = updateFeatureMapping(rowId, 1, adminId);
        }
        return result;
    }

    public static void main(String[] args) throws DatabaseException {
        String[] custs = {"8CUI07471", "8CU1Y0O10", "8CU29243D"};
        for (String custID : custs) {
            AdtagInfo ati = BeanFactory.makeAdtagInfo(custID, CreativeConstants.Level.CUSTOMER);
            System.out.println(getFeatureMappingStatus(ati));
            Long rowId = getFeatureMappingStatusRow(custID);
            updateFeatureMapping(rowId, 0, 16258);
            System.out.println(getFeatureMappingStatus(custID));
        }
    }
}
