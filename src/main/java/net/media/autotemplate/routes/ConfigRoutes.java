package net.media.autotemplate.routes;

import com.google.gson.JsonArray;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.bean.FrameworkSize;
import net.media.autotemplate.dal.configs.GlobalConfig;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.C8LearningKeyMasterUtils;
import net.media.autotemplate.dal.db.DbConstants;
import net.media.autotemplate.dal.db.FrameworkSizeService;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.RouteGroup;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.path;

/*
    Created by shubham-ar
    on 24/11/17 7:40 PM   
*/
public class ConfigRoutes implements RouteGroup {

    private static final Logger LOG = LogManager.getLogger(ConfigRoutes.class);

    @Override
    public void addRoutes() {

        get("/size/:adtag", (request, response) -> {
            try {
                String adtag = DruidDAL.getBracedId(request.params(":adtag"));
                int sizeId = C8LearningKeyMasterUtils.getSizeIdForAdtag(adtag);
                LoggingService.log(LOG, Level.info, "adtag size : " + adtag + " -> " + sizeId);
                return FrameworkSizeService.getFrameworkSize(sizeId).getWidthByHeight();
            } catch (Exception e) {
                return DbConstants.DEFAULT_TEMPLATE_SIZE;
            }
        });

        get("/entity", (request, response) -> {
            try {
                String domain = request.queryParams("domain");
                String adTag = request.queryParams("adtag");
                Long entity_id;
                List<Long> entityIds = AutoTemplateDAL.getEntityId(domain, Long.parseLong(adTag));
                if(entityIds.size() > 0) {
                    entity_id = entityIds.get(0);
                }
                else {
                    String size;
                    Integer sizeId = C8LearningKeyMasterUtils.getSizeIdForAdtag(adTag);
                    if(sizeId == null) {
                        throw new Exception(String.format("NO_SIZE_ID_FOR_ADTAG::%s", adTag));
                    }
                    FrameworkSize frameworkSize = FrameworkSizeService.getFrameworkSize(sizeId);
                    if(frameworkSize == null) {
                        throw new Exception(String.format("NO_FRAMEWORK_SIZE_FOR_SIZE_ID::%s_ADTAG::%s", sizeId, adTag));
                    }
                    else {
                        size = frameworkSize.getWidthByHeight();
                        LoggingService.log(LOG, Level.info, "adtag size : " + adTag + " -> " + sizeId);
                        Entity newEntity = new Entity(-1L, adTag, domain, size);
                        entity_id = AutoTemplateDAL.insertEntityUrlMapping(newEntity, AdminFactory.getAdmin(request));
                        if(entity_id == -1L) {
                            throw new Exception("FAILURE_TO_CREATE_ENTITY");
                        }
                    }
                }
                return entity_id;
            }
            catch (Exception e) {
                LoggingService.log(LOG, Level.error, "ERROR_GETTING_ENTITY::" + e.getMessage(), e);
                return "";
            }
        });

        get("/global_config", (request, response) -> {
            String property = request.queryParams("property");
            JsonArray configValue = new JsonArray();
            try {
                configValue = GlobalConfig.getProperty(property);
            } catch (Exception e) {
                LoggingService.log(LOG, Level.error, "Error in fetching config " + property, e);
                response.status(HttpStatus.SC_BAD_REQUEST);
                return "Error in fetching property";
            }
            return configValue.toString();
        });

        path("/generator", new GeneratorConfigRoutes());

    }
}