package net.media.autotemplate.dal.druid.service.analytics;


import net.media.autotemplate.dal.druid.bean.DruidMeta;

import java.util.Map;

/**
 * @author Prateek Agrawal
 */


public interface IAnalytics {
    boolean isCustomerGrpSet();

    String getModelId();

    String[] getMetrics();

    void setMetrics(String[] metrics);

    String getImpressions();

    void setImpressions(String impressions);

    Map<String, DruidMeta> getMetaMap();

    void setMetaMap(Map<String, DruidMeta> metaMap);
}
