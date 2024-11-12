package net.media.autotemplate.bean;

/**
 * Created by Jatin Warade
 * on 08-September-2023
 * at 20:45
 */
public class AdAssetDetail {
    private final Long adId;
    private final Long adgroupId;
    private final String callToAction;
    private final String description;
    private final String imageUrl;
    private final String sponsoredBy;
    private final String title;


    public AdAssetDetail(Long adId, Long adgroupId, String callToAction, String description, String imageUrl, String sponsoredBy, String title) {
        this.adId = adId;
        this.adgroupId = adgroupId;
        this.callToAction = callToAction;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sponsoredBy = sponsoredBy;
        this.title = title;
    }

    public Long getAdId() {
        return adId;
    }

    public Long getAdgroupId() {
        return adgroupId;
    }

    public String getCallToAction() {
        return callToAction;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSponsoredBy() {
        return sponsoredBy;
    }

    public String getTitle() {
        return title;
    }
}