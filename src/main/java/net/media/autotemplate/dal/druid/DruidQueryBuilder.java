package net.media.autotemplate.dal.druid;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.dal.druid.bean.AnalyticsType;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidMeta;
import net.media.autotemplate.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static net.media.autotemplate.util.Util.getGson;

/*
    Created by shubham-ar
    on 3/10/17 6:30 PM   
*/
public class DruidQueryBuilder {
    private static Gson gson = getGson();

    private String[] metrics;
    private int limit = DruidConstants.MAX_QUERY_LIMIT;
    private int innerLimit = DruidConstants.INNER_LIMIT;
    private final int customerGroupSet;
    private final String customerGroupValue = "000000";
    private final JsonObject filters;
    private JsonObject filterForSearchUrl = new JsonObject();
    private final ArrayList<String> dimensions;
    private String dimension = null;
    private String startDateForContains;
    private String endDateForContains;
    private final String granularity = "All";
    private final int adminId = DruidConstants.ADMIN_ID;
    private String modelId;
    private AnalyticsType analyticsType;

    public DruidQueryBuilder(String[] metricNames, Map<String, DruidMeta> metaMap, String modelId, boolean customerGroupSet) {
        dimensions = new ArrayList<>();
        filters = new JsonObject();
        this.metrics = Arrays.stream(metricNames).map(metric -> metaMap.get(metric).getId()).toArray(String[]::new);
        this.modelId = modelId;
        this.customerGroupSet = customerGroupSet ? 1 : 0;
    }

    public DruidQueryBuilder setMetrics(String[] metrics) {
        this.metrics = metrics;
        return this;
    }

    public DruidQueryBuilder setLimit(int limit) {
        this.limit = Integer.parseInt(String.valueOf(limit));
        return this;
    }

    public DruidQueryBuilder setDateRange(DateRange dateRange) {
        this.startDateForContains = dateRange.getStartDate();
        this.endDateForContains = dateRange.getEndDate();
        return this;
    }

    @Deprecated
    public DruidQueryBuilder addFilter(Dimension filter, String value, DruidConstants.Filter type) {
        String filterValue = DruidConstants.getMetaMap(analyticsType).get(filter.getAnalyticsName()).getId();
        if (!filters.has(filterValue)) {
            filters.add(filterValue, new JsonArray());
        }
        filters.get(filterValue).getAsJsonArray().add("{\"value\":\"" + value + "\",\"type\":\"" + type.getName() + "\"}");
        return this;
    }

    public DruidQueryBuilder addFilterForSearchUrl(Dimension dimension, String filterKey, DruidConstants.Filter type) {
        String filterValue = DruidConstants.getMetaMap(analyticsType).get(dimension.getAnalyticsName()).getId();
        if (!filterForSearchUrl.has(filterValue)) {
            filterForSearchUrl.add(filterValue, new JsonArray());
        }
        for (String filterForDimension : filterKey.split(",")) {
            if (!Util.isStringSet(filterForDimension) || filterForDimension.equals("null"))
                filterForDimension = "";
            filterForSearchUrl.get(filterValue).getAsJsonArray().add("{\"value\":\"" + filterForDimension + "\",\"type\":\"" + type.getName() + "\"}");
        }
        return this;
    }

    public DruidQueryBuilder addDimension(Dimension dimension) {
        dimensions.add(DruidConstants.getMetaMap(analyticsType).get(dimension.getAnalyticsName()).getId());
        return this;
    }

    public DruidQueryBuilder setDimension(Dimension dimension) {
        this.dimension = DruidConstants.getMetaMap(analyticsType).get(dimension.getAnalyticsName()).getId();
        return this;
    }

    public DruidQueryBuilder setAnalyticsType(AnalyticsType analyticsType) {
        this.analyticsType = analyticsType;
        return this;
    }

    @Deprecated
    public String build() {
        return "";
    }

    String buildSearch(String apiKey) {
        JsonObject query = new JsonObject();
        JsonObject queryBody = new JsonObject();
        queryBody.addProperty("isCusGroup", customerGroupSet);
        queryBody.addProperty("cusGroup", customerGroupValue);
        queryBody.addProperty("startDate", startDateForContains);
        queryBody.addProperty("endDate", endDateForContains);
        queryBody.addProperty("limit", limit);
        queryBody.addProperty("innerLimit", innerLimit);
        queryBody.add("metrics", arraytoJson(metrics));
        queryBody.add("dimensions", arraytoJson(new String[]{dimension}));
        queryBody.addProperty("showQuery", 0);
        queryBody.addProperty("isDownload", 0);
        queryBody.addProperty("modelId", modelId);
        queryBody.addProperty("modelName", "All");
        queryBody.addProperty("adminId", adminId);
        queryBody.addProperty("queryId", DruidConstants.getQueryId(DruidConstants.QUERY_SEARCH_ID, apiKey));
        queryBody.addProperty("granularity", granularity);
        queryBody.add("filters", filterForSearchUrl);
        queryBody.addProperty("showGrandTotal", false);
        queryBody.addProperty("showReportTotal", false);
        queryBody.addProperty("approximateTopValues", true);

        query.add("query", queryBody);
        return gson.toJson(query);
    }

    private JsonArray arraytoJson(String[] x) {
        JsonArray js = new JsonArray();
        for (String str : x)
            js.add(str);
        return js;
    }

    public static JsonArray arraytoJson(List<String> x) {
        JsonArray js = new JsonArray();
        for (String str : x)
            js.add(str);
        return js;
    }
}

