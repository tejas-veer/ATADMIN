package net.media.autotemplate.routes;

import com.google.gson.Gson;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.config.ConfigUpdate;
import net.media.autotemplate.routes.util.ATRoute;
import net.media.autotemplate.services.ConfigsService;
import net.media.autotemplate.util.Util;
import net.media.database.DatabaseException;
import spark.Request;
import spark.Response;
import spark.RouteGroup;

import static spark.Spark.get;
import static spark.Spark.post;

/*
    Created by shubham-ar
    on 14/3/18 6:31 PM   
*/
public class GeneratorConfigRoutes implements RouteGroup {
    public static final Gson GSON = Util.getGson();

    @Override
    public void addRoutes() {

        get("/:level/:entityId", new ATRoute() {
            @Override
            protected ApiResponse getResponse(Request req, Response resp, ConfigsService tcs) throws Exception {
                return new ApiResponse(tcs.getConfigs());
            }
        });

        post("/:level/:entityId", new ATRoute() {
            @Override
            protected ApiResponse getResponse(Request req, Response resp, ConfigsService tcs) throws DatabaseException {
                ConfigUpdate updates = GSON.fromJson(req.body(), ConfigUpdate.class);
                return tcs.writeUpdate(updates);
            }
        });

    }
}
