package net.media.autotemplate.dal.druid;

/*
    Created by shubham-ar
    on 3/10/17 5:40 PM   
*/

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.dal.druid.bean.AnalyticsType;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidFilterBean;
import net.media.autotemplate.dal.druid.bean.DruidMeta;
import net.media.autotemplate.factory.DateFactory;

import java.util.List;
import java.util.Map;

import static net.media.autotemplate.dal.druid.DruidConstants.*;

public class DruidDAL {
    public static JsonArray simpleAutoComplete(DateRange dateRange, String key, Dimension dimension, String apiKey, List<DruidFilterBean> filterBeanList, AnalyticsType analyticsType) throws Exception {
        if (!key.isEmpty() && key.length() < 1) {
            return new JsonArray();
        }
        final String[] metrics = DruidConstants.getMetrics(analyticsType);
        final Map<String, DruidMeta> metaMap = DruidConstants.getMetaMap(analyticsType);
        final String modelId = DruidConstants.getModelId(analyticsType);

        boolean isCustomerGrpSet = DruidConstants.getAnalyticsService(analyticsType).isCustomerGrpSet();
        DruidQueryBuilder db = new DruidQueryBuilder(metrics, metaMap, modelId, isCustomerGrpSet)
                .setDateRange(dateRange)
                .setAnalyticsType(analyticsType)
                .setDimension(dimension);

        if (!key.equals(""))
            db.addFilterForSearchUrl(dimension, key, Filter.CONTAINS);

        for (DruidFilterBean filterBean : filterBeanList){
            db.addFilterForSearchUrl(filterBean.getDimension(), filterBean.getValue(), filterBean.getOperation());
        }
        JsonArray druidResponse = DruidUtil.executeQuery(db.buildSearch(apiKey));
        return processFields(druidResponse, dimension);
    }

    public static JsonObject getEntityWiseUrls(Entity entity) throws Exception {
        boolean isCustomerGrpSet = DruidConstants.getAnalyticsService(AnalyticsType.CM).isCustomerGrpSet();
        DruidQueryBuilder dq = new DruidQueryBuilder(getCmMetrics(), getCmMetaMap(), getCmModelId(), isCustomerGrpSet)
                .setLimit(4)
                .setDateRange(DateFactory.makeDateRange())
                .addDimension(Dimension.URL)
                .setAnalyticsType(AnalyticsType.CM)
                .addFilter(Dimension.DOMAIN, entity.getDomain(), DruidConstants.Filter.EQUAL)
                .addFilter(Dimension.ADTAG, entity.getAdtagId(), DruidConstants.Filter.EQUAL)
                .setMetrics(new String[]{DruidConstants.getCmImpression()});
        return DruidUtil.submitQuery(dq.build());
    }

    public static JsonObject getAllTemplates(Entity entity, DateRange dateRange, DruidFilterBean... filters) throws Exception {
        boolean isCustomerGrpSet = DruidConstants.getAnalyticsService(AnalyticsType.CM).isCustomerGrpSet();
        DruidQueryBuilder dq = new DruidQueryBuilder(getCmMetrics(), getCmMetaMap(), getCmModelId(), isCustomerGrpSet)
                .setDateRange(dateRange)
                .setAnalyticsType(AnalyticsType.CM)
                .addDimension(Dimension.TEMPLATE)
                .addFilter(Dimension.DOMAIN, entity.getDomain(), DruidConstants.Filter.EQUAL)
                .addFilter(Dimension.ADTAG, entity.getAdtagId(), DruidConstants.Filter.EQUAL);

        DruidUrlBuilder druidUrlBuilder = new DruidUrlBuilder()
                .addMetric(getCmMetrics())
                .setModelId(getCmModelId())
                .setDateRange(dateRange)
                .addFilter(Dimension.DOMAIN, entity.getDomain(), DruidConstants.Filter.EQUAL)
                .addFilter(Dimension.ADTAG, entity.getAdtagId(), DruidConstants.Filter.EQUAL);

        for (DruidFilterBean filter : filters) {
            dq.addFilter(filter.getDimension(), filter.getValue(), filter.getOperation());
            druidUrlBuilder.addFilter(filter.getDimension(), filter.getValue(), filter.getOperation());
        }

        JsonObject druidResponse = DruidUtil.submitQuery(dq.build());
        druidResponse.addProperty("reportUrl", druidUrlBuilder.build());
        return druidResponse;
    }

