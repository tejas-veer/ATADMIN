package net.media.autotemplate.bean;

import com.google.gson.JsonObject;

import java.util.HashMap;

class Bucket {

    private String bucketId;
    private HashMap<String, Pair<Integer, Double>> templateMap;

    public Bucket(String bucketId) {
        this.bucketId = bucketId;
        templateMap = new HashMap<>();
    }

    public String getBucketId() {
        return bucketId;
    }

    public HashMap<String, Pair<Integer, Double>> getTemplateMap() {
        return templateMap;
    }

    public void addEntry(JsonObject bucket) {
        String templateId = bucket.get("tid").getAsString();
        if (!templateMap.containsKey(templateId)) {
            templateMap.put(templateId, new Pair<>(0, 0.0));
        }
        Pair<Integer, Double> oldPair = templateMap.get(templateId), newPair;
        newPair = new Pair<>(oldPair.first + 1, oldPair.second + bucket.get("pc").getAsDouble());
        templateMap.put(templateId, newPair);
    }

}
