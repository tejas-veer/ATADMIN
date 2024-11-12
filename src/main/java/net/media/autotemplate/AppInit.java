package net.media.autotemplate;

import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.druid.service.AnalyticsMetricsRefresherFactory;
import net.media.autotemplate.routes.*;
import net.media.autotemplate.services.AclService;
import net.media.autotemplate.services.CustomerGrpMapRefresher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.servlet.SparkApplication;

import static spark.Spark.*;

/*
    Created by shubham-ar
    on 9/10/17 2:05 PM   
*/
public class AppInit implements SparkApplication {
    //todo add readme where we will provide all api endpoints
    private static final Logger LOG = LogManager.getLogger(AppInit.class);

    @Override
    public void init() {
        if (!ConfigConstants.LOCAL) {
            CustomerGrpMapRefresher.initRefresher();
            AclService.initRefreshers();
        }
        AnalyticsMetricsRefresherFactory.init();

        before((request, response) -> {
            response.header("Content-Type", "application/json");
            response.header("Access-Control-Allow-Headers", "Access-Control-Allow-Methods, Content-Type");
            response.header("Access-Control-Allow-Methods", "POST, GET");
            response.header("Access-Control-Allow-Origin", "*");
        });

        path("/api", () -> {
            path("/generator", new GeneratorRoutes());
            path("/entity", new EntityRoutes());
            path("/search", new SearchRoutes());
            path("/session", new SessionRoutes());
            path("/fakeadmin", new FakeAdminRoutes());
            path("/config", new ConfigRoutes());
            path("/atmapping", new ATRoutes());
            post("/issue", new IssueReportRoute());
            path("/debug",new DebugRoutes());
            path("/druid", new AnalyticsQueryRoutes());
            path("/asset", new AutoAssetRoutes());
        });
    }
}
