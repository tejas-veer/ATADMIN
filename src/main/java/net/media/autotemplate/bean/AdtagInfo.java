package net.media.autotemplate.bean;

import net.media.autotemplate.util.Util;

/**
 * Created by sumeet
 * on 10/11/17.
 */
public class AdtagInfo {

    private Long entityId;
    private Long adtagId;
    private String domain;
    private String customerId;
    private String partnerId;
    private Long adId;
    private Long campaignId;
    private Long adgroupId;
    private Long advertiserId;
    private String portfolioId;
    private String adDomain;

    public AdtagInfo(Long entityId, Long adtagId, String customerId, String partnerId, String domain) {
        this.entityId = entityId;
        this.adtagId = adtagId;
        this.customerId = customerId;
        this.partnerId = partnerId;
        this.domain = domain;
    }

    public AdtagInfo(Long entityId, Long adtagId, String customerId, String partnerId) {
        this(entityId, adtagId, customerId, partnerId, null);
    }

    public AdtagInfo() {

    }

    public Long getCampaignId() {
        return campaignId;
    }

    public AdtagInfo setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
        return this;
    }

    public Long getAdgroupId() {
        return adgroupId;
    }

    public AdtagInfo setAdgroupId(Long adgroupId) {
        this.adgroupId = adgroupId;
        return this;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public AdtagInfo setAdvertiserId(Long accountId) {
        this.advertiserId = accountId;
        return this;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public AdtagInfo setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
        return this;
    }

    public String getAdDomain() {
        return adDomain;
    }

    public AdtagInfo setAdDomain(String adDomain) {
        this.adDomain = adDomain;
        return this;
    }

    public Long getAdId() {
        return adId;
    }

    public AdtagInfo setAdId(Long adId) {
        this.adId = adId;
        return this;
    }

    public Long entityId() {
        return entityId;
    }

    public Long getAdtagId() {
        return adtagId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return Util.getGson().toJson(this);
    }
}
