package net.media.autotemplate.bean;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/*
    Created by shubham-ar
    on 19/12/17 4:09 PM   
*/
public class APITemplate {
    private Map<String, Strategy> strategyMap;

    public APITemplate() {
        this.strategyMap = new HashMap<>();
    }

    public void addTemplateData(String strategy, JsonObject bucket) {
        if (!strategyMap.containsKey(strategy)) {
            strategyMap.put(strategy, new Strategy(strategy));
        }
        strategyMap.get(strategy).insertBucketData(bucket);
    }

    public Map<String, JsonArray> getTemplatePC() {
        Map<String, JsonArray> jsonObjectMap = new HashMap<>();
        for (Map.Entry<String, Strategy> stategy : strategyMap.entrySet()) {
            Strategy strategy = stategy.getValue();
            String identifier = strategy.getName();
            Map<String, Bucket> bucketMap = strategy.getBucketMap();
            for (Map.Entry<String, Bucket> bucketEntry : bucketMap.entrySet()) {
                Bucket bucket = bucketEntry.getValue();
                HashMap<String, Pair<Integer, Double>> templateMap = bucket.getTemplateMap();
                for (Map.Entry<String, Pair<Integer, Double>> templateEntry : templateMap.entrySet()) {
                    if (!jsonObjectMap.containsKey(templateEntry.getKey())) {
                        jsonObjectMap.put(templateEntry.getKey(), new JsonArray());
                    }
                    JsonArray pc = jsonObjectMap.get(templateEntry.getKey());
                    JsonObject pco = new JsonObject();
                    Pair<Integer, Double> pp = templateEntry.getValue();
                    pco.addProperty("name", strategy.getName() + '[' + bucket.getBucketId() + ']');
                    pco.addProperty("pc", pp.first != 0 ? pp.second / pp.first : 0);
                    pc.add(pco);
                }
            }
        }
        return jsonObjectMap;
    }
}

