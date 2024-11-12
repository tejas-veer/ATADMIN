package net.media.autotemplate.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.*;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.druid.DruidUtil;
import net.media.autotemplate.dal.druid.ReportingDruidQueryBuilder;
import net.media.autotemplate.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: 30/10/23 rename reportingService
public class TopNTemplateService {
    private static Cache<String, JsonArray> topNTemplatesCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();

    private static void cacheResult(String key, JsonArray globalTemplateResponse) throws Exception {
        topNTemplatesCache.put(key, globalTemplateResponse);
    }

    //todo ideal structure DateRange dateRange, List<DruidFilterBean> filterList, List<Dimension> DimensionList, Metric sortConfigMetric, BusinessUnit, Limit, int QueryLevel
    public static JsonArray getResultsFromTopNTemplatesCache(DateRange dateRange, JsonObject filters, String metrics, JsonArray dimensions, BusinessUnit businessUnit, String limit, int queryLevel) throws Exception {
        String startDate = dateRange.getStartDate();
        String endDate = dateRange.getEndDate();
        String keyForTopNTemplatesCache = buildKeyForTopNTemplatesCache(filters, metrics, startDate, endDate, dimensions, businessUnit); // BU not included
        JsonArray resultFromCache = topNTemplatesCache.getIfPresent(keyForTopNTemplatesCache);
        if (!Util.isSet(resultFromCache)) {
            JsonArray responseFromDruid = getResponseFromDruid(startDate, endDate, limit, filters, metrics, businessUnit, dimensions, queryLevel);
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Request Cancelled by user");
            }
            if (Util.isSet(responseFromDruid) && queryLevel == 1) {
                cacheResult(keyForTopNTemplatesCache, responseFromDruid);
            }
            resultFromCache = responseFromDruid;
        }
        return resultFromCache;
    }

    private static JsonArray getResponseFromDruid(String startDate, String endDate, String limit, JsonObject filters, String metrics, BusinessUnit businessUnit, JsonArray dimensions, int queryLevel) throws Exception {
        //todo:- to build query send only filters, dimensions, sortConfigMetric, bu, ....
        String query = ReportingDruidQueryBuilder.buildQueryGlobalTopTemplates(startDate, endDate, limit, filters, metrics, businessUnit, dimensions, queryLevel);
        return DruidUtil.executeQuery(query);
    }

    // TODO: 30/10/23 operate on List of objects ([filters], [dimensions])
    public static String buildKeyForTopNTemplatesCache(JsonObject filters, String metrics, String startDate, String endDate, JsonArray dimensions, BusinessUnit businessUnit) {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        String filtersString = gson.toJson(filters);
        String dimensionsString = gson.toJson(dimensions);

        jsonObject.addProperty("SORT_CONFIG_METRIC", metrics);
        jsonObject.addProperty("START_DATE", startDate);
        jsonObject.addProperty("END_DATE", endDate);
        jsonObject.addProperty("FILTERS", filtersString);
        jsonObject.addProperty("DIMENSIONS", dimensionsString);
        jsonObject.addProperty("BUSINESS_UNIT", businessUnit.getName());

        List<String> sortedKeys = new ArrayList<>(jsonObject.keySet());
        Collections.sort(sortedKeys);

        StringBuilder concatenatedString = new StringBuilder();

        boolean isFirst = true;
        for (String key : sortedKeys) {
            String value = jsonObject.get(key).getAsString();
            if (!isFirst) {
                concatenatedString.append(ConfigConstants.DOLLAR_SEPARATOR);
            }
            concatenatedString.append(key).append(":").append(value);
            isFirst = false;
        }

        return concatenatedString.toString();
    }
}













