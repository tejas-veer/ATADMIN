package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.bean.config.ConfigLine;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.ConfigProperties;
import net.media.autotemplate.constants.GeneratorType;
import net.media.autotemplate.dal.configs.AutoTemplateConfigMaster;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.util.GeneratorUtil;
import net.media.autotemplate.util.SysProperties;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.generator.EntityUrlCache;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.autotemplate.util.logging.UserActionLogging;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.BadPayloadException;
import spark.Request;

import javax.management.BadAttributeValueExpException;
import javax.xml.crypto.URIReferenceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/*
    Created by shubham-ar
    on 31/10/17 1:24 PM   
*/
public class GeneratorService {
    public static final Gson GSON = net.media.autotemplate.util.Util.getGson();
    private static final Logger LOG = LogManager.getLogger(GeneratorService.class);
    Entity entity;
    Admin admin;

    public GeneratorService(Request request) throws Exception {
        entity = AutoTemplateDAL.getEntityInfo(Long.parseLong(request.params(":entityId")));
        admin = AdminFactory.getAdmin(request);
        String failedCheck = validateInput();
        if (Util.isStringSet(failedCheck)) {
            throw new BadPayloadException(failedCheck);
        }
    }

    public JsonObject queueTask(GeneratorType generatorType) throws Exception {
        List<String> urls;

        if (ConfigConstants.LOCAL) {
            urls = new ArrayList<String>() {{
                add("https://stackoverflow.com/questions/449445/java-static");
            }};
        } else urls = EntityUrlCache.get(entity);


        if (Util.empty(urls)) {
            throw new BadAttributeValueExpException("No Urls Found");
        }
        urls = removeHashFromUrls(urls);

        urls = Util.safeSublist(urls, 0, 1);

        NetworkResponse response = fetchGeneratorResponse(entity, urls, generatorType);
        if (response.getStatus() != 200)
            throw new URIReferenceException("Endpoint responded with " + response.getStatus() + " | " + response.getBody());
        JsonObject generatorResponse = new JsonObject();
        generatorResponse.addProperty("generator", response.getBody());
        JsonArray urlJson = new JsonArray();
        for (String crawlUrl : urls)
            urlJson.add(crawlUrl);

        generatorResponse.add("urls", urlJson);
        return generatorResponse;
    }

    private NetworkResponse fetchGeneratorResponse(Entity entity, List<String> urls, GeneratorType generatorType) throws Exception {
        if (generatorType.equals(GeneratorType.OLD)) {
            String taskUrl = GeneratorUtil.buildTaskUrl(entity, urls);
            LoggingService.log(LOG, Level.info, "Queueing Task | " + taskUrl);
            return NetworkUtil.getRequest(taskUrl);
        } else {
            String jsonPayload = GeneratorUtil.buildAsyncQueuePayload(entity, urls, admin);
            return NetworkUtil.formPostRequest(GeneratorUtil.GENERATOR_ASYNC_TASKCREATION_URL, "jsonData=" + jsonPayload);
        }
    }

    private static List<String> removeHashFromUrls(List<String> urls) {
        List<String> list = new ArrayList<>();
        urls.forEach(item -> {
            //todo : try to make this regex
            int i = item.indexOf("#mnet");
            if (i > 0) {
                item = item.substring(0, i);
            }
            list.add(item);
        });
        return list;
    }

    public String mockResponse() {
        Scanner sc = new Scanner(SysProperties.class.getResourceAsStream("/MockResponse.txt"));
        String mockJsonResponse = sc.nextLine();
        sc.close();
        return mockJsonResponse;
    }


    public JsonObject insertTask(String payload, GeneratorType generatorType) throws Exception {
        if (generatorType.equals(GeneratorType.OLD)) {
            return insertOldTemplates(payload);
        }
        return insertNewTemplates(payload);
    }

    private JsonObject insertNewTemplates(String templatePayload) throws Exception {
        boolean needConfirmation = isWhiteListEntity();
        String request = GeneratorUtil.buildAsyncInsertPayload(admin, entity, needConfirmation ? 0 : 1, templatePayload);
        NetworkResponse response = NetworkUtil.postRequest(GeneratorUtil.getNewGeneratorInsertEndpoint(), request);

        if (response.getStatus() != HttpStatus.SC_OK) {
            throw new Exception("NOT OK |" + response.getBody());
        }

        JsonObject jsonObject = GSON.fromJson(response.getBody(), JsonObject.class);
        int status = jsonObject.get("status").getAsInt();

        if (status == 0) {
            throw new Exception("INSERTION_ERROR |" + jsonObject.get("reason").getAsString());
        }

        // TODO : FIX THIS COMMENT - OR THE REQUEST BODY GETTING LOGGED
        UserActionLogging.log(admin, "INSERTED_TEMPLATES", "Inserted Templates on " + entity.getEntityId() + "|" + response.getBody());
        jsonObject.addProperty("needConfirmation", needConfirmation);
        return jsonObject;
    }

    private JsonObject insertOldTemplates(String colorProperties) throws Exception {
        long entityId = entity.getEntityId();
        boolean needConfirmation = isWhiteListEntity();
        String postBody = GeneratorUtil.buildInsertBody(entityId, admin, colorProperties, needConfirmation ? 0 : 1);
        NetworkResponse response = NetworkUtil.formPostRequest(GeneratorUtil.getOldGeneratorInsertEndpoint(), postBody);

        if (response.getStatus() != HttpStatus.SC_OK) {
            throw new Exception("NOT OK |" + response.getBody());
        }

        JsonObject jsonObject = GSON.fromJson(response.getBody(), JsonObject.class);
        int status = jsonObject.get("status").getAsInt();

        if (status == 0) {
            throw new Exception("INSERTION_ERROR |" + jsonObject.get("reason").getAsString());
        }

        UserActionLogging.log(admin, "INSERTED_TEMPLATES", "Inserted Templates on " + entityId + "|" + response.getBody());
        jsonObject.addProperty("needConfirmation", needConfirmation);
        return jsonObject;
    }

    private String validateInput() {

        if (!Util.isSet(entity)) {
            return "Entity is improperly set";
        }

        if (!Util.isStringSet(entity.getDomain())) {
            return "Domain is improperly set";
        }

        if (!Util.isStringSet(entity.getAdtagId())) {
            return "Adtag is improperly set";
        }

        return null;
    }

    public void putLog(int status, String responseStr) {
        UserActionLogging.log(admin, "ASYNC_QUEUE_TASK", entity.getEntityId() + " -> " + status + " | " + responseStr);
    }

    private boolean isWhiteListEntity() throws Exception {
        ConfigLine configLine = AutoTemplateConfigMaster.getInstance().getConfig(entity.getAdtagInfo(), ConfigProperties.MARK_TEMPLATE_INACTIVE.name());
        if (Objects.nonNull(configLine))
            return Boolean.valueOf(configLine.getValue());
        return false;
    }


    public static NetworkResponse pollStatus(String reqId) throws IOException {
        return NetworkUtil.getRequest( ConfigConstants.GENERATOR_BASE_URL + "/" + "asyncTemplateCreation/poll?rid=requestId%7C" + reqId);
    }

    public static NetworkResponse poll(String reqId) throws IOException {
        return NetworkUtil.getRequest("http://at.internal.media.net/TemplateGenerator/getTaskResult?rid=requestId%7C" + reqId);
    }
}