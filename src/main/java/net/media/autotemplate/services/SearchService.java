package net.media.autotemplate.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.dal.druid.DruidUtil;
import net.media.autotemplate.dal.druid.bean.AnalyticsType;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidFilterBean;
import net.media.autotemplate.factory.DateFactory;
import net.media.autotemplate.util.Util;
import org.jetbrains.annotations.NotNull;
import spark.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
    Created by shubham-ar
    on 18/12/17 3:01 PM   
*/
public class SearchService {

    private static Cache<String, JsonArray> cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(3600, TimeUnit.SECONDS)
            .build();

    private Dimension dimension;
    private List<DruidFilterBean> filters;
    private DateRange dateRange;
    private String apiKey;

    public SearchService(Request request) {
        dimension = Dimension.getDimensionFromName(request.params(":dimension"));
        filters = DruidUtil.makeFilterList(request, dimension); // TODO: 21/11/23 this is outdated, need to modify considering reporting and mapping blocking tabs
        dateRange = DateFactory.makeDateRange();
        apiKey = request.queryParams("api-key");
    }

    public JsonArray getResultsFromCache(String key, BusinessUnit businessUnit) throws Exception {
        String keyForCache = dimension.getAnalyticsName() + ConfigConstants.DOLLAR_SEPARATOR + key + businessUnit.getName();
        JsonArray resultFromCache = cache.getIfPresent(keyForCache);
        if (!Util.isSet(resultFromCache)) {
            resultFromCache = getResults(key, businessUnit);
            if (Util.isSet(resultFromCache))
                cacheResult(keyForCache, resultFromCache);
        }
        return resultFromCache;
    }

    @NotNull
    private JsonArray getResults(String key, BusinessUnit businessUnit) throws Exception {
        AnalyticsType analyticsType = businessUnit.getAnalyticsType();
        if (Util.isStringEmpty(key) && analyticsType.equals(AnalyticsType.CM))
            filters.add(new DruidFilterBean(Dimension.AT_STATE, DruidConstants.ATState.ENABLED.getVal()));
        return DruidDAL.simpleAutoComplete(dateRange, key, dimension, apiKey, new ArrayList<>(), analyticsType);
    }

    private void cacheResult(String key, JsonArray jsonArray) {
        cache.put(key, jsonArray);
    }
}
