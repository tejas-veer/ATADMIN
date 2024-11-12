package net.media.autotemplate.enums;

import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.bean.autoasset.EntityParentIdsDetail;
import net.media.autotemplate.services.AutoAssetConfigService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public enum AADemandLevel {
    DOMAIN_AD_ID("DOMAIN$$AD_ID", AutoAssetConfigService::getSiteNameToPropertiesListForDomainAndAdId, AutoAssetConfigService::getSiteNameToPropertiesListForAdId),
    DOMAIN_ADGROUP_ID("DOMAIN$$ADGROUP_ID", AutoAssetConfigService::getSiteNameToPropertiesListForDomainAndAdGroupId, AutoAssetConfigService::getSiteNameToPropertiesListForAdGroupId),
    DOMAIN_CAMPAIGN_ID("DOMAIN$$CAMPAIGN_ID", AutoAssetConfigService::getSiteNameToPropertiesListForDomainAndCampaignId, AutoAssetConfigService::getSiteNameToPropertiesListForCampaignId),
    DOMAIN_ADVERTISER_ID("DOMAIN$$ADVERTISER_ID", AutoAssetConfigService::getSiteNameToPropertiesListForDomainAndAdvertiserId, AutoAssetConfigService::getSiteNameToPropertiesListForAdvertiserId),
    DOMAIN_ALL_DEMAND("DOMAIN$$ALL_DEMAND", AutoAssetConfigService::getSiteNameToPropertiesListForDomainAndAllDemand, AutoAssetConfigService::getSiteNameToPropertiesListForGlobal),

    AD_ID("AD_ID", (e, s) -> new ArrayList<>(), AutoAssetConfigService::getSiteNameToPropertiesListForAdId),
    ADGROUP_ID("ADGROUP_ID", (e, s) -> new ArrayList<>(), AutoAssetConfigService::getSiteNameToPropertiesListForAdGroupId),
    CAMPAIGN_ID("CAMPAIGN_ID", (e, s) -> new ArrayList<>(), AutoAssetConfigService::getSiteNameToPropertiesListForCampaignId),
    ADVERTISER_ID("ADVERTISER_ID", (e, s) -> new ArrayList<>(), AutoAssetConfigService::getSiteNameToPropertiesListForAdvertiserId),

    GLOBAL("GLOBAL", (e, s) -> new ArrayList<>(), AutoAssetConfigService::getSiteNameToPropertiesListForGlobal);


    private String name;
    private BiFunction<EntityParentIdsDetail, String, List<Pair<String, String>>> supplyDemandSiteNamePropertyGetter;
    private BiFunction<EntityParentIdsDetail, String, List<Pair<String, String>>> demandSiteNamePropertyGetter;

    AADemandLevel(String name, BiFunction<EntityParentIdsDetail, String, List<Pair<String, String>>> supplyDemandSiteNamePropertyGetter) {
        this.name = name;
        this.supplyDemandSiteNamePropertyGetter = supplyDemandSiteNamePropertyGetter;
    }

    AADemandLevel(String name, BiFunction<EntityParentIdsDetail, String, List<Pair<String, String>>> supplyDemandSiteNamePropertyGetter, BiFunction<EntityParentIdsDetail, String, List<Pair<String, String>>> demandSiteNamePropertyGetter) {
        this.name = name;
        this.supplyDemandSiteNamePropertyGetter = supplyDemandSiteNamePropertyGetter;
        this.demandSiteNamePropertyGetter = demandSiteNamePropertyGetter;
    }

    public String getName() {
        return name;
    }

    public BiFunction<EntityParentIdsDetail, String, List<Pair<String, String>>> getSupplyDemandSiteNamePropertyGetter() {
        return supplyDemandSiteNamePropertyGetter;
    }

    public BiFunction<EntityParentIdsDetail, String, List<Pair<String, String>>> getDemandSiteNamePropertyGetter() {
        return demandSiteNamePropertyGetter;
    }
}
