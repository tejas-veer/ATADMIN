package net.media.autotemplate.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;

import java.util.concurrent.TimeUnit;

public class SimilarityService {
    private static final String MATCH_MAKER_HOST_URL = "http://matchmaker.g-use1d-auto-kbb-k8s.internal.media.net/similarity?";
    private static final String MATCH_MAKER_VERSION = "v=14";

    private static Cache<String, Double> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(3600, TimeUnit.SECONDS)
            .build();

    public static Double getSimilarityScore(String demandBasis, String Keyword) throws Exception {
        String cacheKey = getCacheKey(demandBasis, Keyword);

        if (!Util.isSet(cache.getIfPresent(cacheKey))) {
            Double score = getScore(demandBasis, Keyword);
            cache.put(cacheKey, score);
        }
        return cache.getIfPresent(cacheKey);
    }

    public static String getCacheKey(String demandBasis, String Keyword) {
        return demandBasis + "#@#" + Keyword;
    }

    public static Double getScore(String demandBasis, String keyword) throws Exception {
        String url = MATCH_MAKER_HOST_URL + MATCH_MAKER_VERSION + String.format("&senB=%s&senA=%s", keyword, demandBasis);
        NetworkResponse networkResponse = NetworkUtil.getRequest(url);
        JsonObject jsonResponse = new Gson().fromJson(networkResponse.getBody(), JsonObject.class);
        Double score = jsonResponse.getAsJsonArray("answers").get(0).getAsJsonObject().get("scr").getAsDouble();
        return score;
    }

}
