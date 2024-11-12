package net.media.autotemplate.routes;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.util.ResponseUtil;
import net.media.autotemplate.util.Util;
import spark.RouteGroup;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;

public class V2Reporting implements RouteGroup {
    private static final Gson GSON = Util.getGson();

    //todo make all variables and methods camelcase
    @Override
    public void addRoutes() {

        get("/entity", (request, response) -> {
            try {
//                List<Entity> results = ATDbUtil.getDecisionEntities();
                List<Entity> results = new ArrayList<>();
//                return ResponseUtil.getBaseResponse(true, GSON.fromJson(GSON.toJson(results), JsonElement.class));
                return ResponseUtil.getBaseResponse(true, GSON.toJson(results, List.class));
            } catch (Exception e) {
                return ResponseUtil.getErrorResponse(e.getClass().getName(), e.getMessage());
            }
        });

        get("/entity/:id", (request, response) -> {
            String id = request.params(":id");
            try {
//                List<EntityDecision> results = ATDbUtil.getEntityWiseDecisions(id);
                List<Entity> results = new ArrayList<>();
                return ResponseUtil.getBaseResponse(true, GSON.fromJson(GSON.toJson(results), JsonArray.class));
            } catch (Exception e) {
                return ResponseUtil.getErrorResponse(e.getClass().getName(), e.getMessage());
            }
        });
    }
}
