package net.media.autotemplate.dal.druid;

import com.google.gson.*;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.ExpandedDimension;
import net.media.autotemplate.dal.druid.bean.AnalyticsType;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidMeta;
import net.media.autotemplate.dal.druid.service.analytics.CMAnalytics;
import net.media.autotemplate.dal.druid.service.analytics.MaxAnalytics;
import net.media.autotemplate.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.media.autotemplate.util.Util.getGson;

public class ReportingDruidQueryBuilder {
    private static Gson gson = getGson();

    public static JsonArray getDimensionArray(BusinessUnit businessUnit, JsonArray dimensions, int queryLevel) throws Exception {
        Set<String> dimensionArrayForSelectedFilters = new HashSet<>();
        for (JsonElement element : dimensions) {
            String frontendName = element.getAsString();
            ExpandedDimension expandedDimension = ExpandedDimension.getExpandedDimension(frontendName);
            if (Util.isSet(expandedDimension)) {
                if (queryLevel <= 1 || expandedDimension == ExpandedDimension.ALL_ASSETS) {
                    Set<String> expandedDimensionIds = getExpandedDimensionIds(businessUnit, expandedDimension);
                    dimensionArrayForSelectedFilters.addAll(expandedDimensionIds);
                }
                Dimension primaryDimension = expandedDimension.getPrimaryDimension();
                if (Util.isSet(primaryDimension)) {
                    String dimensionId = DruidConstants.getMetaMap(businessUnit.getAnalyticsType()).get(primaryDimension.getAnalyticsName()).getId();
                    dimensionArrayForSelectedFilters.add(dimensionId);
                }
            }
        }
        JsonArray jsonArray = new JsonArray();
        for (String dimension : dimensionArrayForSelectedFilters) {
            jsonArray.add(dimension);
        }
        return jsonArray;
    }

    @NotNull
    private static Set<String> getExpandedDimensionIds(BusinessUnit businessUnit, ExpandedDimension expandedDimension) {
        Set<String> expandedDimensionIds = new HashSet<>();
        for (Dimension dimension : expandedDimension.getExpandedDimensionList()) {
            String dimensionId = DruidConstants.getMetaMap(businessUnit.getAnalyticsType()).get(dimension.getAnalyticsName()).getId();
            expandedDimensionIds.add(dimensionId);
        }
        return expandedDimensionIds;
    }

    public static JsonObject getFilterJsonObject(JsonObject filters, BusinessUnit businessUnit) {
        JsonObject filterJsonObject = expandFilterJson(filters, businessUnit);
        return filterJsonObject;
    }


    public static JsonObject expandFilterJson(JsonObject jsonObject, BusinessUnit businessUnit) {
        JsonObject filterForSearchUrl = new JsonObject();

        for (String key : jsonObject.keySet()) {
            JsonArray filterArray = jsonObject.get(key).getAsJsonArray();
            Dimension dimension = Dimension.getDimensionFromAnalyticsName(key);
            String dimensionId = DruidConstants.getMetaMap(businessUnit.getAnalyticsType()).get(dimension.getAnalyticsName()).getId();

            for (JsonElement element : filterArray) {
                JsonObject filterObject = element.getAsJsonObject();
                String stateString = filterObject.get("state").getAsString();
                DruidConstants.Filter state = DruidConstants.Filter.getFilterFromName(stateString);
                JsonArray valuesArray = filterObject.getAsJsonArray("values");
                List<String> values = new ArrayList<>();
                for (JsonElement value : valuesArray) {
                    values.add(value.getAsString());
                }

                for (String filterValue : values) {
                    if (!filterForSearchUrl.has(dimensionId)) {
                        filterForSearchUrl.add(dimensionId, new JsonArray());
                    }
                    if (filterValue.equals("null"))
                        filterValue = "";
                    filterForSearchUrl.get(dimensionId).getAsJsonArray().add("{\"value\":\"" + filterValue + "\",\"type\":\"" + state.getName() + "\"}");
                }
            }
        }
        return filterForSearchUrl;
    }

    public static JsonArray getMetricsArray(BusinessUnit businessUnit, JsonElement selectedMetricId) {
        String selectedMetricIdString = selectedMetricId.toString();
        List<String> metricList = CMAnalytics.getInstance().getMetricIdsForReporting();
        if (BusinessUnit.MAX.equals(businessUnit)) {
            metricList = MaxAnalytics.getInstance().getMetricIdsForReporting();
        }

        metricList.removeIf(metric -> metric.equals(selectedMetricIdString));
        metricList.add(0, selectedMetricIdString);

        return metricList.stream()
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    public static String buildQueryGlobalTopTemplates(String startDate, String endDate, String limit, JsonObject filters, String sortConfigMetric, BusinessUnit businessUnit, JsonArray dimensions, int queryLevel) throws Exception {
        JsonObject query = new JsonObject();
        JsonObject queryBody = new JsonObject();
        JsonObject sortConfigBody = new JsonObject();
        JsonElement selectedMetricId = getSortConfigJson(sortConfigMetric, businessUnit);

        int customerGrpSet = DruidConstants.getAnalyticsService(businessUnit.getAnalyticsType()).isCustomerGrpSet() ? 1 : 0;
        queryBody.addProperty("isCusGroup", customerGrpSet);
        queryBody.addProperty("cusGroup", "000000");
        queryBody.addProperty("startDate", startDate);
        queryBody.addProperty("endDate", endDate);
        queryBody.addProperty("limit", limit);
        queryBody.addProperty("innerLimit", 0);
        queryBody.add("metrics", getMetricsArray(businessUnit, selectedMetricId));
        queryBody.add("dimensions", getDimensionArray(businessUnit, dimensions, queryLevel));
        queryBody.addProperty("showQuery", 0);
        queryBody.addProperty("isDownload", 0);
        queryBody.addProperty("modelId", DruidConstants.getModelId(businessUnit.getAnalyticsType()));
        queryBody.addProperty("adminId", DruidConstants.ADMIN_ID);
        queryBody.addProperty("queryId", DruidConstants.getQueryId(DruidConstants.GLOBAL_TOP_TEMPLATES, null));
        queryBody.addProperty("buName", businessUnit.getName());
        queryBody.addProperty("isNADisabled", "0");
        queryBody.addProperty("isNestedEnabled", 0);
        queryBody.addProperty("granularity", "All");
        queryBody.add("filters", getFilterJsonObject(filters, businessUnit));
        queryBody.addProperty("showGrandTotal", false);
        queryBody.addProperty("showReportTotal", false);
        queryBody.addProperty("connection", "c8 druid");
        queryBody.addProperty("limitBytesScanned", true);
        queryBody.addProperty("timezone", "UTC");
        queryBody.addProperty("approximateTopValues", true);
        queryBody.addProperty("enableCalculatedMetricSort", false);
        query.add("query", queryBody);

        sortConfigBody.add("id", selectedMetricId);
        sortConfigBody.addProperty("type", "metric");
        sortConfigBody.addProperty("order", "desc");
        query.add("sortConfig", sortConfigBody);
        return gson.toJson(query);
    }

    private static JsonElement getSortConfigJson(String sortConfigMetric, BusinessUnit businessUnit) {
        AnalyticsType analyticsType = businessUnit.getAnalyticsType();
        Map<String, DruidMeta> metaMap = DruidConstants.getMetaMap(analyticsType);
        String metricId = metaMap.get(sortConfigMetric).getId();
        return new JsonParser().parse(metricId);
    }
}
