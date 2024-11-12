package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.bean.autoasset.EntityParentIdsDetail;
import net.media.autotemplate.bean.autoasset.SitePropertyDetail;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.CMSitePropertyMaster;
import net.media.autotemplate.enums.AADemandLevel;
import net.media.database.DatabaseException;
import net.media.database.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.media.autotemplate.constants.AutoAssetConstants.ALL_DEMAND;
import static net.media.autotemplate.constants.ConfigConstants.*;
import static net.media.autotemplate.enums.AADemandLevel.*;

public class AutoAssetConfigService {
    private static final String CONFIG_NAME = "AUTO_ASSET_CONFIG";
    private static final List<String> PROPERTIES_LIST = Arrays.asList(
            "AUTO_ASSET_TEST_PERCENTAGE",
            "IMAGE_TEST_PERCENTAGE",
            "TITLE_TEST_PERCENTAGE",
            "C2A_TEST_PERCENTAGE",
            "ONLY_AA_C2A",
            "ONLY_AA_IMAGE",
            "ONLY_AA_TITLE"
    );

    public static JsonObject getAutoAssetConfigForEntity(String entityName, String entityValue) throws Exception {
        JsonObject jsonResponse = new JsonObject();
        EntityParentIdsDetail entityParentIdsDetails = null;
        if (isDemandPresentInEntity(entityName, entityValue)) {
            entityParentIdsDetails = getEntityDetails(entityName, entityValue);
            jsonResponse.add("entity_ids", new Gson().toJsonTree(entityParentIdsDetails));
        }

        List<Pair<String, String>> siteNameAndPropertiesList = getSiteNameToPropertiesListForEntityName(entityName, entityValue, entityParentIdsDetails);
        List<SitePropertyDetail> sitePropertyDetailValueList = CMSitePropertyMaster.getCmSitePropertyMasterValue(siteNameAndPropertiesList);
        List<SitePropertyDetail> sitePropertyDetailValueWithEntity = updateAAConfigWithEntityNameAndValue(sitePropertyDetailValueList, entityParentIdsDetails, entityValue);
        sitePropertyDetailValueWithEntity = filterAAConfigByIsActive(sitePropertyDetailValueWithEntity, 1);
        jsonResponse.add("config", new Gson().toJsonTree(sitePropertyDetailValueWithEntity));
        return jsonResponse;
    }

    private static boolean isDemandPresentInEntity(String entityName, String entityValue) {
        return !GLOBAL.getName().equals(entityName) && !ALL_DEMAND.equals(entityValue.split(DOUBLE_DOLLAR_REGEX_SEPARATOR).length > 1 ? entityValue.split(DOUBLE_DOLLAR_REGEX_SEPARATOR)[1] : entityValue);
    }

    private static EntityParentIdsDetail getEntityDetails(String entityName, String entityValue) throws Exception {
        String demandEntityName = getDemandFromEntityName(entityName);
        String demandIdFromEntityValue = getDemandIdFromEntityValue(entityValue);
        List<EntityParentIdsDetail> entityParentIdsDetailList = AutoTemplateDAL.getEntityParentIdsDetail(demandEntityName, demandIdFromEntityValue);
        if (entityParentIdsDetailList.size() > 0) {
            return entityParentIdsDetailList.get(0);
        } else {
            EntityParentIdsDetail entityParentIdsDetail = new EntityParentIdsDetail();
            String[] entityNameSplits = entityName.split(DOUBLE_DOLLAR_REGEX_SEPARATOR);
            String[] entityValueSplits = entityValue.split(DOUBLE_DOLLAR_REGEX_SEPARATOR);
            if(entityNameSplits.length > 1){
                entityName = entityNameSplits[1];
                entityValue = entityValueSplits[1];
            }

            switch (entityName) {
                case "AD_ID":
                    entityParentIdsDetail.setAdId(Long.parseLong(entityValue));
                    break;
                case "ADGROUP_ID":
                    entityParentIdsDetail.setAdGroupId(Long.parseLong(entityValue));
                    break;
                case "CAMPAIGN_ID":
                    entityParentIdsDetail.setCampaignId(Long.parseLong(entityValue));
                    break;
                case "ADVERTISER_ID":
                    entityParentIdsDetail.setAdvertiserId(Long.parseLong(entityValue));
                    break;
            }
            return entityParentIdsDetail;
        }
    }

