package net.media.keywordlearning.beans;

/**
 * Created by autoopt/rohit.aga.
 */
public class PirateDebugInput {
    private String siteName;
    private int keywordType;
    private String basis;
    private int urlCount;
    private String canonHash;
    //    private actualData; // what to do with this
    private int learnerId;
    private int bucketId;
    private int adTagId;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getKeywordType() {
        return keywordType;
    }

    public void setKeywordType(int keywordType) {
        this.keywordType = keywordType;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public int getUrlCount() {
        return urlCount;
    }

    public void setUrlCount(int urlCount) {
        this.urlCount = urlCount;
    }

    public String getCanonHash() {
        return canonHash;
    }

    public void setCanonHash(String canonHash) {
        this.canonHash = canonHash;
    }

    public int getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(int learnerId) {
        this.learnerId = learnerId;
    }

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    public int getAdTagId() {
        return adTagId;
    }

    public void setAdTagId(int adTagId) {
        this.adTagId = adTagId;
    }
}
