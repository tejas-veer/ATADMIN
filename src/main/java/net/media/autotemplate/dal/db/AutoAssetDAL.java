package net.media.autotemplate.dal.db;

import com.google.common.collect.Lists;
import net.media.autotemplate.bean.AssetDetail;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.XmlUtil;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;
import spark.Request;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AutoAssetDAL extends Database {
    private static final AutoAssetDAL SR_DB = new AutoAssetDAL(DbConstants.SEARCH_RETARGETING_ADC);
    private static final AutoAssetDAL AT_DB = new AutoAssetDAL(DbConstants.AUTO_TEMPLATE_ADC);

    public AutoAssetDAL(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static void upsertAutoAssetData(List<AssetDetail> assetList, String basis, String state, Long adminId) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("UPSERT_AUTO_ASSET_DATA_COMBINED");
        sp.addParameter("xml", XmlUtil.getUpdateAssetAssetIdCombinedXml(assetList, basis, state), Types.LONGVARCHAR);
        sp.addParameter("admin_id", adminId, Types.BIGINT);
        SR_DB.executeUpdate(sp);
    }

    public static List<AssetDetail> getAssetToReview(Request request, int reviewStatus) throws DatabaseException {
        StoredProcedureCall<AssetDetail> sp = new StoredProcedureCall<>("GET_REVIEW_ASSET_MAPPINGS", RowMappers.getAssetRowMapperForReview());
        sp.addParameter("review_status", reviewStatus, Types.LONGVARCHAR);
        sp.addParameter("asset_type", request.queryParams("asset_type"), Types.LONGVARCHAR);
        sp.addParameter("page_size", Integer.parseInt(request.queryParams("page_size")), Types.BIGINT);
        sp.addParameter("cursor_updation_date", Util.isStringSetAndDefined(request.queryParams("cursor_updation_date")) ? request.queryParams("cursor_updation_date") : null, Types.LONGVARCHAR);
        sp.addParameter("cursor_id", Integer.parseInt(request.queryParams("cursor_id")), Types.BIGINT);
        sp.addParameter("entity_name", Util.isStringSetAndDefined(request.queryParams("entity_name")) ? request.queryParams("entity_name") : null, Types.LONGVARCHAR );
        sp.addParameter("entity_value",  Util.isStringSetAndDefined(request.queryParams("entity_value")) ? request.queryParams("entity_value") : null, Types.LONGVARCHAR );
        sp.addParameter("set_id", Util.isStringSetAndDefined(request.queryParams("set_id")) ? Integer.parseInt(request.queryParams("set_id")) : null, Types.BIGINT);
        sp.addParameter("admin_name", Util.isStringSetAndDefined(request.queryParams("admin_name")) ? request.queryParams("admin_name") : null, Types.LONGVARCHAR);
        sp.addParameter("asset_value",  Util.isStringSetAndDefined(request.queryParams("asset_value")) ? request.queryParams("asset_value") : null, Types.LONGVARCHAR );

        List<AssetDetail> assetList = SR_DB.executeQuery(sp);
        return assetList;
    }

    public static void updateAutoAssetDataForReview(List<AssetDetail> assetList, Long adminId) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("UPDATE_UNDER_REVIEW_AUTO_ASSET_DATA");
        sp.addParameter("xml", XmlUtil.getUpdateReviewAssetsXml(assetList), Types.LONGVARCHAR);
        sp.addParameter("admin_id", adminId, Types.BIGINT);
        SR_DB.executeUpdate(sp);
    }


    public static List<AssetDetail> getMappedAssetsOnKey(String assetType, String entityName, List<String> entityValueList) throws DatabaseException {
        List<AssetDetail> assetList = new ArrayList<>();
        List<List<String>> partition = Lists.partition(entityValueList, AutoTemplateDAL.MYSQL_READ_WRITE_LIMIT);
        for (List<String> list : partition) {
            StoredProcedureCall<AssetDetail> sp = new StoredProcedureCall<>("GET_ASSETS_FROM_AUTO_ASSET_DATA_MAPPING", RowMappers.getAssetRowMapper());
            sp.addParameter("xml", XmlUtil.getMappedAssetXml(assetType, entityName, list), Types.LONGVARCHAR);
            assetList.addAll(AT_DB.executeQuery(sp));
        }
        return assetList;
    }

    public static List<AssetDetail> getAutoAssetsMappedOnKeyValue(List<String> keyValueList) throws DatabaseException {
        List<AssetDetail> assetList = new ArrayList<>();
        List<List<String>> partition = Lists.partition(keyValueList, AutoTemplateDAL.MYSQL_READ_WRITE_LIMIT);
        for (List<String> list : partition) {
            StoredProcedureCall<AssetDetail> sp = new StoredProcedureCall<>("GET_ASSETS_FROM_AUTO_ASSET_DATA_MAPPING", RowMappers.getAssetRowMapper());
            sp.addParameter("xml", XmlUtil.getMappedAssetForKeyValueXml(list), Types.LONGVARCHAR);
            assetList.addAll(AT_DB.executeQuery(sp));
        }
        return assetList;
    }
}












