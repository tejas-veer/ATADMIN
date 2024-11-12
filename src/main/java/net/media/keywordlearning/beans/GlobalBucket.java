package net.media.keywordlearning.beans;

/**
 * Created by autoopt/rohit.aga.
 */
public class GlobalBucket {
    private final int bucketId;
    private final String bucketName;

    public GlobalBucket(int bucketId, String bucketName) {
        this.bucketId = bucketId;
        this.bucketName = bucketName;
    }

    public int getBucketId() {
        return bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }
}