    private static String getDemandIdFromEntityValue(String entityValue) {
        String id = entityValue;
        String[] entityValueSplit = entityValue.split(DOUBLE_DOLLAR_REGEX_SEPARATOR);
        if (entityValueSplit.length > 1) {
            id = entityValueSplit[1];
        }
        return id;
    }

    private static String getDemandFromEntityName(String entityName) {
        String entityNameSplit[] = entityName.split(DOUBLE_DOLLAR_REGEX_SEPARATOR);
        if (entityNameSplit.length > 1) {
            entityName = entityNameSplit[1];
        }
        return entityName;
    }

    public static void updateConfig(List<SitePropertyDetail> sitePropertyDetailList, String adminEmail) throws DatabaseException {
        CMSitePropertyMaster.updateCmSitePropertyMasterValue(filterAAConfigByIsActive(sitePropertyDetailList, 1), 1, adminEmail);
        CMSitePropertyMaster.updateCmSitePropertyMasterValue(filterAAConfigByIsActive(sitePropertyDetailList, 0), 0, adminEmail);
    }

    private static List<SitePropertyDetail> filterAAConfigByIsActive(List<SitePropertyDetail> sitePropertyDetailList, int isActive) {
        return sitePropertyDetailList.stream()
                .filter(aaConfig -> aaConfig.getIsActive() == isActive)
                .collect(Collectors.toList());
    }

    private static List<SitePropertyDetail> updateAAConfigWithEntityNameAndValue(List<SitePropertyDetail> sitePropertyDetailList, EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        Map<String, String> entityNameToValueMap = getEntityNameToValueMap(entityParentIdsDetail, entityValue);
        sitePropertyDetailList.forEach(aaConfigValue -> {
            String[] siteNameSplit = splitStringForFirstOccurrence(aaConfigValue.getSiteName(), DOLLAR_SEPARATOR);
            if (siteNameSplit.length > 1) {
                String key = siteNameSplit[1];
                aaConfigValue.setEntityValue(key);
                aaConfigValue.setEntityName(entityNameToValueMap.get(key));
            } else {
                aaConfigValue.setEntityValue("");
                aaConfigValue.setEntityName(GLOBAL.getName());
            }
        });
        return sitePropertyDetailList;
    }

    private static String[] splitStringForFirstOccurrence(String str, String regex) {
        int dollarIndex = str.indexOf(regex);
        if (dollarIndex != -1) {
            String firstPart = str.substring(0, dollarIndex);
            String secondPart = str.substring(dollarIndex + 1);
            return new String[]{firstPart, secondPart};
        } else {
            return new String[]{str};
        }
    }

    private static Map<String, String> getEntityNameToValueMap(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        Map<String, String> entityNameToValueMap = new HashMap<>();
        String prefix = "";

        String[] entityValueSplit = entityValue.split(DOUBLE_DOLLAR_REGEX_SEPARATOR);
        if (entityValueSplit.length > 1) {
            prefix = entityValueSplit[0];
            entityNameToValueMap.put(prefix + DOUBLE_DOLLAR_SEPARATOR + ALL_DEMAND, DOMAIN_ALL_DEMAND.getName());
        }
        if (Util.isSet(entityParentIdsDetail)) {
            entityNameToValueMap.put(prefix + DOUBLE_DOLLAR_SEPARATOR + entityParentIdsDetail.getAdId(), DOMAIN_AD_ID.getName());
            entityNameToValueMap.put(prefix + DOUBLE_DOLLAR_SEPARATOR + entityParentIdsDetail.getCampaignId(), DOMAIN_CAMPAIGN_ID.getName());
            entityNameToValueMap.put(prefix + DOUBLE_DOLLAR_SEPARATOR + entityParentIdsDetail.getAdGroupId(), DOMAIN_ADGROUP_ID.getName());
            entityNameToValueMap.put(prefix + DOUBLE_DOLLAR_SEPARATOR + entityParentIdsDetail.getAdvertiserId(), DOMAIN_ADVERTISER_ID.getName());
            entityNameToValueMap.put(String.valueOf(entityParentIdsDetail.getAdId()), AD_ID.getName());
            entityNameToValueMap.put(String.valueOf(entityParentIdsDetail.getCampaignId()), CAMPAIGN_ID.getName());
            entityNameToValueMap.put(String.valueOf(entityParentIdsDetail.getAdGroupId()), ADGROUP_ID.getName());
            entityNameToValueMap.put(String.valueOf(entityParentIdsDetail.getAdvertiserId()), ADVERTISER_ID.getName());
            entityNameToValueMap.put(String.valueOf(0L), GLOBAL.getName());
        }
        return entityNameToValueMap;
    }

