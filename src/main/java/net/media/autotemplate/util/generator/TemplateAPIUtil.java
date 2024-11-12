package net.media.autotemplate.util.generator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.APITemplate;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
    Created by shubham-ar
    on 19/12/17 3:27 PM   
*/
public class TemplateAPIUtil {
    private static final String KBB_CALL_BASE = "http://c8-kbb-api.srv.media.net:8989/kbb/template_api.php?&calling_source=AUTO_TEMPLATE_ADMIN&nocache=1";
    private static final Gson GSON = Util.getGson();
    private static final Logger LOG = LogManager.getLogger(TemplateAPIUtil.class);

    public static String getKbbURL(Entity entity) {
        String[] url_components = entity.getDomain().split("\\.");
        String tldr = url_components[url_components.length - 1];
        return KBB_CALL_BASE + "&d=" + entity.getDomain() + "&dtld=" + tldr + "&crid=" + entity.getAdtagId() + "&tsize=" + entity.getSize() + "&csid=" + entity.getCustomerId() + "&partnerid=" + entity.getPartnerId() + "&rurl=" + URLEncoder.encode("http://"+entity.getDomain());
    }

    public static String reliableCall(Entity entity, int max_tries) throws IOException {
        String error = null;
        for (int i = 0; i < max_tries; i++) {
            NetworkResponse resp = NetworkUtil.getRequest(getKbbURL(entity));
            error = resp.getBody();
        }
        return error;
    }

    public static Map<String, JsonArray> getAPIData(Entity entity, int avgCalls) throws IOException {

        APITemplate apiTemplate = new APITemplate();
//        JsonObject blackBox = new Gson().fromJson(jsonString,JsonObject.class);
        for (int TT = 0; TT < avgCalls; TT++) {
            String jsonString = reliableCall(entity, 1);
            JsonArray serving = GSON.fromJson(jsonString, JsonObject.class).get("st").getAsJsonArray();
            int pc1 = serving.get(0).getAsJsonObject().get("pc").getAsInt() + 1;
            serving.get(0).getAsJsonObject().addProperty("pc", pc1);
            for (JsonElement element : serving) {
                JsonObject strategy = element.getAsJsonObject();
                String strategyName = strategy.get("sts").getAsString();
                int i = strategyName.indexOf("tstype=") + "tstype=".length();
                int j = strategyName.indexOf("||");
                strategyName = strategyName.substring(i, j);
                double strategyPercentage = strategy.get("pc").getAsDouble() / 100;
                JsonArray buckets = strategy.get("r").getAsJsonArray();
                for (JsonElement object : buckets) {
                    JsonObject bucket = object.getAsJsonObject();
                    if (!bucket.get("tid").isJsonNull()) {
                        int pc = bucket.get("pc").getAsInt();
                        double servingPercentage = (pc * strategyPercentage);
                        bucket.addProperty("pc", servingPercentage);
                        apiTemplate.addTemplateData(strategyName, bucket);
                    }
                }
            }
        }
        return apiTemplate.getTemplatePC();
    }

    public static Map<String, Double> getAggregatedTemplatePercentage(Entity entity, int avgCalls) throws IOException {
        Map<String, JsonArray> response = getAPIData(entity, avgCalls);
        Set<Map.Entry<String, JsonArray>> entries = response.entrySet();
        Map<String, Double> accumulatedResonse = new HashMap<>();
        for (Map.Entry<String, JsonArray> entry : entries) {
            JsonArray pcs = entry.getValue();
            Double pc = 0.0;
            for (JsonElement jsonElement : pcs) {
                pc += jsonElement.getAsJsonObject().get("pc").getAsDouble();
            }

            accumulatedResonse.put(entry.getKey(), pc);
        }
        return accumulatedResonse;
    }
}
