package net.media.autotemplate.dal.druid.service;

import net.media.autotemplate.dal.druid.service.metrics.AbstractMetrics;
import net.media.autotemplate.dal.druid.service.metrics.CmMetricService;
import net.media.autotemplate.dal.druid.service.metrics.MaxMetricService;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
    Created by shubham-ar
    on 17/7/18 8:30 PM   
*/

public class AnalyticsMetricsRefresherFactory {
    private static final ScheduledExecutorService cmMetricExecutorService = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService maxMetricExecutorService = Executors.newScheduledThreadPool(1);
    private static final Logger LOG = LogManager.getLogger(AnalyticsMetricsRefresherFactory.class);

    public static void init() {
        LoggingService.log(LOG, Level.info, "AdTag_Customer_Map_Refresher_Initialised");
        cmMetricExecutorService.scheduleAtFixedRate(new CmMetricService(), 0, 5, TimeUnit.MINUTES);
        maxMetricExecutorService.scheduleAtFixedRate(new MaxMetricService(), 0, 5, TimeUnit.MINUTES);
    }

    public static void main(String[] args) {
        AbstractMetrics.runAll();
    }
}
