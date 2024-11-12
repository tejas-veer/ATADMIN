package net.media.autotemplate.routes;


import net.media.autotemplate.routes.util.DebugRoute;
import net.media.autotemplate.services.DebugService;
import net.media.autotemplate.util.ResponseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.RouteGroup;

import static spark.Spark.get;

public class DebugRoutes implements RouteGroup {
    private static final Logger LOG = LogManager.getLogger(EntityRoutes.class);

    @Override
    public void addRoutes() {
        get("", new DebugRoute() {
            @Override
            protected String makeResponse(Request request, DebugService debugService) throws Exception {
                String url = request.queryParams("durl");
                return ResponseUtil.getBaseResponse(true, debugService.getTemplateDebugData(url));
            }
        });
    }
}
