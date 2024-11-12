package net.media.autotemplate.bean;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

class Strategy {
    private Map<String, Bucket> bucketMap;
    private String name;

    public Strategy(String name) {
        this.name = name;
        this.bucketMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void insertBucketData(JsonObject bucket) {

        String bucketId = bucket.get("tbid").getAsString();
        if (!bucketMap.containsKey(bucketId)) {
            bucketMap.put(bucketId, new Bucket(bucketId));
        }
        Bucket bucketer = bucketMap.get(bucketId);
        bucketer.addEntry(bucket);
    }

    public Map<String, Bucket> getBucketMap() {

        return bucketMap;
    }
}