    private static List<Pair<String, String>> getSiteNameToPropertiesListForEntityName(String entityName, String entityValue, EntityParentIdsDetail entityParentIdsDetail) {
        return Arrays.stream(AADemandLevel.values())
                .filter(level -> level.getName().equals(entityName))
                .findFirst()
                .map(level -> Stream.concat(
                        level.getSupplyDemandSiteNamePropertyGetter().apply(entityParentIdsDetail, entityValue).stream(),
                        level.getDemandSiteNamePropertyGetter().apply(entityParentIdsDetail, entityValue).stream())
                        .distinct()
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @NotNull
    private static List<Pair<String, String>> getSiteNamePropertyList(String entityValue, Long id) {
        String siteName = generateSiteName(entityValue, id);
        List<Pair<String, String>> siteNameToPropertyList = new ArrayList<>();
        for (String property : PROPERTIES_LIST) {
            siteNameToPropertyList.add(new Pair<>(siteName, property));
        }
        return siteNameToPropertyList;
    }

    private static String generateSiteName(String entityValue, Long id) {
        if (Util.isSet(entityValue)) {
            String[] entityValueSplit = entityValue.split(DOUBLE_DOLLAR_REGEX_SEPARATOR);
            if (ALL_DEMAND.equals(entityValueSplit[1])) {
                return CONFIG_NAME + DOLLAR_SEPARATOR + entityValueSplit[0] + DOUBLE_DOLLAR_SEPARATOR + ALL_DEMAND;
            }
            if (Util.isSet(id)) {
                if (entityValueSplit.length > 1) {
                    return CONFIG_NAME + DOLLAR_SEPARATOR + entityValueSplit[0] + DOUBLE_DOLLAR_SEPARATOR + id;
                }
            }
        } else {
            if (Util.isSet(id)) {
                return CONFIG_NAME + DOLLAR_SEPARATOR + id;
            }
        }
        return CONFIG_NAME;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForDomainAndAdId(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(entityValue, entityParentIdsDetail.getAdId());
        siteNameToPropertyList.addAll(getSiteNameToPropertiesListForDomainAndAdGroupId(entityParentIdsDetail, entityValue));
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForDomainAndAdGroupId(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(entityValue, entityParentIdsDetail.getAdGroupId());
        siteNameToPropertyList.addAll(getSiteNameToPropertiesListForDomainAndCampaignId(entityParentIdsDetail, entityValue));
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForDomainAndCampaignId(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(entityValue, entityParentIdsDetail.getCampaignId());
        siteNameToPropertyList.addAll(getSiteNameToPropertiesListForDomainAndAdvertiserId(entityParentIdsDetail, entityValue));
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForDomainAndAdvertiserId(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(entityValue, entityParentIdsDetail.getAdvertiserId());
        siteNameToPropertyList.addAll(getSiteNameToPropertiesListForDomainAndAllDemand(entityParentIdsDetail, entityValue));
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForDomainAndAllDemand(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        String allDemandEntityValue = entityValue.split(DOUBLE_DOLLAR_REGEX_SEPARATOR)[0] + DOUBLE_DOLLAR_SEPARATOR + ALL_DEMAND;
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(allDemandEntityValue, null);
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForAdId(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(null, entityParentIdsDetail.getAdId());
        siteNameToPropertyList.addAll(getSiteNameToPropertiesListForAdGroupId(entityParentIdsDetail, null));
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForAdGroupId(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(null, entityParentIdsDetail.getAdGroupId());
        siteNameToPropertyList.addAll(getSiteNameToPropertiesListForCampaignId(entityParentIdsDetail, null));
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForCampaignId(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(null, entityParentIdsDetail.getCampaignId());
        siteNameToPropertyList.addAll(getSiteNameToPropertiesListForAdvertiserId(entityParentIdsDetail, null));
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForAdvertiserId(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(null, entityParentIdsDetail.getAdvertiserId());
        siteNameToPropertyList.addAll(getSiteNameToPropertiesListForGlobal(entityParentIdsDetail, null));
        return siteNameToPropertyList;
    }

    public static List<Pair<String, String>> getSiteNameToPropertiesListForGlobal(EntityParentIdsDetail entityParentIdsDetail, String entityValue) {
        List<Pair<String, String>> siteNameToPropertyList = getSiteNamePropertyList(null, null);
        return siteNameToPropertyList;
    }
}
