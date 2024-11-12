package net.media.autotemplate.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.bean.InboundParams;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.dal.druid.DruidQueryBuilder;
import net.media.autotemplate.util.generator.RequestValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.util.ArrayList;
import java.util.List;

/*
    Created by shubham-ar
    on 14/11/17 4:37 PM   
*/
public class GeneratorUtil {
    private static final String OLD_GENERATOR_QUEUETASK_URL = "http://at.internal.media.net/TemplateGenerator/queueTask?";
    public static final String GENERATOR_ASYNC_TASKCREATION_URL = ConfigConstants.GENERATOR_BASE_URL + "/asyncTemplateCreation/createTask";
    private static final String OLD_GENERATOR_INSERT_ENDPOINT = "http://at.internal.media.net/TemplateGenerator/insertTemplate";
    private static final String NEW_GENERATOR_INSERT_ENDPOINT =  ConfigConstants.GENERATOR_BASE_URL + "/insertTemplates";
    private static final String FSET = "SUMEET";
    private static final String TSET = "FIRST";
    private static final String QUEUE_SOURCE = "ADMIN_INTERFACE";

    public static String getOldGeneratorInsertEndpoint() {
        return OLD_GENERATOR_INSERT_ENDPOINT;
    }

    public static String getNewGeneratorInsertEndpoint() {
        return NEW_GENERATOR_INSERT_ENDPOINT;
    }

    public static String buildTaskUrl(Entity entity, List<String> urls) {
        List<NameValuePair> queryList = new ArrayList<>();
        queryList.add(new RequestValuePair("d", entity.getDomain()));
        queryList.add(new RequestValuePair("adtag", entity.getAdtagId()));
        queryList.add(new RequestValuePair("size", entity.getSize()));
        queryList.add(new RequestValuePair("ifc", "1"));
        queryList.add(new RequestValuePair("img", "0"));
        queryList.add(new RequestValuePair("url", concatUrls(urls)));
        queryList.add(new RequestValuePair("qsrc", QUEUE_SOURCE));

        InboundParams inboundParams = InboundParams.getInstance();
        queryList.add(new RequestValuePair("up", inboundParams.getUseproxy()));
        queryList.add(new RequestValuePair("nc", inboundParams.getNocache()));

        String query = URLEncodedUtils.format(queryList, "UTF-8");
        return OLD_GENERATOR_QUEUETASK_URL + query;
    }

    public static String buildAsyncQueuePayload(Entity entity, List<String> urls, Admin admin) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", entity.getDomain());
        jsonObject.addProperty("adtagId", entity.getAdtagIdAsLong());
        jsonObject.addProperty("adtagSize", entity.getSize());
        jsonObject.addProperty("deviceType", "DESKTOP"); // Currently we only support desktop for generator
        jsonObject.addProperty("customerId", entity.getCustomerId());
        jsonObject.addProperty("partnerId", entity.getPartnerId());
        jsonObject.addProperty("queueSource", QUEUE_SOURCE);
        jsonObject.addProperty("adminId", admin.getAdminId());
        jsonObject.addProperty("hierarchyLevel", "GLOBAL");
        jsonObject.addProperty("sendImageInResponse", true);
        jsonObject.add("urls", DruidQueryBuilder.arraytoJson(urls));

        InboundParams inboundParams = InboundParams.getInstance();
        jsonObject.addProperty("insertTemplates", false);
        jsonObject.addProperty("noCache", inboundParams.getNocache());
        jsonObject.addProperty("useProxy", inboundParams.getUseproxy());
        return jsonObject.toString();
    }

    public static String buildAsyncInsertPayload(Admin adminId, Entity entity, int isActiveStatus, String templatePayload) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("domain", entity.getDomain());
        jsonObject.addProperty("adtagId", entity.getAdtagIdAsLong());
        jsonObject.addProperty("adtagSize", entity.getSize());
        jsonObject.addProperty("deviceType", "DESKTOP"); // Currently we only support desktop for generator
        jsonObject.addProperty("customerId", entity.getCustomerId());
        jsonObject.addProperty("partnerId", entity.getPartnerId());
        jsonObject.addProperty("queueSource", QUEUE_SOURCE);
        jsonObject.addProperty("adminId", adminId.getAdminId());
        jsonObject.addProperty("hierarchyLevel", "ENTITY");
        jsonObject.add("templates", new JsonParser().parse(templatePayload).getAsJsonArray());
        jsonObject.addProperty("insertTemplates", true);
        jsonObject.addProperty("templateActiveStatus", isActiveStatus);
        return jsonObject.toString();
    }


    public static String buildInsertBody(long entity_id, Admin admin_id, String cp, int isActive) {
        List<NameValuePair> queryList = new ArrayList<>();
        queryList.add(new RequestValuePair("adminId", admin_id.getAdminId()));
        queryList.add(new RequestValuePair("entityId", entity_id));
        queryList.add(new RequestValuePair("fset", FSET));
        queryList.add(new RequestValuePair("tset", TSET));
        queryList.add(new RequestValuePair("cp", cp));
        queryList.add(new RequestValuePair("tis", isActive));
        return URLEncodedUtils.format(queryList, "UTF-8");
    }

    public static List<String> getUrlsFromDruid(Entity entity) throws Exception {
        JsonObject jsonObject = DruidDAL.getEntityWiseUrls(entity);
        JsonArray data = jsonObject.get("data").getAsJsonArray();
        List<String> urls = new ArrayList<>();
        for (JsonElement jse : data) {
            JsonObject dataItem = jse.getAsJsonObject();
            urls.add(dataItem.get("URL").getAsString());
        }
        return urls;
    }

    private static String concatUrls(List<String> listUrls) {
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (String url : listUrls) {
            if (first) {
                first = false;
            } else {
                builder.append("|");
            }
            builder.append(url);
        }
        return builder.toString();
    }
}
