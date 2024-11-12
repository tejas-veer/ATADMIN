package net.media.autotemplate.dal.druid.service.analytics;


import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.bean.DruidMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Prateek Agrawal
 */


public class MaxAnalytics implements IAnalytics {
    private String[] METRICS = {};
    private String IMPRESSIONS;
    private Map<String, DruidMeta> META_MAP;
    private static MaxAnalytics maxAnalytics;

    private MaxAnalytics() {
    }

    public static MaxAnalytics getInstance() {
        if (maxAnalytics == null)
            maxAnalytics = new MaxAnalytics();
        return maxAnalytics;
    }

    @Override
    public boolean isCustomerGrpSet() {
        return false;
    }

    @Override
    public String getModelId() {
        return "1082";
    }

    @Override
    public String[] getMetrics() {
        return METRICS;
    }

    @Override
    public void setMetrics(String[] metrics) {
        METRICS = metrics;
    }

    @Override
    public String getImpressions() {
        return IMPRESSIONS;
    }

    @Override
    public void setImpressions(String impressions) {
        IMPRESSIONS = impressions;
    }

    @Override
    public Map<String, DruidMeta> getMetaMap() {
        return META_MAP;
    }

    public List<String> getMetricIdsForReporting() {
        return DruidConstants.MAX_REPORTING_METRICS_LIST.stream().map(metricName -> getMetaMap().get(metricName).getId()).collect(Collectors.toList());
    }

    @Override
    public void setMetaMap(Map<String, DruidMeta> metaMap) {
        this.META_MAP = metaMap;
    }
}
