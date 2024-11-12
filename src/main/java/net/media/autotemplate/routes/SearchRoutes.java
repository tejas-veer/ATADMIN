package net.media.autotemplate.routes;

import com.google.gson.*;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.dal.druid.*;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidFilterBean;
import net.media.autotemplate.factory.DateFactory;
import net.media.autotemplate.routes.util.AbstractRoute;
import net.media.autotemplate.services.SearchService;
import net.media.autotemplate.util.Util;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;
import spark.RouteGroup;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

/*
    Created by shubham-ar
    on 1/11/17 12:29 PM   
*/
// TODO: 21/11/23 connectedFilter should also use SearchService Cache functionality, in case of non connected filters pass FilterBeanList as emptyList
// TODO: 06/12/23 @sneha.bs/tejas.v in next upload only return values to be shown in auto-select without any extra metric data
public class SearchRoutes implements RouteGroup {
    public static final Gson GSON = Util.getGson();

    @Override
    public void addRoutes() {
        path("/connectFilters", () -> {
            post("/value", new AbstractRoute() {
                @Override
                public ApiResponse getResponse(Request req, Response resp) throws Exception {
                    List<DruidFilterBean> listOfFilters = getDruidFilterBeans(req);
                    Dimension searchDimension = Dimension.getDimensionFromAnalyticsName(req.queryParams("search_dimension"));
                    listOfFilters.removeIf(filterObj -> filterObj.getDimension().equals(searchDimension));
                    String searchInput = req.queryParams("search_input");
                    BusinessUnit businessUnit = BusinessUnit.getBUFromName(req.queryParams("buSelected"));
                    JsonArray jsonArray = DruidDAL.simpleAutoComplete(DateFactory.makeDateRange(), searchInput, searchDimension,
                            null, listOfFilters, businessUnit.getAnalyticsType());
                    return new ApiResponse(jsonArray);
                }

                @NotNull
                private List<DruidFilterBean> getDruidFilterBeans(Request req) {
                    JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
                    List<DruidFilterBean> listOfFilters = new ArrayList<>();
                    for (String key : jsonObject.keySet()) {
                        Dimension dimension = Dimension.getDimensionFromAnalyticsName(key);
                        JsonArray filtersArray = jsonObject.getAsJsonArray(key);
                        for (JsonElement filterElement : filtersArray) {
                            JsonObject filterObject = filterElement.getAsJsonObject();
                            String state = filterObject.get("state").getAsString();
                            JsonArray valuesArray = filterObject.getAsJsonArray("values");
                            List<String> values = new ArrayList<>();
                            for (JsonElement value : valuesArray) {
                                values.add(value.getAsString());
                            }
                            listOfFilters.add(new DruidFilterBean(dimension, String.join(", ", values), DruidConstants.Filter.getFilterFromName(state)));
                        }
                    }
                    return listOfFilters;
                }
            });
        });

        path("/connectFiltersV2", () -> {
            post("/value", new AbstractRoute() {
                @Override
                public ApiResponse getResponse(Request req, Response resp) throws Exception {
                    String body = req.body();
                    JsonParser jsonParser = new JsonParser();
                    JsonObject asJsonObject = jsonParser.parse(body).getAsJsonObject();

                    List<DruidFilterBean> listOfFilters = getDruidFilterBeans(asJsonObject.getAsJsonObject("filters"));
                    Dimension searchDimension = Dimension.getDimensionFromName(asJsonObject.get("search_dimension").getAsString());
                    String searchInput = asJsonObject.get("search_input").getAsString();
                    BusinessUnit businessUnit = BusinessUnit.getBUFromName(asJsonObject.get("buSelected").getAsString());

                    listOfFilters.add(new DruidFilterBean(searchDimension, searchInput, DruidConstants.Filter.CONTAINS));
                    listOfFilters.removeIf(filterObj -> filterObj.getDimension().equals(searchDimension));
                    JsonArray jsonArray = DruidDAL.simpleAutoComplete(DateFactory.makeDateRange(), searchInput, searchDimension,
                            null, listOfFilters, businessUnit.getAnalyticsType());
                    return new ApiResponse(jsonArray);
                }

                private List<DruidFilterBean> getDruidFilterBeans(JsonObject jsonObject) {
                    List<DruidFilterBean> listOfFilters = new ArrayList<>();
                    for (String key : jsonObject.keySet()) {
                        String value = jsonObject.get(key).getAsString();
                        String[] filterValueList = value.split(",");
                        for (String filterValue : filterValueList) {
                            DruidFilterBean druidFilterBean = new DruidFilterBean(Dimension.getDimensionFromName(key), filterValue, DruidConstants.Filter.EQUAL);
                            listOfFilters.add(druidFilterBean);
                        }
                    }
                    return listOfFilters;
                }
            });
        });

        path("/:dimension", () -> {

            get("/", new AbstractRoute() {
                @Override
                public ApiResponse getResponse(Request req, Response resp) throws Exception {
                    BusinessUnit businessUnit = BusinessUnit.getBUFromName(req.queryParams("buSelected"));
                    SearchService searchService = new SearchService(req);
                    return new ApiResponse(searchService.getResultsFromCache("", businessUnit));
                }
            });

            get("/:value", new AbstractRoute() {
                @Override
                public ApiResponse getResponse(Request req, Response resp) throws Exception {
                    String value = req.params(":value");
                    BusinessUnit businessUnit = BusinessUnit.getBUFromName(req.queryParams("buSelected"));
                    SearchService searchService = new SearchService(req);
                    return new ApiResponse(searchService.getResultsFromCache(value, businessUnit));
                }
            });
        });
    }
}
