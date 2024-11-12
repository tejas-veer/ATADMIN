package net.media.autotemplate.routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.factory.DateFactory;
import net.media.autotemplate.routes.util.AbstractATRoute;
import net.media.autotemplate.routes.util.AbstractRoute;
import net.media.autotemplate.services.RequestManagerService;
import net.media.autotemplate.services.TopNTemplateService;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TopTemplateRoute extends AbstractATRoute {
    private static final Logger LOG = LogManager.getLogger(TopTemplateRoute.class);
    private static final String LIMIT = "50";

    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        JsonObject jsonObject = getJsonPayloadFromBody(req.body());
        String requestId = jsonObject.has("hash") ? jsonObject.get("hash").getAsString() : null;
        Callable<JsonArray> task = () -> getDruidResponse(req);
        Future<JsonArray> future = RequestManagerService.submitRequest(task);
        RequestManagerService.addActiveRequest(requestId, future);
        try {
            JsonArray jsonArray = future.get();
            return new ApiResponse(jsonArray);
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            LoggingService.log(LOG, Level.error, "TopTemplateRoute", e);
            return new ApiResponse(HttpStatus.SC_BAD_REQUEST, ExceptionUtils.getStackTrace(e));
        } finally {
            RequestManagerService.removeActiveRequest(requestId);
        }
    }

    public static JsonArray getDruidResponse(Request req) throws Exception {
        String startDate = req.queryParams("startDate");
        String endDate = req.queryParams("endDate");
        DateRange dateRange = DateFactory.makeDateRange(startDate, endDate);
        if (!Util.isSet(dateRange))
            throw new Exception("Invalid Date Format");
        JsonArray jsonArray = getDruidResponse(req, dateRange);
        return jsonArray;
    }

    public static JsonArray getDruidResponse(Request req, DateRange dateRange) throws Exception {
        Long adminId = AdminFactory.getAdmin(req).getAdminId();
        JsonObject jsonObject = getJsonPayloadFromBody(req.body());
        JsonArray dimensions = jsonObject.getAsJsonArray("dimensions");
        JsonObject filters = jsonObject.getAsJsonObject("filters");
        String metrics = jsonObject.get("metrics").getAsString();
        int queryLevel = Util.isSet(jsonObject.get("queryLevel")) ? jsonObject.get("queryLevel").getAsInt() : 1;
        BusinessUnit businessUnit = Util.isSet(jsonObject.get("bu")) ? BusinessUnit.getBUFromName(jsonObject.get("bu").getAsString()) : BusinessUnit.MAX;
        String hash = jsonObject.has("hash") ? jsonObject.get("hash").getAsString() : null;
        if (Util.isStringSet(hash) && queryLevel == 1)
            AutoTemplateDAL.insertHash(getJsonObjectAsString(jsonObject, req.queryParams("startDate"), req.queryParams("endDate")), hash, adminId);
        return TopNTemplateService.getResultsFromTopNTemplatesCache(dateRange, filters, metrics, dimensions, businessUnit, LIMIT, queryLevel);
    }

    public static JsonObject getJsonPayloadFromBody(String body) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(body).getAsJsonObject();
        return jsonObject;
    }

    private static String getJsonObjectAsString(JsonObject jsonObject, String startDate, String endDate) {
        JsonObject jsonObjectWithDate = new JsonObject();
        jsonObject.entrySet().forEach(entry -> jsonObjectWithDate.add(entry.getKey(), entry.getValue()));
        jsonObjectWithDate.addProperty("start", startDate);
        jsonObjectWithDate.addProperty("end", endDate);
        return jsonObjectWithDate.toString();
    }

}



















