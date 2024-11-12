package net.media.autotemplate.dal.druid;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidMeta;
import net.media.autotemplate.util.Util;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
    Created by shubham-ar
    on 29/11/17 4:30 PM   
*/
public class DruidUrlBuilder {
    private static final String BASE_URL = "https://analytics.mn/rest/model";
    public static final Gson GSON = Util.getGson();
    private JsonArray dimensions;
    private JsonArray metrics;
    private Map<Dimension, JsonObject> filters;
    private JsonObject customerGroup = GSON.fromJson("{\"name\":\"All\",\"id\":\"000000\"}", JsonObject.class);
    private JsonObject CategoryModel = GSON.fromJson("{\"name\":\"All\",\"id\":\"27\",\"isDefault\":false,\"isFav\":false,\"enableCustomerGroup\":\"1\",\"isNADisabled\":\"0\"}", JsonObject.class);
    private JsonObject topBottom = GSON.fromJson("{\"name\":\"Top\",\"id\":1}", JsonObject.class);
    private String dateState = "Static";
    private String modelId;
    private DateRange dateRange;
    private int limit = 50;

    public DruidUrlBuilder() {
        filters = new HashMap<>();
        dimensions = new JsonArray();
        metrics = new JsonArray();
    }

    public DruidUrlBuilder setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
        return this;
    }

    public DruidUrlBuilder addDimension(Dimension... dimensions) {
        for (Dimension dimension : dimensions)
            this.dimensions.add(getBaseDimension(dimension.getAnalyticsName()));
        return this;
    }

    public DruidUrlBuilder addMetric(String metric) {
        metrics.add(getMetricMeta(metric));
        return this;
    }

    public DruidUrlBuilder addMetric(String[] metrics) {
        for (String metric : metrics) {
            this.addMetric(metric);
        }
        return this;
    }

    public DruidUrlBuilder addFilter(Dimension dimension, String key, DruidConstants.Filter filter) {
        if (!filters.containsKey(dimension)) {
            filters.put(dimension, getBaseFilter(dimension));
        }

        JsonArray filterList = filters.get(dimension).getAsJsonArray("list");
        JsonObject filterJson = new JsonObject();
        filterJson.addProperty("name", key);
        filterJson.addProperty("operator", filter.urlVal());
        filterList.add(filterJson);
        return this;

    }

    public DruidUrlBuilder setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public String build() {
        JsonObject compiledJson = new JsonObject();
        compiledJson.add("dimensions", dimensions);
        compiledJson.add("metrics", metrics);
        compiledJson.add("customerGroup", customerGroup);
        compiledJson.add("filters", bakedFilters());
        compiledJson.add("CategoryModel", CategoryModel);
        compiledJson.add("topBottom", topBottom);
        compiledJson.addProperty("startDate", dateRange.getStartDate() + "T00:00:00Z");
        compiledJson.addProperty("endDate", dateRange.getEndDate() + "T23:59:59Z");
        compiledJson.addProperty("dateState", dateState);
        compiledJson.addProperty("modelId", modelId);
        compiledJson.addProperty("topBottomFiltervalue", limit);
        return BASE_URL + new String(Base64.encodeBase64(GSON.toJson(compiledJson).getBytes()));
    }

    private JsonArray bakedFilters() {
        Iterator<Map.Entry<Dimension, JsonObject>> iterator = filters.entrySet().iterator();
        JsonArray filtersJson = new JsonArray();
        while (iterator.hasNext()) {
            Map.Entry<Dimension, JsonObject> filter = iterator.next();
            filtersJson.add(filter.getValue());
        }
        return filtersJson;
    }

    private static JsonObject getMetricMeta(String metric) {

        DruidMeta metaData = DruidConstants.getCmMetaMap().get(metric);
        JsonObject baseMetric = GSON.toJsonTree(metaData).getAsJsonObject();
        return baseMetric;
    }

    private static JsonObject getBaseDimension(String dimension) {
        return getMetricMeta(dimension);
    }

    private static JsonObject getBaseFilter(Dimension dimension) {
        JsonObject baseFilter = getBaseDimension(dimension.getAnalyticsName());
        baseFilter.add("list", new JsonArray());
        baseFilter.addProperty("openFullView", false);
        return baseFilter;
    }

    public DruidUrlBuilder setModelId(String modelId) {
        this.modelId = modelId;
        return this;
    }
}
