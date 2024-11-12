package net.media.autotemplate.dal.db;

import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.autoasset.SitePropertyDetail;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.EntityParentIdsDetail;
import net.media.autotemplate.bean.autoasset.PromptDetail;
import net.media.autotemplate.bean.config.ConfigLine;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.util.Util;
import net.media.database.RowMapper;

import java.util.Arrays;
import java.util.stream.Collectors;

/*
    Created by shubham-ar
    on 19/3/18 6:53 PM   
*/
public class RowMappers {
    public static final RowMapper<ATRequest> AT_REQUEST_MAPPER = resultSet ->
            new ATRequest(Util.isStringSet(resultSet.getString("request_id")) ? Long.valueOf(resultSet.getString("request_id")) : null,
                    ATRequestType.getATRequestTypeFromDbName(resultSet.getString("request_type")),
                    resultSet.getLong("admin_id"),
                    resultSet.getString("admin_name"),
                    resultSet.getInt("is_active"),
                    resultSet.getInt("rows_processed"),
                    resultSet.getInt("rows_generated"),
                    resultSet.getString("hash"),
                    resultSet.getString("creation_date"),
                    resultSet.getInt("total_count")
            );

    public static final RowMapper<ATRequestDetail> AT_REQUEST_DETAILS = resultSet ->
            new ATRequestDetail(resultSet.getLong("task_id"),
                    resultSet.getLong("request_id"),
                    resultSet.getLong("admin_id"),
                    resultSet.getString("task_type"),
                    resultSet.getString("supply_hierarchy_level"),
                    resultSet.getString("supply_entity_value"),
                    resultSet.getString("demand_hierarchy_level"),
                    resultSet.getString("demand_entity_value"),
                    resultSet.getString("template_framework_ids"),
                    resultSet.getString("template_sizes"),
                    resultSet.getString("creative_type"),
                    resultSet.getString("mapping_type"),
                    SystemPageType.getSPTFromId(resultSet.getInt("system_page_type_id")),
                    BusinessUnit.getBUFromId(resultSet.getInt("business_unit_id")));

    public static final RowMapper<EntityParentsData> ENTITY_PARENT_DATA_MAPPER = rs -> {
        EntityParentsData entityParentsData;
        if (rs.getString("hierarchy").equalsIgnoreCase("ADGROUP")) {
            if (!Util.isStringSet(rs.getString("adgroup_id")))
                throw new Exception("Adgroup Not Synced :: Adgroup might be inactive at Perform or MAX UI");
            entityParentsData = new EntityParentsData(rs.getString("entity_id"), rs.getString("hierarchy"), Util.getCampaignKey(rs.getString("campaign_id")), Arrays.asList(Util.getAdvertiserKey(rs.getString("advertiser_id"))));
        } else if (rs.getString("hierarchy").equalsIgnoreCase("CAMPAIGN")) {
            if (!Util.isStringSet(rs.getString("campaign_id")))
                throw new Exception("Campaign Not Synced :: Campaign might be inactive at Perform or MAX UI");
            entityParentsData = new EntityParentsData(rs.getString("entity_id"), rs.getString("hierarchy"), Util.getCampaignKey(rs.getString("campaign_id")), Arrays.asList(Util.getAdvertiserKey(rs.getString("advertiser_id"))));
        } else {
            entityParentsData = new EntityParentsData(rs.getString("entity_id"), rs.getString("hierarchy"), null,
                    Arrays.stream(rs.getString("advertiser_id").split(ConfigConstants.COMMA_SEPARATED))
                            .map(adv -> Util.getAdvertiserKey(adv.trim()))
                            .collect(Collectors.toList()));
        }
        return entityParentsData;
    };

    public static RowMapper<ConfigLine> configLineRowMapper() {
        return rs -> new ConfigLine(rs.getString("entity"), rs.getString("property"), rs.getString("value"), rs.getString("environment"), rs.getString("updation_date"), rs.getString("admin_id"));
    }

    public static RowMapper<ConfigLine> configLineRowMapper(CreativeConstants.Level level) {
        return rs -> new ConfigLine(rs.getString("entity"), rs.getString("property"), rs.getString("value"), rs.getString("environment"), rs.getString("updation_date"), rs.getString("admin_id"), level);
    }

    public static RowMapper<AdAssetDetail> getAdAssetRowMapper() {
        return rs ->
                new AdAssetDetail(
                        rs.getLong("ad_id"),
                        rs.getLong("adgroup_id"),
                        rs.getString("CALL_TO_ACTION"),
                        rs.getString("DESCRIPTION"),
                        rs.getString("IMAGE_URL"),
                        rs.getString("SPONSERED_BY"),
                        rs.getString("TITLE")
                );
    }

