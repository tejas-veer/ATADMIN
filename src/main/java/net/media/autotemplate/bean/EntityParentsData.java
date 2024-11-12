package net.media.autotemplate.bean;

import java.util.List;

public class EntityParentsData {
    private final String entity;
    private final String hierarchy;
    private final String campaignKey;
    private final List<String> advertiserKeys;

    public EntityParentsData(String entity, String hierarchy, String campaignKey, List<String> advertiserKeys) {
        this.entity = entity;
        this.hierarchy = hierarchy;
        this.campaignKey = campaignKey;
        this.advertiserKeys = advertiserKeys;
    }

    public String getEntity() {
        return entity;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public String getCampaignKey() {
        return campaignKey;
    }

    public List<String> getAdvertiserKeys() {
        return advertiserKeys;
    }

    public String getAdvertiserKey(){
        return getAdvertiserKeys().get(0);
    }
}
