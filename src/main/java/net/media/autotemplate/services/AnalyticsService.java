package net.media.autotemplate.services;

import com.google.gson.JsonObject;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.dal.druid.exception.DruidException;
import net.media.autotemplate.dal.druid.service.metrics.AbstractMetrics;
import net.media.autotemplate.factory.DateFactory;
import net.media.autotemplate.factory.ParallelizeFactory;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.autotemplate.util.parallelizer.Paralize;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
    Created by shubham-ar
    on 12/2/18 8:48 PM   
*/
public class AnalyticsService {
    private static final Logger LOG = LogManager.getLogger(AnalyticsService.class);
    private final Entity entity;
    private final DateRange dateRange;

    public AnalyticsService(Entity entity, DateRange dateRange) {
        this.entity = entity;
        this.dateRange = dateRange;
    }

    public JsonObject getDruidTemplates() {
        JsonObject druidResponse = DruidDAL.getDruidResponseSkeleton();
        Paralize<String, JsonObject, JsonObject> parallelQuery = ParallelizeFactory.getAnalyticsParallelQuery(entity,dateRange);
        try {
            druidResponse = parallelQuery.doTask();
        } catch (DruidException e) {
            LoggingService.log(LOG, Level.error, "Druid Error", e);
            druidResponse.add("analytics-error", e.getErrorJson());
        } catch (Exception e) {
            e.printStackTrace();
            druidResponse.get("system-error").getAsJsonArray().add(e.getClass().getName() + " | " + e.getMessage());
        }
        return druidResponse;
    }

    public static void main(String[] args) throws Exception {
        AbstractMetrics.runAll();
        System.out.println(DruidDAL.getEntityWiseState(AutoTemplateDAL.getEntityInfo(53083L),DateFactory.makeDateRange()));
        System.out.println(new AnalyticsService(AutoTemplateDAL.getEntityInfo(53083L), DateFactory.makeDateRange()).getDruidTemplates().toString());
    }
}