    public static RowMapper<AssetDetail> getAssetRowMapper() {
        return rs ->
                new AssetDetail(
                        rs.getInt("id"),
                        rs.getString("entity_name"),
                        rs.getString("entity_value"),
                        rs.getString("key_value"),
                        rs.getString("key_hash"),
                        rs.getString("asset_type"),
                        rs.getLong("asset_id"),
                        Util.isStringSet(rs.getString("ext_asset_id")) ? Long.valueOf(rs.getString("ext_asset_id")) : null,
                        rs.getString("asset_value"),
                        Util.isStringSet(rs.getString("basis")) ? rs.getString("basis") : "NULL",
                        rs.getInt("set_id"),
                        Util.isStringSet(rs.getString("size")) ? rs.getString("size") : "NULL",
                        rs.getFloat("score"),
                        rs.getInt("is_active"),
                        rs.getLong("admin_id")
                );
    }

    public static RowMapper<AssetDetail> getAssetRowMapperForReview() {
        return rs ->
                new AssetDetail(
                        rs.getLong("id"),
                        rs.getString("entity_name"),
                        rs.getString("entity_value"),
                        rs.getString("key_value"),
                        rs.getString("key_hash"),
                        rs.getString("asset_type"),
                        Util.isStringSet(rs.getString("ext_asset_id")) ? Long.valueOf(rs.getString("ext_asset_id")) : null,
                        rs.getString("asset_value"),
                        Util.isStringSet(rs.getString("basis")) ? rs.getString("basis") : "NULL",
                        rs.getInt("set_id"),
                        Util.isStringSet(rs.getString("size")) ? rs.getString("size") : "NULL",
                        rs.getFloat("score"),
                        rs.getInt("review_status"),
                        rs.getLong("admin_id"),
                        rs.getString("admin_name"),
                        rs.getString("updation_date")
                );
    }

    public static RowMapper<String> getJsonObjectFromHashMapper() {
        return rs -> rs.getString("url_json");
    }

    public static RowMapper<Long> getRequestIDMapper() {
        return resultSet -> resultSet.getLong("VAR_REQUEST_ID");
    }

    public static RowMapper<AATaskDetail> AA_TASK_DETAIL_MAPPER() {
        return rs -> new AATaskDetail(
                rs.getLong("task_id"),
                rs.getLong("request_id"),
                rs.getString("task_input_details"),
                rs.getString("task_output_details"),
                ATRequestState.getATRequestStateFromDbName(rs.getString("state")),
                rs.getInt("is_active"),
                rs.getLong("admin_id")
        );

    }

    public static RowMapper<StableDiffusionImageDetail> AT_AA_STABLE_DIFFUSION_IMAGE_DETAIL_MAPPER() {
        return rs -> new StableDiffusionImageDetail(
                rs.getLong("task_id"),
                rs.getString("image_url"),
                rs.getString("keyword"),
                rs.getString("prompt"),
                rs.getString("negative_prompt"),
                rs.getString("version"),
                ATRequestState.getATRequestStateFromDbName(rs.getString("state"))
        );
    }

    public static RowMapper<PromptDetail> PROMPT_DETAIL_MAPPER() {
        return rs -> new PromptDetail(
                rs.getLong("id"),
                rs.getString("prompt"),
                rs.getString("hash")
        );
    }

    static RowMapper<EntityParentIdsDetail> GET_ENTITY_PARENT_IDS_DETAIL_MAPPER() {
        return rs -> new EntityParentIdsDetail(
                rs.getLong("ad_id"),
                rs.getLong("adgroup_id"),
                rs.getLong("campaign_id"),
                rs.getLong("advertiser_id")
        );
    }

    public static RowMapper<SitePropertyDetail> GET_CM_SITE_PROPERTY_MASTER_MAPPER() {
        return rs -> new SitePropertyDetail(
                rs.getString("site_name"),
                rs.getString("property"),
                rs.getString("value"),
                rs.getInt("is_active")
        );
    }

    public static RowMapper<TemplateMetaInfo> TEMPLATE_META_INFO_MAPPER() {
        return rs -> new TemplateMetaInfo(
                rs.getLong("template_id"),
                rs.getLong("framework_id"),
                rs.getString("template_key")
        );
    }

    public static RowMapper<TemplateMetaInfo> TEMPLATE_META_INFO_MAPPER_V2() {
        return rs -> new TemplateMetaInfo(
                rs.getLong("template_id"),
                rs.getString("template_name"),
                rs.getLong("html_framework_id"),
                rs.getString("html_framework_name"),
                rs.getString("template_key")
        );
    }
}