    public static String getTemplateDataUrl(Entity entity, DateRange dateRange, DruidFilterBean... filters) {
        DruidUrlBuilder druidUrlBuilder = new DruidUrlBuilder()
                .addMetric(getCmMetrics())
                .setModelId(getCmModelId())
                .setDateRange(dateRange)
                .addFilter(Dimension.DOMAIN, entity.getDomain(), DruidConstants.Filter.EQUAL)
                .addFilter(Dimension.ADTAG, entity.getAdtagId(), DruidConstants.Filter.EQUAL);

        for (DruidFilterBean filter : filters) {
            druidUrlBuilder.addFilter(filter.getDimension(), filter.getValue(), filter.getOperation());
        }
        return druidUrlBuilder.build();
    }

    public static JsonObject getEntityWiseState(Entity entity, DateRange dateRange, DruidFilterBean... filters) throws Exception {
        boolean isCustomerGrpSet = DruidConstants.getAnalyticsService(AnalyticsType.CM).isCustomerGrpSet();
        DruidQueryBuilder dq = new DruidQueryBuilder(getCmMetrics(), getCmMetaMap(), getCmModelId(), isCustomerGrpSet)
                .setAnalyticsType(AnalyticsType.CM)
                .setDateRange(dateRange)
                .addDimension(Dimension.AT_STATE)
                .addFilter(Dimension.DOMAIN, entity.getDomain(), DruidConstants.Filter.EQUAL)
                .addFilter(Dimension.ADTAG, entity.getAdtagId(), DruidConstants.Filter.EQUAL);
        DruidUrlBuilder druidUrlBuilder = new DruidUrlBuilder()
                .setDateRange(dateRange)
                .addMetric(getCmMetrics())
                .setModelId(getCmModelId())
                .addDimension(Dimension.AT_STATE)
                .addFilter(Dimension.DOMAIN, entity.getDomain(), DruidConstants.Filter.EQUAL)
                .addFilter(Dimension.ADTAG, entity.getAdtagId(), DruidConstants.Filter.EQUAL);

        for (DruidFilterBean filter : filters) {
            dq.addFilter(filter.getDimension(), filter.getValue(), filter.getOperation());
            druidUrlBuilder.addFilter(filter.getDimension(), filter.getValue(), filter.getOperation());
        }

        JsonObject druidResponse = DruidUtil.submitQuery(dq.build());
        druidResponse.addProperty("reportUrl", druidUrlBuilder.build());
        return druidResponse;
    }

    public static JsonObject getDruidResponseSkeleton() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("data", new JsonArray());
        jsonObject.add("meta", new JsonArray());
        return jsonObject;
    }


    public static String getBracedId(String templateName) {
        try {
            int i = templateName.indexOf('[') + 1;
            int j = templateName.indexOf(']');
            return templateName.substring(i, j);
        } catch (Exception e) {
            return templateName;
        }
    }

    private static JsonArray processFields(JsonArray druidResponse, Dimension dimension) {
        String dimensionName = dimension.getAnalyticsName();

        JsonArray processedResponse = new JsonArray();
        for (JsonElement jsEle : druidResponse) {
            JsonObject jsonObject = jsEle.getAsJsonObject();
            if (!jsonObject.has(dimensionName))
                continue;
            else {
                String prop = jsonObject.remove(dimensionName).getAsString();
                jsonObject.addProperty(dimension.getFrontendName(), prop);
            }
            processedResponse.add(jsonObject);
        }
        return processedResponse;
    }
}
