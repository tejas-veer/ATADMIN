package net.media.autotemplate.dal.druid;

import net.media.autotemplate.dal.druid.bean.AnalyticsType;
import net.media.autotemplate.dal.druid.bean.DruidMeta;
import net.media.autotemplate.dal.druid.service.analytics.CMAnalytics;
import net.media.autotemplate.dal.druid.service.analytics.IAnalytics;
import net.media.autotemplate.dal.druid.service.analytics.MaxAnalytics;

import java.text.DecimalFormat;
import java.util.*;

/*
    Created by shubham-ar
    on 3/10/17 6:00 PM   
*/
public class DruidConstants {
    public static final int ADMIN_ID = 17022;//jatin.w@media.net
    public static final String ANALYTICS_API_DRUID_URL = "https://analytics.mn/querydruidapi";
    public static final String ANALYTICS_API_CONTAINS_URL = "https://analytics.mn/rest/model/query";
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_AUTH_VALUE = "Bearer BJ9loqpHSKwmRJuRUYlAzuEQO3qGPl0E";
    public static final int MAX_QUERY_LIMIT = 20;
    public static final int INNER_LIMIT = 0;
    public static final String QUERY_SEARCH_ID = "MNET_SEARCH_API-ATI";
    public static final String GLOBAL_TOP_TEMPLATES = "MNET_GLOBAL_TOP_TEMPLATES_API-ATI";
    public static final int ANALYTICS_API_DRUID_CONNECTION_TIMEOUT_MS = 5 * 60 * 1000;
    public static final String TEMPLATE_TITLE = "Template";
    public static final String[] SEARCH_PARAMS = {"domain", "adtag", "customer", "partner"};
    public static final List<String> MAX_REPORTING_METRICS_LIST = Arrays.asList("Valid Impressions", "Valid Clicks", "Weighted Conversions", "[Learning] Weighted Conversions", "[Ad] Actual CTR", "[Page] Actual CTR", "Actual CVR", "[Learning] Actual CVR", "[Ad] CTR * Actual CVR", "[Ad] CTR * [Learning] Actual CVR", "[Page] CTR * Actual CVR", "[Page] CTR * [Learning] Actual CVR", "Internal Revenue", "[Learning] Internal Revenue", "Media.net Rev", "Media.net Internal Rev", "Media.net CPA", "Media.net Profit", "Media.net CPM", "Media.net RPM", "Media.net Internal RPM", "[Learning] Ad Internal RPM", "[Learning] Page Internal RPM", "Rev Ratio", "Internal Rev Ratio");
    public static final List<String> CM_REPORTING_METRICS_LIST = Arrays.asList("Page Impression", "Keyword Clicks", "Ad Clicks", "Revenue", "RPM", "L2R (%)", "L2A (%)", "RPC");

    private static final Map<AnalyticsType, IAnalytics> ANALYTICS = new HashMap<AnalyticsType, IAnalytics>() {{
        put(AnalyticsType.CM, CMAnalytics.getInstance());
        put(AnalyticsType.MAX, MaxAnalytics.getInstance());
    }};

    private static DecimalFormat decimalFormat = new DecimalFormat("######");


    public static IAnalytics getCmAnalytics() {
        return ANALYTICS.get(AnalyticsType.CM);
    }

    public static IAnalytics getMaxAnalytics() {
        return ANALYTICS.get(AnalyticsType.MAX);
    }

    public static IAnalytics getAnalyticsService(AnalyticsType analyticsType) {
        return ANALYTICS.get(analyticsType);
    }

    public static String getCmImpression() {
        return getCmAnalytics().getImpressions();
    }

    public static String[] getCmMetrics() {
        return getCmAnalytics().getMetrics();
    }

    public static String getCmModelId() {
        return getCmAnalytics().getModelId();
    }

    public static String getMaxModelId() {
        return getMaxAnalytics().getModelId();
    }

    public static String getModelId(AnalyticsType analyticsType) {
        return ANALYTICS.get(analyticsType).getModelId();
    }

    public static Map<String, DruidMeta> getCmMetaMap() {
        return getCmAnalytics().getMetaMap();
    }

    public static Map<String, DruidMeta> getMaxMetaMap() {
        return getMaxAnalytics().getMetaMap();
    }

    public static Map<String, DruidMeta> getMetaMap(AnalyticsType analyticsType) {
        return ANALYTICS.get(analyticsType).getMetaMap();
    }

    public static void main(String args[]) {
        String c = ATState.BASE.getVal();
        System.out.println(c);
    }

    public static String[] getMetrics(AnalyticsType analyticsType) {
        return ANALYTICS.get(analyticsType).getMetrics();
    }

    public static String getQueryId(String init, String apiKey) {
        String random = decimalFormat.format(Math.random() * 1000000);
        String timeStamp = String.valueOf(new Date().getTime());
        if (Objects.nonNull(apiKey))
            init = (apiKey + '_' + init);
        return init + "_" + random + "_" + timeStamp;
    }

    public enum Granularity {
        HOUR("Hour"),
        DAY("Day"),
        DEFAULT("All");

        String val;

        Granularity(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    public enum Filter {
        EQUAL("Equal"),
        NOT_EQUAL("Not Equal"),
        REG_INCLUDE("Regex Inclusion"),
        REG_EXCLUDE("Regex Exclusion"),
        CONTAINS("Contains");

        String name;

        Filter(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private static Map<String, Filter> filterMap = new HashMap<String, Filter>() {{
            for (Filter filter : Filter.values()) {
                this.put(filter.getName(), filter);
            }
        }};

        public static Filter getFilterFromName(String name) {
            return filterMap.get(name);
        }

        public String urlVal() {
            switch (name) {
                case "Equal":
                    return "EQ";
                case "Not Equal":
                    return "NE";
                case "Regex Inclusion":
                    return "RI";
                case "Regex Exclusion":
                    return "RE";
                default:
                    return null;
            }
        }
    }

    public enum ATState {
        ENABLED("AUTO_TEMPLATE_ENABLED"),
        BASE("BASE");

        private String val;

        ATState(String val) {
            this.val = val;
        }

        public static String getDimensionName() {
            return "Auto Template State";
        }

        public String getVal() {
            return val;
        }
    }
}
