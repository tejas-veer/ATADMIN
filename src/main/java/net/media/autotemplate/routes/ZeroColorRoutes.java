package net.media.autotemplate.routes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.routes.util.AbstractRoute;
import net.media.autotemplate.routes.util.ZeroColorRoute;
import net.media.autotemplate.services.ZeroColorGenerationService;
import net.media.autotemplate.util.Util;
import spark.Request;
import spark.Response;
import spark.RouteGroup;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

/*
    Created by shubham-ar
    on 5/4/18 3:13 PM   
*/
public class ZeroColorRoutes implements RouteGroup {

    public static final Gson GSON = Util.getGson();

    @Override
    public void addRoutes() {

        get("/frameworks", new AbstractRoute() {
            @Override
            public ApiResponse getResponse(Request req, Response resp) throws Exception {
                return new ApiResponse(GSON.toJsonTree(ZeroColorGenerationService.getValidFrameworks()));
            }
        });

        path("/:level/:entityId", () -> {

            post("/templates", new ZeroColorRoute() {
                @Override
                public ApiResponse makeResponse(Request req, ZeroColorGenerationService zeroColorGenerationService) throws Exception {
                    JsonObject jsonObject = GSON.fromJson(req.body(), JsonObject.class);
                    List<String> frameworks = new ArrayList<>(), sizes = new ArrayList<>();
                    jsonObject.get("frameworks").getAsJsonArray().forEach(item -> frameworks.add(item.getAsString()));
                    jsonObject.get("sizes").getAsJsonArray().forEach(item -> sizes.add(item.getAsString()));
                    String response = zeroColorGenerationService.getTemplates(frameworks, sizes);
                    return new ApiResponse(response);
                }
            });

            post("/insertTemplates", new ZeroColorRoute() {
                @Override
                public ApiResponse makeResponse(Request req, ZeroColorGenerationService zeroColorGenerationService) throws Exception {
                    return new ApiResponse(zeroColorGenerationService.insertTemplates(req.body()));
                }
            });

        });

    }
}
