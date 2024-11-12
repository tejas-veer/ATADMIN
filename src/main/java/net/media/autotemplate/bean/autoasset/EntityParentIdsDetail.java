package net.media.autotemplate.bean.autoasset;

import com.google.gson.annotations.SerializedName;

public class EntityParentIdsDetail {
    @SerializedName("AD_ID")
    private Long adId;
    @SerializedName("ADGROUP_ID")
    private Long adGroupId;
    @SerializedName("CAMPAIGN_ID")
    private  Long campaignId;
    @SerializedName("ADVERTISER_ID")
    private  Long advertiserId;

    public EntityParentIdsDetail(Long adId, Long adGroupId, Long campaignId, Long advertiserId) {
        this.adId = adId;
        this.adGroupId = adGroupId;
        this.campaignId = campaignId;
        this.advertiserId = advertiserId;
    }

    public EntityParentIdsDetail() {
    }

    public Long getAdId() {
        return adId;
    }

    public Long getAdGroupId() {
        return adGroupId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public void setAdGroupId(Long adGroupId) {
        this.adGroupId = adGroupId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }
}
