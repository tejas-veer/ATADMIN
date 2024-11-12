package net.media.autotemplate.routes;

import com.google.gson.*;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.factory.DateFactory;
import net.media.autotemplate.routes.util.AbstractATRoute;
import spark.Request;
import spark.Response;
import spark.RouteGroup;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

// TODO: 01/11/23 rename ReportingRoute
public class AnalyticsQueryRoutes implements RouteGroup {
    @Override
    public void addRoutes() {
        path("/query", () -> {
            post("/topTemplatesForSelectedFilters", new TopTemplateRoute());
            post("/cancelQuery", new CancelRoute());

            post("/hash", new SharingHashRoute());

            post("/topDemandBasis", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request req, Response resp) throws Exception {
                    DateRange dateRange = DateFactory.makeDateRange();
                    JsonArray druidResponse = TopTemplateRoute.getDruidResponse(req, dateRange);

                    List<String> entityList = new ArrayList<>();
                    for (JsonElement element : druidResponse) {
                        String demandBasis = element.getAsJsonObject().get("Demand basis").getAsString();
                        String[] parts = demandBasis.split(" - \\[");
                        if (parts.length > 0) {
                            entityList.add(parts[0]);
                        } else {
                            entityList.add(demandBasis);
                        }
                    }
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(entityList);
                    JsonElement jsonElement = gson.fromJson(jsonString, JsonElement.class);
                    return new ApiResponse(jsonElement);
                }
            });
        });
    }
}
