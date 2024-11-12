package net.media.autotemplate.routes;


import com.google.gson.Gson;
import net.media.autotemplate.factory.DateFactory;
import net.media.autotemplate.routes.util.EntityRoute;
import net.media.autotemplate.services.EntityService;
import net.media.autotemplate.util.ResponseUtil;
import net.media.autotemplate.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.RouteGroup;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/*
    Created by shubham-ar
    on 6/10/17 3:08 PM   
*/
public class EntityRoutes implements RouteGroup {

    private static final Logger LOG = LogManager.getLogger(EntityRoutes.class);
    private static final Gson GSON = Util.getGson();
    private static final Map<String, String> CACHE_MAP = new HashMap<>();

    @Override
    public void addRoutes() {
        post("/:entityId/template/update", new EntityRoute() {

                    @Override
                    protected String makeResponse(Request request, EntityService entityService) throws Exception {
                        return ResponseUtil.getBaseResponse(true, entityService.updateTemplateStatus(request.body()));
                    }
                }
        );

        post("/v2/:entityId/template/update", new EntityRoute() {
                    @Override
                    protected String makeResponse(Request request, EntityService entityService) throws Exception {
                        return ResponseUtil.getBaseResponse(true, entityService.updateTemplateStatus(request.body()));
                    }
                }
        );

        get("/:entityId/template/add", new EntityRoute() {

            @Override
            protected String makeResponse(Request request, EntityService entityService) throws Exception {
                String template = request.queryParams("template");
                String buSelected = request.queryParams("bu");
                return entityService.addManualTemplates(template, buSelected);
            }

        });

        get("/:entityId", new EntityRoute() {
            @Override
            protected String makeResponse(Request request, EntityService entityService) throws Exception {
                return ResponseUtil.getBaseResponse(true, GSON.toJsonTree(entityService.getEntity()));
            }
        });

        get("/:entityId/state", new EntityRoute() {

            @Override
            protected String makeResponse(Request request, EntityService entityService) throws Exception {
                return ResponseUtil.getBaseResponse(true, entityService.getStateWiseData(DateFactory.makeDateRange(request)));
            }
        });


        get("/:entityId/druid", new EntityRoute() {

            @Override
            protected String makeResponse(Request request, EntityService entityService) throws Exception {
                return ResponseUtil.getBaseResponse(true, entityService.getTemplateData(DateFactory.makeDateRange(request), getBUSelected()));
            }
        });

        get("/:entityId/customization", new EntityRoute() {

            @Override
            protected String makeResponse(Request request, EntityService entityService) throws Exception {
                return ResponseUtil.getResponse(entityService.getCustomization());
            }
        });

        get("/:entityId/customization/add", new EntityRoute() {

            @Override
            protected String makeResponse(Request request, EntityService entityService) throws Exception {
                entityService.setAdAttribution(request.queryParams("ad-attr-link"));
                entityService.setHeaderCustomization(request.queryParams("header-text"));
                return String.format("%s", true);
            }
        });

    }

}
