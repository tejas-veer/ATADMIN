package net.media.autotemplate.dal.druid.service.analytics;


import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.bean.DruidMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Prateek Agrawal
 */


public class CMAnalytics implements IAnalytics {
    private String[] METRICS = {};
    private String IMPRESSIONS;
    private Map<String, DruidMeta> META_MAP;

    private static CMAnalytics cmAnalytics;

    private CMAnalytics() {
    }

    public static CMAnalytics getInstance() {
        if (cmAnalytics == null)
            cmAnalytics = new CMAnalytics();
        return cmAnalytics;
    }

    @Override
    public boolean isCustomerGrpSet() {
        return true;
    }

    @Override
    public String getModelId() {
        return "27";
    }

    @Override
    public String[] getMetrics() {
        return METRICS;
    }

    @Override
    public void setMetrics(String[] metrics) {
        this.METRICS = metrics;
    }

    @Override
    public String getImpressions() {
        return IMPRESSIONS;
    }

    @Override
    public void setImpressions(String impressions) {
        this.IMPRESSIONS = impressions;
    }

    @Override
    public Map<String, DruidMeta> getMetaMap() {
        return META_MAP;
    }

    public List<String> getMetricIdsForReporting() {
        return DruidConstants.CM_REPORTING_METRICS_LIST.stream().map(metricName -> getMetaMap().get(metricName).getId()).collect(Collectors.toList());
    }

    @Override
    public void setMetaMap(Map<String, DruidMeta> metaMap) {
        this.META_MAP = metaMap;
    }
}
