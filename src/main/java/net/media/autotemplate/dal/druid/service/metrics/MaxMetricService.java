package net.media.autotemplate.dal.druid.service.metrics;

import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.bean.DruidMeta;

import java.util.Map;

/**
 * @author Prateek Agrawal
 */


public class MaxMetricService extends AbstractMetrics {
    @Override
    public String getProperty() {
        return "MAX_ANALYTICS_METRICS_V2";
    }

    @Override
    public String getEntity() {
        return "GLOBAL_GLOBAL";
    }

    @Override
    String getModelId() {
        return DruidConstants.getMaxModelId();
    }

    @Override
    String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    void setMetaMap(Map<String, DruidMeta> refreshedMetaMap) {
        DruidConstants.getMaxAnalytics().setMetaMap(refreshedMetaMap);
    }

    @Override
    void setMetrics(String[] metrics) {
        DruidConstants.getMaxAnalytics().setMetrics(metrics);
    }

    @Override
    void setImpression(String impression) {
        DruidConstants.getMaxAnalytics().setImpressions(impression);
    }
}
