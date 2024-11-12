package net.media.autotemplate.services;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.media.autotemplate.bean.Pair;

/*
    Created by shubham-ar
    on 8/2/18 6:19 PM   
*/
public class MetricsService {
    public enum Metric {
        ANALYTICS_CALL_TIME,
        ANALYTICS_ERRORS,
        GENERATOR_STATUS,
        GENERATOR_TIME
    }

    ;

    private static Multimap<String, Pair<Long, Long>> metricStorage = HashMultimap.create();

    public static void updateMetric(Metric metric, Long value) {
        metricStorage.put(metric.name(), new Pair<>(System.currentTimeMillis(), value));
    }

}
