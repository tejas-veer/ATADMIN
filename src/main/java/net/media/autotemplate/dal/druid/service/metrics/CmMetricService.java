package net.media.autotemplate.dal.druid.service.metrics;

import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.bean.DruidMeta;

import java.util.Map;

/**
 * @author Prateek Agrawal
 */


public class CmMetricService extends AbstractMetrics {
    @Override
    public String getProperty() {
        return "ANALYTICS_METRICS";
    }

    @Override
    public String getEntity() {
        return "GLOBAL_GLOBAL";
    }

    @Override
    String getModelId() {
        return DruidConstants.getCmModelId();
    }

    @Override
    String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    void setMetaMap(Map<String, DruidMeta> refreshedMetaMap) {
        DruidConstants.getCmAnalytics().setMetaMap(refreshedMetaMap);
    }

    @Override
    void setMetrics(String[] metrics) {
        DruidConstants.getCmAnalytics().setMetrics(metrics);
    }

    @Override
    void setImpression(String impression) {
        DruidConstants.getCmAnalytics().setImpressions(impression);
    }
}
