package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.configs.GlobalConfig;
import net.media.autotemplate.routes.util.ApiException;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/*
    Created by shubham-ar
    on 4/4/18 10:32 PM   
*/

@Deprecated
public class ZeroColorGenerationService {
    private static final String ZERO_COLOR_BASE_URL = "http://at.internal.media.net/TemplateGenerator/";
    private static final String ZERO_COLOR_GET_ENDPOINT = "getZeroColorTemplates";
    private static final String ZERO_COLOR_INSERT_ENDPOINT = "insertZeroColorTemplates";
    private static final String FRAMEWORK_LIST_PROPERTY = "ZERO_COLOR_FRAMEWORKS_META_INFO";
    private static final Gson GSON = Util.getGson();
    private static final Logger LOG = LogManager.getLogger(ZeroColorGenerationService.class);
    private final String entity;
    private final CreativeConstants.Level level;
    private final Admin admin;
    private final BusinessUnit businessUnit;

    public ZeroColorGenerationService(String entity, CreativeConstants.Level level, Admin admin, BusinessUnit businessUnit) throws ExecutionException, DatabaseException {
        this.entity = entity;
        this.level = level;
        this.admin = admin;
        this.businessUnit = businessUnit;
        AclService.checkAccess(entity, level, admin, businessUnit);
    }

    public String getTemplates(List<String> frameworks, List<String> sizes) throws Exception {
//        return mockResponse();
        String frameworkParam = getListParam(frameworks);
        String sizeParam = getListParam(sizes);
        String requestUrl = String.format("%s&f-ids=%s&sizes=%s", getBaseUrl(ZERO_COLOR_GET_ENDPOINT), frameworkParam, sizeParam);
        NetworkResponse generatorResponse = NetworkUtil.getRequest(requestUrl);
        if (generatorResponse.isOK())
            return generatorResponse.getBody();

        throw new Exception("ZERO_COLOR_SERVICE_ERROR|" + generatorResponse.getBody());
    }

    public String insertTemplates(String update) throws Exception {
        JsonArray json = GSON.fromJson(update, JsonArray.class);
        String url = ZERO_COLOR_BASE_URL + ZERO_COLOR_INSERT_ENDPOINT, body = getParams() + "&template-list=" + json.toString();
        LoggingService.log(LOG, Level.info, url + "|" + body);
        NetworkResponse response = NetworkUtil.formPostRequest(url, body);
        if (response.isOK())
            return response.getBody();
        throw new ApiException("INSERT_TEMPLATES_FAILED", response.getBody(), this);
    }

    private String getBaseUrl(String endpoint) {
        return ZERO_COLOR_BASE_URL + endpoint + String.format("?%s", getParams());
    }

    private String getParams() {
        return String.format("entity=%s&hierarchy-level=%s&admin-id=%s", entity, this.level.name(), Math.abs(this.admin.getAdminId()));
    }

    private static String getListParam(List<String> items) throws UnsupportedEncodingException {
        StringBuilder paramsBuilder = new StringBuilder();
        items.forEach(item -> paramsBuilder.append(item).append("|"));
        paramsBuilder.deleteCharAt(paramsBuilder.length() - 1);
        return URLEncoder.encode(paramsBuilder.toString(), "UTF-8");
    }

    public static List<String> getValidFrameworks() throws DatabaseException, IOException {
        JsonObject frameworkJson = GSON.fromJson(GlobalConfig.getSimpleProperty(FRAMEWORK_LIST_PROPERTY), JsonObject.class);
        JsonArray frameworks = frameworkJson.get("zero-color-frameworks").getAsJsonArray();
        Set<String> frameworkSet = new HashSet<>();
        frameworks.forEach(framework -> frameworkSet.add(framework.getAsJsonObject().get("framework_id").getAsString()));
        return new ArrayList<>(frameworkSet);
    }
}
