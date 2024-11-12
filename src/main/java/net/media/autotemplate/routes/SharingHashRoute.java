package net.media.autotemplate.routes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.routes.util.AbstractRoute;
import spark.Request;
import spark.Response;

public class SharingHashRoute extends AbstractRoute {
    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        JsonObject jsonObject = getJsonPayloadFromBody(req.body());
        String hash = jsonObject.get("hash").getAsString();
        ApiResponse apiResponse = new ApiResponse(AutoTemplateDAL.getReportingPayloadFromHash(hash));
        return apiResponse;
    }

    public static JsonObject getJsonPayloadFromBody(String body) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(body).getAsJsonObject();
        return jsonObject;
    }
}
