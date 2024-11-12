package net.media.autotemplate.routes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.ATRequest;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.bean.mapping.MappingUpdate;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.routes.util.BlockingRoute;
import net.media.autotemplate.routes.util.BulkRoute;
import net.media.autotemplate.routes.util.MappingRoute;
import net.media.autotemplate.services.ATBlockingService;
import net.media.autotemplate.services.TemplateBulkService;
import net.media.autotemplate.services.mapping.MappingService;
import net.media.autotemplate.util.Util;
import spark.Request;
import spark.Response;
import spark.RouteGroup;

import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;

/*
    Created by shubham-ar
    on 13/2/18 5:20 PM
*/

public class ATRoutes implements RouteGroup {
    public static final Gson GSON = Util.getGson();
    public static final List<String> templateResponseFields = Arrays.asList("templateId", "templateSize", "hierarchyLevel", "entity", "admin_name", "date_added");

    @Override
    public void addRoutes() {

        path("/update", () -> {
            post("/blocking", new BlockingRoute() {
                @Override
                protected ApiResponse makeResponse(Request req, ATBlockingService atBlockingService) throws Exception {
                    return new ApiResponse("status", atBlockingService.updateEntity(req.body()));
                }
            });

            post("/mapping/:mappingType/insert", new MappingRoute() {
                @Override
                protected ApiResponse doTask(Request req, Response resp, MappingService mappingService) throws Exception {
                    MappingUpdate<? extends MappingTemplate> completedUpdates = mappingService.insertMappings(req.body());
                    return new ApiResponse(getMappingUpdateResponse(completedUpdates));
                }
            });


            post("/mapping/:mappingType/delete", new MappingRoute() {
                @Override
                protected ApiResponse doTask(Request req, Response resp, MappingService mappingService) throws Exception {
                    MappingUpdate<? extends MappingTemplate> completedUpdates = mappingService.deleteMappings(req.body());
                    return new ApiResponse(getMappingUpdateResponse(completedUpdates));
                }
            });

            post("/bulkMapping/insert", new BulkRoute() {
                @Override
                protected ApiResponse doTask(Request req, Response resp, TemplateBulkService templateBulkService) throws Exception {
                    return new ApiResponse(templateBulkService.insertMappingRequestDetails(req.body()));
                }
            });

            post("/bulkUnmapping", new BulkRoute() {
                @Override
                protected ApiResponse doTask(Request req, Response resp, TemplateBulkService templateBulkService) throws Exception {
                    return new ApiResponse(templateBulkService.insertUnmappingRequestDetails(req.body()));
                }
            });

            get("/request", new BulkRoute() {
                @Override
                protected ApiResponse doTask(Request req, Response resp, TemplateBulkService templateBulkService) throws Exception {
                    List<ATRequestType> atRequestTypeList = Arrays.asList(ATRequestType.values());
                    List<ATRequest> requests = AutoTemplateDAL.getAARequestForAdminId(100, 0, templateBulkService.getAdmin().getAdminId(), atRequestTypeList);
                    JsonArray requestJsonArray = new JsonArray();
                    requests.stream().map(ATRequest::getAsJson).forEach(jb -> requestJsonArray.add(jb));
                    return new ApiResponse(requestJsonArray);
                }
            });

            post("/bulkBlocking", new BulkRoute() {
                @Override
                protected ApiResponse doTask(Request req, Response resp, TemplateBulkService templateBulkService) throws Exception {
                    return new ApiResponse(templateBulkService.insertBlockingRequestDetails(req.body()));
                }
            });

        });

        path("/query", () -> {

            get("/blocking/:type", new BlockingRoute() {
                @Override
                protected ApiResponse makeResponse(Request req, ATBlockingService atBlockingService) throws Exception {
                    return new ApiResponse(atBlockingService.getTableData());
                }
            });

            get("/mapping/:mappingType", new MappingRoute() {
                @Override
                protected ApiResponse doTask(Request req, Response resp, MappingService mappingService) throws Exception {
                    List<MappingTemplate> templates = mappingService.getTemplates();
                    JsonArray mappings = getMappingTemplateResponse(templates);
                    JsonObject responseJson = new JsonObject();
                    responseJson.add("mappings", mappings);
                    responseJson.add("fields", GSON.toJsonTree(templateResponseFields));
                    responseJson.addProperty("aclStatus", mappingService.getAclStatus().getStatusCode());
                    responseJson.addProperty("aclErrMessage", mappingService.getAclStatus().getErrMessage());
                    return new ApiResponse(responseJson);
                }
            });

        });
    }

    public JsonObject getMappingUpdateResponse(MappingUpdate<? extends MappingTemplate> mappingUpdate) {
        JsonObject response = new JsonObject();
        JsonArray updates = getMappingTemplateResponse(mappingUpdate.getUpdates());
        response.add("updates", updates);
        return response;
    }

    public JsonArray getMappingTemplateResponse(List<? extends MappingTemplate> mappingTemplates) {
        JsonArray templates = new JsonArray();
        for (MappingTemplate mappingTemplate : mappingTemplates) {
            JsonObject mappingResponse = new JsonObject();
            mappingResponse.addProperty("templateId", mappingTemplate.getTemplateId());
            mappingResponse.addProperty("templateSize", mappingTemplate.getTemplateSize());
            mappingResponse.addProperty("mappingType", mappingTemplate.getMappingType().name());
            mappingResponse.addProperty("hierarchyLevel", mappingTemplate.getHierarchyLevel());
            mappingResponse.addProperty("entity", mappingTemplate.getEntityValue());
            mappingResponse.addProperty("systemPageType", mappingTemplate.getSystemPageType().getName());
            mappingResponse.addProperty("businessUnit", mappingTemplate.getBusinessUnit().getName());
            mappingResponse.addProperty("admin_name", mappingTemplate.getAdminName());
            mappingResponse.addProperty("date_added", mappingTemplate.getUpdationDate());
            templates.add(mappingResponse);
        }
        return templates;
    }

}

