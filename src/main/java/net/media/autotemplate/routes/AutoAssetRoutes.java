package net.media.autotemplate.routes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.SDTaskDetail;
import net.media.autotemplate.bean.autoasset.SitePropertyDetail;
import net.media.autotemplate.bean.autoasset.TitleGenerationTaskDetail;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.AutoAssetDAL;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.routes.util.AbstractATRoute;
import net.media.autotemplate.services.AssetDetailService;
import net.media.autotemplate.services.AutoAssetConfigService;
import net.media.autotemplate.services.OzilApiService;
import net.media.autotemplate.services.SimilarityService;
import net.media.autotemplate.services.autoAsset.StableDiffusionService;
import net.media.autotemplate.util.Util;
import spark.Request;
import spark.Response;
import spark.RouteGroup;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.media.autotemplate.services.AssetDetailService.ASSET_MASTER_BASIS;
import static net.media.autotemplate.services.AssetDetailService.ASSET_MASTER_STATE;
import static spark.Spark.*;

public class AutoAssetRoutes implements RouteGroup {

    @Override
    public void addRoutes() {

        path("/query", () -> {
            post("/getMappedAssetListOnKey", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    Gson gson = new Gson();
                    String assetType = request.queryParams("asset_type");
                    String entityName = request.queryParams("entity_name");
                    String entityValueString = request.queryParams("entity_value");
                    List<String> entityValueList = Arrays.asList(entityValueString.split(","));
                    List<AssetDetail> assetList = AutoAssetDAL.getMappedAssetsOnKey(assetType, entityName, entityValueList);
                    assetList = assetList.stream()
                            .filter(asset -> asset.getIsActive() != -1)
                            .collect(Collectors.toList());

                    if (assetType.equals("IMAGE")) {
                        assetList = AssetDetailService.addPromptToAssetDetail(assetList);
                    }

                    JsonArray assetData = gson.toJsonTree(assetList).getAsJsonArray();
                    return new ApiResponse(assetData);
                }
            });

            post("/getMappedAssetListOnKeyValue", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String keyValueString = request.queryParams("key_values");
                    List<String> keyValueList = Arrays.asList(keyValueString.split(","));
                    List<AssetDetail> assetList = AutoAssetDAL.getAutoAssetsMappedOnKeyValue(keyValueList);
                    assetList = assetList.stream()
                            .filter(asset -> asset.getIsActive() != -1)
                            .collect(Collectors.toList());
                    Gson gson = new Gson();
                    JsonArray data = gson.toJsonTree(assetList).getAsJsonArray();
                    return new ApiResponse(data);
                }
            });

            get("/getAssetsForReview", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    int isActive = -1;
                    List<AssetDetail> assetDetailList = AutoAssetDAL.getAssetToReview(request, isActive);
                    JsonArray assetList = new JsonArray();
                    JsonObject respToBeReturned = new JsonObject();
                    assetDetailList.stream().map(AssetDetail::getAsJson).forEach(j -> assetList.add(j));
                    respToBeReturned.add("assetList", assetList);
                    if (assetDetailList.size() > 0) {
                        respToBeReturned.addProperty("cursor_id", assetDetailList.get(assetDetailList.size() - 1).getId());
                        respToBeReturned.addProperty("updation_date", assetDetailList.get(assetDetailList.size() - 1).getUpdationDate());
                    }
                    return new ApiResponse(respToBeReturned);
                }
            });

            post("/blockUnblockAsset", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String requestBody = request.body();
                    List<AssetDetail> updatedAssetList = new Gson().fromJson(requestBody, new TypeToken<List<AssetDetail>>() {
                    }.getType());
                    Long adminId = RequestGlobal.getAdmin().getAdminId();
                    AutoAssetDAL.upsertAutoAssetData(updatedAssetList, ASSET_MASTER_BASIS, ASSET_MASTER_STATE, adminId);
                    return new ApiResponse("message", "Success");
                }
            });

            post("/reviewAsset", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String requestBody = request.body();
                    List<AssetDetail> updatedAssetList = new Gson().fromJson(requestBody, new TypeToken<List<AssetDetail>>() {
                    }.getType());
                    Long adminId = RequestGlobal.getAdmin().getAdminId();
                    List<AssetDetail> activeAssets = updatedAssetList.stream()
                            .filter(asset -> asset.getIsActive() == 1)
                            .collect(Collectors.toList());
                    if (activeAssets.size() > 0)
                        AutoAssetDAL.upsertAutoAssetData(activeAssets, ASSET_MASTER_BASIS, ASSET_MASTER_STATE, adminId);
                    AutoAssetDAL.updateAutoAssetDataForReview(updatedAssetList, adminId);
                    return new ApiResponse("message", "Success");
                }
            });

            post("/getSimilarityScore", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String keyword = request.queryParams("keyword");
                    String demandBasis = request.queryParams("demand_basis");
                    Double score = SimilarityService.getSimilarityScore(demandBasis, keyword);
                    return new ApiResponse("score", String.valueOf(score));
                }
            });

            post("/getKBBImage", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String size = request.queryParams("size");
                    String keyword = request.queryParams("keyword");
                    ApiResponse apiResponse = AssetDetailService.getKBBImagesForKeyword(size, keyword);
                    return apiResponse;
                }
            });

            // TODO: 26/12/23 display proper error msg if occurred V2
            post("/getAARequest", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    int limit = Integer.parseInt(request.queryParams("limit"));
                    int offset = Integer.parseInt(request.queryParams("offset"));
                    long adminId = AdminFactory.getAdmin(request).getAdminId();
                    List<ATRequestType> atRequestTypeList = Arrays.stream(request.queryParams("request_types").split(","))
                            .map(ATRequestType::getATRequestTypeFromDbName)
                            .collect(Collectors.toList());
                    List<ATRequest> atRequestList = AutoTemplateDAL.getAARequestForAdminId(limit, offset, adminId, atRequestTypeList);
                    JsonArray requestList = new JsonArray();
                    atRequestList.stream().forEach(aaRequest -> requestList.add(aaRequest.getAsJson()));
                    return new ApiResponse(requestList);
                }
            });


            post("/generateStableDiffusionImages", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String requestBody = request.body();
                    Gson gson = new Gson();
                    JsonArray stableDiffusionInputDataJsonArray = gson.fromJson(requestBody, JsonArray.class);
                    JsonObject jsonObject = stableDiffusionInputDataJsonArray.get(0).getAsJsonObject();
                    String keyword = Util.isSet(jsonObject.get("kwd")) ? jsonObject.get("kwd").getAsString() : "";
                    SDTaskDetail sdTaskDetail = new SDTaskDetail(keyword, jsonObject.get("pmt"), jsonObject.get("npmt"), jsonObject.get("ver"));
                    JsonObject jsonResponse = OzilApiService.getOzilStableDiffusionImages(sdTaskDetail);
                    return new ApiResponse(jsonResponse);
                }
            });

            post("/queueStableDiffusionImageGeneration", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    long adminId = AdminFactory.getAdmin(request).getAdminId();
                    ApiResponse apiResponse = StableDiffusionService.queueStableDiffusionRequest(request, adminId);
                    return apiResponse;
                }
            });


            post("/getStableDiffusionImagesByImageFetchId", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    long imageFetchId = Long.parseLong(request.queryParams("image_fetch_id"));
                    JsonObject jsonResponse = OzilApiService.getOzilStableDiffusionImagesForImageFetchId(imageFetchId);
                    ApiResponse apiResponse = new ApiResponse(jsonResponse);
                    return apiResponse;
                }
            });

            post("/getStableDiffusionImagesFromRequestId", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    long requestId = Long.parseLong(request.queryParams("request_id"));
                    List<AATaskDetail> aaTaskDetailList = AutoTemplateDAL.getTaskOutputDetailsForRequest(requestId);
                    JsonObject taskStateInfo = AssetDetailService.getTaskStateInfo(aaTaskDetailList);
                    JsonArray stableDiffusionImageData = StableDiffusionService.getStableDiffusionImageListFromAATaskDetailList(aaTaskDetailList);
                    if (stableDiffusionImageData.size() == 0) {
                        List<StableDiffusionImageDetail> stableDiffusionMasterAssetList = AutoTemplateDAL.getStableDiffusionImageDetailForRequest(requestId);
                        taskStateInfo = AssetDetailService.getTaskStateInfoForStableDiffusionImageDetail(stableDiffusionMasterAssetList);
                        stableDiffusionImageData = new Gson().toJsonTree(stableDiffusionMasterAssetList).getAsJsonArray();
                    }
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.add("image_list", stableDiffusionImageData);
                    jsonResponse.add("task_status_info", taskStateInfo);
                    return new ApiResponse(jsonResponse);
                }
            });

            post("/suspendAARequest", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    long requestId = Long.parseLong(request.queryParams("request_id"));
                    AutoTemplateDAL.suspendAARequest(requestId);
                    return new ApiResponse("message", "Success");
                }
            });

            post("/mapAsset", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String requestBody = request.body();
                    Gson gson = new Gson();
                    JsonArray jsonArray = gson.fromJson(requestBody, JsonArray.class);
                    String assetType = request.queryParams("asset_type");
                    Long adminId = RequestGlobal.getAdmin().getAdminId();
                    List<AssetDetail> modifiedAssetDetailList = AssetDetailService.getAssetDetailListForKBBImages(jsonArray, assetType);
                    AutoAssetDAL.upsertAutoAssetData(modifiedAssetDetailList, ASSET_MASTER_BASIS, ASSET_MASTER_STATE, adminId);
                    return new ApiResponse("message", "Success");
                }
            });

            post("/mapHostedImage", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    JsonObject jsonResponse = AssetDetailService.mapHostedImage(request);
                    ApiResponse apiResponse = new ApiResponse(jsonResponse);
                    return apiResponse;
                }
            });

            post("/mapBulkHostedImages", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    JsonObject jsonResponse = AssetDetailService.queueAssetMapping(request);
                    ApiResponse apiResponse = new ApiResponse(jsonResponse);
                    return apiResponse;
                }
            });

            post("/mapStableDiffusionImage", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    JsonObject jsonResponse = AssetDetailService.mapHostedImage(request);
                    ApiResponse apiResponse = new ApiResponse(jsonResponse);
                    return apiResponse;
                }
            });
        });

        path("/query/title", () -> {
            post("/generateTitlesFromOzil", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String requestBody = request.body();
                    Gson gson = new Gson();
                    JsonArray titleFromOzilInputDataJsonArray = gson.fromJson(requestBody, JsonArray.class);
                    JsonObject jsonObject = titleFromOzilInputDataJsonArray.get(0).getAsJsonObject();
                    JsonElement demandKwdElement = jsonObject.get("demand_kwd");
                    JsonElement publisherPageTitleElement = jsonObject.get("publisher_page_title");
                    JsonElement urlElement = jsonObject.get("publisher_page_url");
                    JsonElement promptElement = jsonObject.get("prompt");

                    JsonArray demandKwdJsonArray = demandKwdElement.getAsJsonArray();
                    String demandKwd = demandKwdJsonArray.get(0).getAsString();
                    String publisherPageTitle = Util.isSet(publisherPageTitleElement) && Util.isStringSet(publisherPageTitleElement.getAsString()) ? publisherPageTitleElement.getAsString() : "";
                    String url = Util.isSet(urlElement) && Util.isStringSet(urlElement.getAsString()) ? urlElement.getAsString() : "";
                    String prompt = Util.isSet(promptElement) && Util.isStringSet(promptElement.getAsString()) ? promptElement.getAsString() : null;

                    JsonObject jsonResponse = AssetDetailService.generateTitleFromOzil(demandKwd, publisherPageTitle, url, prompt);
                    ApiResponse apiResponse = new ApiResponse(jsonResponse);
                    return apiResponse;
                }
            });

            post("/queueTitleGeneration", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String requestBody = request.body();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<TitleGenerationTaskDetail>>() {
                    }.getType();
                    List<TitleGenerationTaskDetail> titleGenerationTaskDetailList = gson.fromJson(requestBody, listType);
                    Map<String, Long> promptToIdMap = AssetDetailService.getPromptToIdMapFromTitleGenerationTaskDetails(titleGenerationTaskDetailList);
                    JsonObject jsonResponse = AssetDetailService.queueTitlesGeneration(titleGenerationTaskDetailList, promptToIdMap);
                    ApiResponse apiResponse = new ApiResponse(jsonResponse);
                    return apiResponse;
                }
            });

            post("/getGeneratedAssetListByRequestId", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    Long requestId = Long.parseLong(request.queryParams("request_id"));
                    List<AATaskDetail> aaTaskDetailList = AutoTemplateDAL.getTaskOutputDetailsForRequest(requestId);
                    JsonArray jsonArray = AssetDetailService.getGeneratedTitleDetailsFromAATaskDetailList(aaTaskDetailList);
                    ApiResponse apiResponse = new ApiResponse(jsonArray);
                    return apiResponse;
                }
            });
        });

        path("/query/config", () -> {
            post("/getConfig", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    String entityName = request.queryParams("entity_name");
                    String entityValueString = request.queryParams("entity_value").split(",")[0];
                    JsonObject jsonResponse = AutoAssetConfigService.getAutoAssetConfigForEntity(entityName, entityValueString);
                    ApiResponse apiResponse = new ApiResponse(jsonResponse);
                    return apiResponse;
                }
            });

            post("/updateConfig", new AbstractATRoute() {
                @Override
                public ApiResponse getResponse(Request request, Response response) throws Exception {
                    Gson gson = new Gson();
                    String requestBody = request.body();
                    String adminEmail = AdminFactory.getAdmin(request).getAdminEmail();
                    Type listType = new TypeToken<List<SitePropertyDetail>>() {
                    }.getType();
                    List<SitePropertyDetail> sitePropertyDetailList = gson.fromJson(requestBody, listType);
                    AutoAssetConfigService.updateConfig(sitePropertyDetailList, adminEmail);
                    return new ApiResponse("message", "Success");
                }
            });
        });
    }
}
