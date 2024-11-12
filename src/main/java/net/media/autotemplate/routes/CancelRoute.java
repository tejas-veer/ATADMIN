package net.media.autotemplate.routes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.routes.util.AbstractATRoute;
import net.media.autotemplate.services.RequestManagerService;
import spark.Request;
import spark.Response;

public class CancelRoute extends AbstractATRoute {
    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        JsonObject jsonObject = getJsonPayloadFromBody(req.body());
        String requestId = jsonObject.has("hash") ? jsonObject.get("hash").getAsString() : null;
        RequestManagerService.cancelRequest(requestId);
        return new ApiResponse("Call stopped by user");
    }

    public static JsonObject getJsonPayloadFromBody(String body) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(body).getAsJsonObject();
        return jsonObject;
    }
}
