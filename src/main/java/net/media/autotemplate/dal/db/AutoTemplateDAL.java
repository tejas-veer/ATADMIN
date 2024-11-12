package net.media.autotemplate.dal.db;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.EntityParentIdsDetail;
import net.media.autotemplate.bean.autoasset.PromptDetail;
import net.media.autotemplate.bean.blocking.BlockingInfo;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.factory.BeanFactory;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.XmlUtil;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by shubham.ar
 * on 12/09/17.
 */

public class AutoTemplateDAL extends Database {

    private static final AutoTemplateDAL DB = new AutoTemplateDAL(DbConstants.AUTO_TEMPLATE_ADC);
    private static final int MAX_LIMIT = 50;
    public static final int MYSQL_READ_WRITE_LIMIT = 100;
    private static Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return s;
                }
            });

    public AutoTemplateDAL(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }


    public static QueryTuple getAutoTemplateConfigMaster(String entity, String property) throws DatabaseException {

        StoredProcedureCall<QueryTuple> sp = new StoredProcedureCall<>("GET_AUTO_TEMPLATE_CONFIG_MASTER", rs -> {
            QueryTuple result = new QueryTuple(rs.getString("entity"), rs.getString("property"), rs.getString("value"));
            return result;
        });

        sp.addParameter("entity", entity, Types.VARCHAR);
        sp.addParameter("property", property, Types.VARCHAR);
        sp.addParameter("env", "", Types.VARCHAR);
        List<QueryTuple> results = DB.executeQuery(sp);
        if (results.size() > 0)
            return results.get(0);
        return null;
    }

    public static Map<String, EntityParentsData> getEntityParentData(Set<String> entityHierarchySet) throws DatabaseException {
        StoredProcedureCall<EntityParentsData> sp = new StoredProcedureCall<>("GET_PARENT_ENTITIES_XML", RowMappers.ENTITY_PARENT_DATA_MAPPER);
        sp.addParameter("xml", XmlUtil.getEntityHierarchyXml(entityHierarchySet), Types.LONGVARCHAR);
        return DB.executeQuery(sp).stream()
                .collect(Collectors.toMap(
                        data -> Util.getKey(data.getEntity().trim(), data.getHierarchy().trim().toUpperCase()),
                        data -> data,
                        (x, y) -> x));
    }

    public static Map<String, List<String>> getEntityKeyAdminMap(List<String> entityKeys) throws DatabaseException {
        StoredProcedureCall<ConfigData> sp = new StoredProcedureCall<>("GET_AUTO_TEMPLATE_CONFIG_MASTER_XML",
                rs -> {
                    ConfigData configData = new ConfigData(rs.getString("entity"), rs.getString("property"), rs.getString("value"));
                    return configData;
                });
        String xml = XmlUtil.getEntityPropertyXml(entityKeys.stream()
                .map(key -> new Pair<>(key, ConfigConstants.MAX_ACL_PROPERTY))
                .collect(Collectors.toList())
        );
        sp.addParameter("xml", xml, Types.LONGVARCHAR);
        return DB.executeQuery(sp).stream()
                .collect(Collectors.toMap(
                        ConfigData::getEntity,
                        t -> Arrays.stream(t.getValue().split(ConfigConstants.COMMA_SEPARATED)).map(String::trim).collect(Collectors.toList()),
                        (x, y) -> x)
                );
    }

    public static long insertEntityUrlMapping(Entity entity, Admin admin) throws Exception {
        StoredProcedureCall<Long> sp = new StoredProcedureCall<>("INSERT_ENTITY_URL_MAPPING", rs -> rs.getLong("entity_id"));
        sp.addParameter("adtag_id", entity.getAdtagIdAsLong(), Types.BIGINT);
        sp.addParameter("domain", entity.getDomain(), Types.VARCHAR);
        sp.addParameter("queueing_source", "TEMPLATE_ADMIN_INTERFACE", Types.VARCHAR);
        sp.addParameter("admin_id", admin.getAdminId(), Types.INTEGER);
        sp.addParameter("adtag_size", entity.getSize(), Types.VARCHAR);
        sp.addParameter("url_xml", "", Types.VARCHAR);

        List<Long> entity_id = DB.executeQuery(sp);
        if (entity_id.isEmpty())
            return -1;
        return entity_id.get(0);
    }

    public static void insertAutoTemplateConfigMaster(String entity, String property, String value, long admin) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("INSERT_AUTO_TEMPLATE_CONFIG_MASTER");
        sp.addParameter("entity", entity, Types.VARCHAR);
        sp.addParameter("property", property, Types.VARCHAR);
        sp.addParameter("value", value, Types.LONGVARCHAR);
        sp.addParameter("env", "", Types.VARCHAR);
        sp.addParameter("admin_id", admin, Types.INTEGER);
        DB.executeUpdate(sp);
    }

    public static List<BlockingInfo> getCreativeStatus(Entity entity, CreativeConstants.Type type, CreativeConstants.Level[] levels, String buSelected) throws DatabaseException {
        StoredProcedureCall<BlockingInfo> sp = new StoredProcedureCall<>("GET_ENTITY_WISE_FRAMEWORK_STATUS_XML_V3", rs -> new BlockingInfo(rs.getString("entity"), rs.getString("framework_id"), CreativeConstants.Type.valueOf(rs.getString("type")), rs.getString("framework_size"), CreativeConstants.Status.valueOf(rs.getString("status")), CreativeConstants.Level.valueOf(rs.getString("ht"))));
        sp.addParameter("type", type.name(), Types.VARCHAR);
        sp.addParameter("size", entity.getSize(), Types.VARCHAR);
        sp.addParameter("bu", BusinessUnit.getBUFromName(buSelected).getId(), Types.INTEGER);
        sp.addParameter("entity_xml", XmlUtil.getEntityBlockingXML(entity, levels), Types.VARCHAR);
        return DB.executeQuery(sp);
    }


    public static Entity getEntityInfo(Long entity_id) throws DatabaseException {
        StoredProcedureCall<Entity> sp = new StoredProcedureCall<>("GET_ENTITY_INFO", rs -> new Entity(entity_id, rs.getString("adtag_id"), rs.getString("domain"), rs.getString("adtag_size"), rs.getString("customer_id"), rs.getString("partner_id")));
        sp.addParameter("entity_id", entity_id, Types.BIGINT);
        List<Entity> entityList = DB.executeQuery(sp);
        if (entityList.isEmpty()) {
            return null;
        } else {
            Entity entity = entityList.get(0);
            entity.setAdtagInfo(BeanFactory.makeAdtagInfo(entity));
            return entity;
        }
    }

    public static List<Long> getEntityId(String domain, Long adTag) throws Exception {
        StoredProcedureCall<Long> sp = new StoredProcedureCall<>("GET_ENTITY_ID", rs -> rs.getLong("id"));
        sp.addParameter("adtag_id", adTag, Types.BIGINT);
        sp.addParameter("domain", domain, Types.VARCHAR);
        return DB.executeQuery(sp);
    }

    public static List<EntityInfo> getEntityIds(List<Pair<String, String>> domainAdtagList) throws Exception {
        StoredProcedureCall<EntityInfo> sp = new StoredProcedureCall<>("GET_ENTITY_ID_XML",
                rs -> {
                    EntityInfo entityInfo = new EntityInfo(rs.getString("domain"), rs.getString("adtag_id"), rs.getLong("id"));
                    return entityInfo;
                });
        sp.addParameter("xml", XmlUtil.getEntityXml(domainAdtagList), Types.LONGVARCHAR);
        return DB.executeQuery(sp);
    }

    @Deprecated
    public static List<Template> getCMTemplatesForEntity(Entity entity) throws DatabaseException {
        StoredProcedureCall<Template> sp = new StoredProcedureCall<>("GET_ALL_CM_TEMPLATES_FOR_ENTITY_V2", rs -> new Template(rs.getString("cm_template_id"), rs.getBoolean("is_active"), rs.getInt("global_rank"), rs.getString("framework_id"), rs.getInt("template_source")));
        sp.addParameter("entity_id", entity.getEntityId(), Types.VARCHAR);
        return DB.executeQuery(sp);
    }

    public static void logUserAction(Admin admin, String action, String comment) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("INSERT_INTO_ADMIN_ACTION_LOGS");
        sp.addParameter("admin_id", admin.getAdminId(), Types.BIGINT);
        sp.addParameter("admin_name", admin.getAdminName(), Types.VARCHAR);
        sp.addParameter("admin_ip", "", Types.VARCHAR);
        sp.addParameter("action", action, Types.VARCHAR);
        sp.addParameter("comment", comment, Types.LONGVARCHAR);
        sp.addParameter("test_id", null, Types.BIGINT);
        sp.addParameter("bucket_id", null, Types.BIGINT);
        DB.executeUpdate(sp);
    }


    public static List<String> getCachedUrlsForEntity(Entity entity) throws DatabaseException {
        StoredProcedureCall<String> sp = new StoredProcedureCall<>("GET_CACHED_URLS_FOR_ENTITY", rs -> rs.getString("url"));
        sp.addParameter("entity_id", entity.getEntityId(), Types.BIGINT);
        return DB.executeQuery(sp);
    }

    /*
    only for testing
     */

    public static void updateAutoTemplateStatus(Entity entity, List<String> enabled, List<String> disabled, Admin admin) throws DatabaseException {
        String updateXML = XmlUtil.getEntityUpdateXML(enabled, disabled);
        StoredProcedureCall sp = new StoredProcedureCall("UPDATE_AUTO_TEMPLATE_STATUS_V2");
        sp.addParameter("entity_id", entity.getEntityId(), Types.BIGINT);
        sp.addParameter("admin_id", admin.getAdminId(), Types.BIGINT);
        sp.addParameter("xml", updateXML, Types.VARCHAR);
        DB.executeUpdate(sp);
    }


    public static void deleteTemplatesForEntity(List<String> entities, Admin admin) throws DatabaseException {
        int processedCounter = 0;
        while (processedCounter < entities.size()) {
            List<String> entitySublist = Util.safeSublist(entities, processedCounter, processedCounter + MAX_LIMIT);
            StoredProcedureCall<Long> sp = new StoredProcedureCall<>("DELETE_TEMPLATES_FOR_ENTITIY");
            sp.addParameter("entity_xml", XmlUtil.getEntityListXML(entitySublist), Types.VARCHAR);
            sp.addParameter("admin_id", admin.getAdminId(), Types.BIGINT);
            DB.executeUpdate(sp);
            processedCounter += MAX_LIMIT;
        }

    }

    /**
     * This method is used in the servlet so doesn't report as usage
     *
     * @return
     * @throws DatabaseException
     */
    public static List<Pair<String, String>> getFrameworkDefaultTemplates() throws DatabaseException {
        StoredProcedureCall<Pair<String, String>> sp = new StoredProcedureCall<>("GET_FRAMEWORK_TRANSFORMATIONS_FOR_ENTITY_MAPPING",
                rs -> new Pair<>(rs.getString("framework_id"), rs.getString("default_template_id"))
        );
        sp.addParameter("include_all", 1, Types.TINYINT);
        return DB.executeQuery(sp);
    }


    public static List<String> getEntityIds(CreativeConstants.Level level, String id) throws DatabaseException {
        StoredProcedureCall<String> sp = new StoredProcedureCall<String>("GET_ENTITY_HIERARCHY_MASTER_DATA", r -> r.getString("id"));
        sp.addParameter("property", level.getDatabaseColumn(), Types.VARCHAR);
        sp.addParameter("value", id, Types.VARCHAR);
        return DB.executeQuery(sp);
    }

    public static void insertEntityHierarchyMaster(List<Pair<String, String>> domainAdTag, long adminId) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("INSERT_ENTITY_HIERARCHY_MASTER_XML");
        sp.addParameter("xml", XmlUtil.getEntityInsertionXml(domainAdTag), Types.LONGVARCHAR);
        sp.addParameter("queueing_source", "TEMPLATE_ADMIN_INTERFACE", Types.VARCHAR);
        sp.addParameter("admin_id", adminId, Types.INTEGER);
        DB.executeUpdate(sp);
    }

    public static Long insertATRequest(BulkRequest bulkRequest, String requestType, Long adminId) throws DatabaseException {
        String queryString = "AT_INSERT_REQUEST_DETAILS";
        StoredProcedureCall<Long> storedProcedureCall = new StoredProcedureCall<>(queryString, resultSet -> resultSet.getLong("VAR_REQUEST_ID"));
        storedProcedureCall.addParameter("xml", XmlUtil.getATRequestXML(bulkRequest), Types.LONGVARCHAR);
        storedProcedureCall.addParameter("request_type", requestType, Types.VARCHAR);
        storedProcedureCall.addParameter("admin_id", adminId, Types.BIGINT);
        return DB.executeQuery(storedProcedureCall).get(0);
    }

    public static List<ATRequestDetail> getRequestDetails(int limit) throws DatabaseException {
        String queryString = "AT_GET_REQUEST_DETAILS";
        StoredProcedureCall<ATRequestDetail> storedProcedureCall = new StoredProcedureCall<>(queryString, RowMappers.AT_REQUEST_DETAILS);
        storedProcedureCall.addParameter("limit", limit, Types.INTEGER);
        return DB.executeQuery(storedProcedureCall);
    }

    public static void updateRequestDetails(List<ATRequestDetail> requestDetails) throws DatabaseException {
        String queryString = "AT_UPDATE_REQUEST_DETAILS";
        StoredProcedureCall<Void> storedProcedureCall = new StoredProcedureCall<>(queryString);
        storedProcedureCall.addParameter("xml", XmlUtil.getATRequestDetailsXML(requestDetails), Types.LONGVARCHAR);
        storedProcedureCall.addParameter("is_active", 0, Types.TINYINT);
        DB.executeUpdate(storedProcedureCall);
    }

    public static AdAssetDetail getAssetsFromAdId(String adId) throws DatabaseException {
        StoredProcedureCall<AdAssetDetail> sp = new StoredProcedureCall<>("AT_ADMIN_GET_AD_DATA", RowMappers.getAdAssetRowMapper());
        sp.addParameter("xml", XmlUtil.getAdIdXml(adId), Types.LONGVARCHAR);
        List<AdAssetDetail> adAssetDetails = DB.executeQuery(sp);
        return adAssetDetails.get(0);
    }

    public static void insertHash(String payload, String hash, Long adminId) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("AT_INSERT_INTERFACE_URL_HASH_MAPPING");
        sp.addParameter("url_hash", hash, Types.LONGVARCHAR);
        sp.addParameter("url_json", payload, Types.LONGVARCHAR);
        sp.addParameter("version", 1, Types.BIGINT);
        sp.addParameter("admin", adminId, Types.BIGINT);
        DB.executeUpdate(sp);
    }

    public static String getReportingPayloadFromHash(String hash) throws DatabaseException {
        String queryString = "AT_GET_INTERFACE_URL_PARAMS";
        StoredProcedureCall<String> storedProcedureCall = new StoredProcedureCall<>(queryString, RowMappers.getJsonObjectFromHashMapper());
        storedProcedureCall.addParameter("url_hash", hash, Types.LONGVARCHAR);
        storedProcedureCall.addParameter("version", 1, Types.BIGINT);
        return DB.executeQuery(storedProcedureCall).toString();
    }

    public static List<TemplateMetaInfo> getTemplateMetaData(String templateId) throws DatabaseException {
        String procedureName = "GET_GBL_TEMPLATE_MASTER";
        StoredProcedureCall<TemplateMetaInfo> sp = new StoredProcedureCall<>(procedureName, RowMappers.TEMPLATE_META_INFO_MAPPER());
        sp.addParameter("xml", XmlUtil.getTemplateIdXML(Arrays.asList(templateId)), Types.LONGVARCHAR);
        return DB.executeQuery(sp);
    }

    //Common
    public static List<ATRequest> getAARequestForAdminId(int limit, int offset, long adminId, List<ATRequestType> atRequestTypeList) throws DatabaseException {
        String procedureName = "AT_GET_REQUEST_MASTER_V3";
        StoredProcedureCall<ATRequest> sp = new StoredProcedureCall<>(procedureName, RowMappers.AT_REQUEST_MAPPER);
        sp.addParameter("limit", limit, Types.INTEGER);
        sp.addParameter("offset", offset, Types.INTEGER);
        sp.addParameter("admin_id", adminId, Types.BIGINT);
        sp.addParameter("xml", XmlUtil.getRequestMasterGetXml(atRequestTypeList), Types.VARCHAR);
        return DB.executeQuery(sp);
    }

    //Common
    public static void updateATRequestMaster() throws DatabaseException {
        String procedureName = "AT_UPDATE_REQUEST_MASTER_V2";
        StoredProcedureCall sp = new StoredProcedureCall(procedureName);
        DB.executeUpdate(sp);
    }

    //AA
    public static List<AATaskDetail> getAATaskDetails(int limit, ATRequestType requestType, ATRequestState state) throws DatabaseException {
        String procedureName = "AT_AA_GET_REQUEST_TASK_DETAILS";
        StoredProcedureCall<AATaskDetail> sp = new StoredProcedureCall<>(procedureName, RowMappers.AA_TASK_DETAIL_MAPPER());
        sp.addParameter("task_type", requestType.getDbName(), Types.VARCHAR);
        sp.addParameter("state", state.getDbName(), Types.VARCHAR);
        sp.addParameter("limit", limit, Types.INTEGER);
        return DB.executeQuery(sp);
    }

    //AA
    public static List<StableDiffusionImageDetail> getStableDiffusionImageDetailForRequest(long requestId) throws DatabaseException {
        String procedureName = "AT_GET_STABLE_DIFFUSION_IMAGE_FOR_REQUEST";
        StoredProcedureCall<StableDiffusionImageDetail> sp = new StoredProcedureCall<>(procedureName, RowMappers.AT_AA_STABLE_DIFFUSION_IMAGE_DETAIL_MAPPER());
        sp.addParameter("request_id", requestId, Types.BIGINT);
        return DB.executeQuery(sp);
    }

    //AA
    public static Long insertAATaskDetails(List<AATaskDetail> aaTaskDetailList, ATRequestType requestType, ATRequestState state, long adminId) throws DatabaseException {
        String procedureName = "AT_AA_INSERT_REQUEST_TASK_DETAILS_V2";
        StoredProcedureCall<Long> sp = new StoredProcedureCall<>(procedureName, RowMappers.getRequestIDMapper());
        sp.addParameter("xml", XmlUtil.getAATaskDetailInsertXML(aaTaskDetailList), Types.LONGVARCHAR);
        sp.addParameter("request_type", requestType.getDbName(), Types.VARCHAR);
        sp.addParameter("state", state.getDbName(), Types.VARCHAR);
        sp.addParameter("admin_id", adminId, Types.BIGINT);
        return DB.executeQuery(sp).get(0);
    }

    //AA
    public static void updateAATaskDetails(List<AATaskDetail> aaTaskDetailList) throws DatabaseException {
        String procedureName = "AT_AA_UPDATE_REQUEST_TASK_DETAILS_V2";
        StoredProcedureCall sp = new StoredProcedureCall(procedureName);
        sp.addParameter("xml", XmlUtil.getAATaskDetailsUpdateXML(aaTaskDetailList), Types.LONGVARCHAR);
        DB.executeUpdate(sp);
    }

    //AA
    public static void suspendAARequest(long requestId) throws DatabaseException {
        String procedureName = "AT_AA_CANCEL_REQUEST";
        StoredProcedureCall sp = new StoredProcedureCall(procedureName);
        sp.addParameter("request_id", requestId, Types.BIGINT);
        DB.executeUpdate(sp);
    }

    //AA
    public static List<PromptDetail> insertPromptDetails(List<String> promptList) throws DatabaseException {
        String procedureName = "INSERT_PROMPT_DETAILS";
        StoredProcedureCall<PromptDetail> sp = new StoredProcedureCall<>(procedureName, RowMappers.PROMPT_DETAIL_MAPPER());
        sp.addParameter("xml", XmlUtil.getPromptDetailInsertXML(promptList), Types.LONGVARCHAR);
        return DB.executeQuery(sp);
    }

    //AA
    public static List<PromptDetail> getPromptDetails(List<Long> promptIdList) throws DatabaseException {
        String procedureName = "GET_PROMPT_DETAILS";
        StoredProcedureCall<PromptDetail> sp = new StoredProcedureCall<>(procedureName, RowMappers.PROMPT_DETAIL_MAPPER());
        sp.addParameter("xml", XmlUtil.getPromptDetailGetXML(promptIdList), Types.LONGVARCHAR);
        return DB.executeQuery(sp);
    }

    //AA
    public static List<AATaskDetail> getTaskOutputDetailsForRequest(Long requestId) throws DatabaseException {
        String procedureName = "AT_GET_TASK_OUTPUT_DETAILS_FOR_REQUEST";
        StoredProcedureCall<AATaskDetail> sp = new StoredProcedureCall<>(procedureName, RowMappers.AA_TASK_DETAIL_MAPPER());
        sp.addParameter("request_id", requestId, Types.BIGINT);
        return DB.executeQuery(sp);
    }

    //AA
    public static List<EntityParentIdsDetail> getEntityParentIdsDetail(String entityName, String entityValue) throws DatabaseException {
        String procedureName = "GET_MAX_ENTITY_DETAILS";
        StoredProcedureCall<EntityParentIdsDetail> sp = new StoredProcedureCall<>(procedureName, RowMappers.GET_ENTITY_PARENT_IDS_DETAIL_MAPPER());
        sp.addParameter("entity_name", entityName, Types.VARCHAR);
        sp.addParameter("entity_value", entityValue, Types.VARCHAR);
        return DB.executeQuery(sp);
    }

    public static String getCachedFrameworkId(String templateId) throws DatabaseException {
        String framework = cache.getIfPresent(templateId);
        if (Objects.isNull(framework)) {
            List<TemplateMetaInfo> templateMetaData = getTemplateMetaData(templateId);
            framework = String.valueOf(templateMetaData.get(0).getFrameworkId());
            if (Objects.isNull(framework)) {
                framework = "";
            }
            cache.put(templateId, framework);
        }
        return framework;
    }

    public static String getTemplateKey(String templateId) {
        try {
            List<TemplateMetaInfo> templateMetaData = getTemplateMetaData(templateId);
            return templateMetaData.get(0).getTemplateKey();
        } catch (Exception e) {
            //ignored Exception
        }
        return null;
    }
}