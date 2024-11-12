package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.AssetDetail;
import net.media.autotemplate.bean.RequestGlobal;
import net.media.autotemplate.bean.StableDiffusionImageDetail;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.AssetMappingTaskDetail;
import net.media.autotemplate.bean.autoasset.PromptDetail;
import net.media.autotemplate.bean.autoasset.TitleGenerationTaskDetail;
import net.media.autotemplate.constants.AutoAssetConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.AutoAssetDAL;
import net.media.autotemplate.enums.AAImageSource;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;
import net.media.database.DatabaseException;
import spark.Request;

import javax.servlet.http.Part;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class AssetDetailService {
    public static final String ASSET_MASTER_BASIS = "101";
    public static final String ASSET_MASTER_STATE = "v2";

    public static final String KBB_IMAGE_ENDPOINT = "http://g-use1d-kwd-api-realapi.srv.media.net/kbb/multiple_image?";
    public static final String KBB_IMAGE_ENDPOINT_FIXED_PARAM = "img=1&d=rtb&maxno=20&calling_source=aa";
    public static final String GENERATE_TITLE_DEFAULT_PROMPT = "Generate a list of 5 compelling ad titles for a digital advertisement with the following information: (Keep in mind that the user has shown intent of reading the publisher's page, so the ad titles should appeal to that intent) Advertiser Keyword: {{DEMAND_KEYWORD}}, Titles should be simple and effective. Limit ad title length to 60 characters.";

    public static List<AssetDetail> addPromptToAssetDetail(List<AssetDetail> assetList) throws DatabaseException {
        List<AssetDetail> sdGptGeneratedAssets = assetList.stream().filter(asset -> asset.getSetId() == AAImageSource.STABLE_DIFFUSION_GPT.getSetId()).collect(Collectors.toList());
        List<Long> promptIds = new ArrayList<>(sdGptGeneratedAssets.stream()
                .map(AssetDetail::getBasis)
                .filter(basis -> Util.isNumeric(basis))
                .map(Long::valueOf)
                .collect(Collectors.toSet()));

        Map<Long, String> idToPromptMap = AssetDetailService.getIdToPromptMap(promptIds);

        sdGptGeneratedAssets.stream()
                .forEach(asset -> {
                    String basis = asset.getBasis();
                    if (Util.isNumeric(basis)) {
                        asset.setPrompt(idToPromptMap.get(Long.valueOf(basis)));
                    }
                });
        return assetList;
    }

    public static ApiResponse getKBBImagesForKeyword(String size, String keyword) throws Exception {
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());
        String url = KBB_IMAGE_ENDPOINT + KBB_IMAGE_ENDPOINT_FIXED_PARAM + String.format("&imgsize=%s&kwdlist=%s", size, encodedKeyword);
        NetworkResponse networkResponse = NetworkUtil.getRequest(url);
        return networkResponse.getApiResponse();
    }


    public static Map<String, String> getLocalToUploadedImagUrlMap(List<Part> imageParts, int setId) throws Exception {
        String dirPath = AAImageSource.getImageSourceDirPathFromSetId(setId);
        return OzilApiService.getOzilImageUrlsForLocalImages(imageParts, dirPath);
    }

    public static JsonObject mapHostedImage(Request request) throws Exception {
        Long adminId = RequestGlobal.getAdmin().getAdminId();

        String requestBody = request.body();
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(requestBody, JsonArray.class);

        Map<Integer, List<String>> setIdToImageUrlsMap = getSetIdToImageUrlMap(jsonArray);
        Map<Integer, Map<String, String>> setIdHostedImageToUploadedMap = getOzilImageUrlForHostedImages(setIdToImageUrlsMap);

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject obj = jsonArray.get(i).getAsJsonObject();
            int setId = obj.get("set_id").getAsInt();
            Map<String, String> hostedImageToUploadedImageMap = setIdHostedImageToUploadedMap.get(setId);

            String hostedImageUrl = obj.get("asset_value").getAsString();
            obj.addProperty("uploaded_asset_url", hostedImageToUploadedImageMap.get(hostedImageUrl));
        }
        JsonObject jsonResponse = mapImageAssetData(jsonArray, adminId);
        return jsonResponse;
    }

    public static JsonObject queueAssetMapping(Request request) throws Exception {
        Long adminId = RequestGlobal.getAdmin().getAdminId();

        String requestBody = request.body();
        Gson gson = new Gson();
        List<AssetMappingTaskDetail> assetMappingTaskDetailList = gson.fromJson(requestBody, new TypeToken<List<AssetMappingTaskDetail>>() {
        }.getType());
        List<AATaskDetail> aaTaskDetailList = new ArrayList<>();

        for (AssetMappingTaskDetail assetMappingTaskDetail : assetMappingTaskDetailList) {
            String taskInputDetails = gson.toJson(assetMappingTaskDetail);
            AATaskDetail aaTaskDetail = new AATaskDetail(taskInputDetails);
            aaTaskDetailList.add(aaTaskDetail);
        }

        ATRequestType requestType = ATRequestType.ASSET_MAPPING;
        ATRequestState state = ATRequestState.UNPROCESSED;
        long requestId = AutoTemplateDAL.insertAATaskDetails(aaTaskDetailList, requestType, state, adminId);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("request_id", requestId);
        return jsonResponse;
    }

    private static Map<Integer, List<String>> getSetIdToImageUrlMap(JsonArray jsonArray) {
        Map<Integer, List<String>> setIdToImageUrlsMap = new HashMap<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement element = jsonArray.get(i);
            JsonObject jsonObject = (JsonObject) element;
            int setId = jsonObject.get("set_id").getAsInt();
            String imageUrl = jsonObject.get("asset_value").getAsString();
            setIdToImageUrlsMap.computeIfAbsent(setId, k -> new ArrayList<>()).add(imageUrl);
        }
        return setIdToImageUrlsMap;
    }

    public static Map<Integer, Map<String, String>> getOzilImageUrlForHostedImages(Map<Integer, List<String>> setIdToImageUrlsMap) throws Exception {
        Map<Integer, Map<String, String>> originalToUploadedUrlMap1 = new HashMap<>();

        for (Map.Entry<Integer, List<String>> entry : setIdToImageUrlsMap.entrySet()) {
            int setId = entry.getKey();
            String dirPath = AAImageSource.getImageSourceDirPathFromSetId(setId);
            List<String> imageUrls = entry.getValue();

            Map<String, String> originalToUploadedUrlMap = OzilApiService.getOzilImageUrlForHostedImages(imageUrls, dirPath);
            originalToUploadedUrlMap1.put(setId, originalToUploadedUrlMap);
        }

        return originalToUploadedUrlMap1;
    }

    public static List<AssetDetail> getAssetDetailListForOzilImages(JsonArray jsonArray) {
        String assetType = AutoAssetConstants.ASSET_TYPE_IMAGE;
        float score = 0;

        List<AssetDetail> assetDetailsList = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            JsonObject item = element.getAsJsonObject();
            String entityName = item.get("entity_name").getAsString();
            String entityValue = item.get("entity_value").getAsString();
            String[] entityValueSplits = entityValue.split(AutoAssetConstants.ENTITY_VALUE_WITH_SIZE_SEPARATOR);
            JsonElement sizeElement = item.get("asset_size");
            String size = (Util.isSet(sizeElement) && !sizeElement.isJsonNull() && !sizeElement.getAsString().equals("null")) ? sizeElement.getAsString() : (entityValueSplits.length > 1) ? entityValueSplits[1] : null;
            int setId = item.get("set_id").getAsInt();
            String keyValue = (assetType + AutoAssetConstants.KEY_VALUE_SEPARATOR + entityName + AutoAssetConstants.KEY_VALUE_SEPARATOR + entityValue).toLowerCase();
            String assetValue = item.get("uploaded_asset_url").getAsString();
            JsonElement keywordElement = item.get("keyword");
            String basis = (Util.isSet(keywordElement) && !keywordElement.isJsonNull() && !keywordElement.getAsString().equals("null")) ? keywordElement.getAsString() : "";
            int isActive = 1;
            Long extAssetId = null;

            AssetDetail assetDetail = new AssetDetail(entityName, entityValue, keyValue, assetType, extAssetId, assetValue, basis, setId, size, score, isActive);
            assetDetailsList.add(assetDetail);
        }
        return assetDetailsList;
    }

    public static List<AssetDetail> getAssetDetailListForKBBImages(JsonArray jsonArray, String assetType) {
        float score = 0;

        List<AssetDetail> assetDetailsList = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            JsonObject item = element.getAsJsonObject();
            String entityName = item.get("entity_name").getAsString();
            String entityValue = item.get("entity_value").getAsString();
            String keyValue = (assetType + AutoAssetConstants.KEY_VALUE_SEPARATOR + entityName + AutoAssetConstants.KEY_VALUE_SEPARATOR + entityValue).toLowerCase();
            int setId = item.get("set_id").getAsInt();
            String[] entityValueSplits = entityValue.split(AutoAssetConstants.ENTITY_VALUE_WITH_SIZE_SEPARATOR);
            String size = (entityValueSplits.length > 1 && assetType.equals(AutoAssetConstants.ASSET_TYPE_IMAGE)) ? entityValueSplits[1] : null;
            JsonElement assetValueElement = item.has("assetValue") ? item.get("assetValue") : item.get("asset_value");
            String assetValue = assetValueElement.getAsString();
            String basis = Util.isSet(item.get("keyword")) && Util.isStringSet(item.get("keyword").getAsString()) ? item.get("keyword").getAsString() : null;
            int isActive = 1;
            Long extAssetId = Util.isSet(item.get("croppedImageId")) ? item.get("croppedImageId").getAsLong() : null;

            AssetDetail assetDetail = new AssetDetail(entityName, entityValue, keyValue, assetType, extAssetId, assetValue, basis, setId, size, score, isActive);
            assetDetailsList.add(assetDetail);
        }
        return assetDetailsList;
    }

    public static JsonObject mapImageAssetData(JsonArray jsonArray, long adminId) throws Exception {
        List<AssetDetail> modifiedAssetDetailList = getAssetDetailListForOzilImages(jsonArray);
        AutoAssetDAL.upsertAutoAssetData(modifiedAssetDetailList, AssetDetailService.ASSET_MASTER_BASIS, AssetDetailService.ASSET_MASTER_STATE, adminId);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("message", "Success");
        return jsonResponse;
    }

    public static JsonObject getTaskStateInfo(List<AATaskDetail> aaTaskDetailList) {
        int totalCount = 0;
        int successCount = 0;
        int errorCount = 0;

        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            ATRequestState state = aaTaskDetail.getState();
            if (ATRequestState.SUCCESS.equals(state)) {
                successCount++;
            } else {
                errorCount++;
            }
            totalCount++;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("total_count", totalCount);
        jsonObject.addProperty("success_count", successCount);
        jsonObject.addProperty("error_count", errorCount);
        return jsonObject;
    }

    public static JsonObject getTaskStateInfoForStableDiffusionImageDetail(List<StableDiffusionImageDetail> stableDiffusionMasterAssetList) {
        int totalCount = 0;
        int successCount = 0;
        int errorCount = 0;

        Map<Long, ATRequestState> taskStateMap = stableDiffusionMasterAssetList
                .stream()
                .collect(Collectors.toMap(
                        s -> s.getTaskId(),
                        s -> s.getTaskState(),
                        (x, y) -> x));

        for (Map.Entry<Long, ATRequestState> entry : taskStateMap.entrySet()) {
            ATRequestState state = entry.getValue();
            if (ATRequestState.SUCCESS.equals(state)) {
                successCount++;
            } else {
                errorCount++;
            }
            totalCount++;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("total_count", totalCount);
        jsonObject.addProperty("success_count", successCount);
        jsonObject.addProperty("error_count", errorCount);
        return jsonObject;
    }

    public static JsonObject generateTitleFromOzil(String demandKwd, String publisherPageTitle, String url, String prompt) throws Exception {
        prompt = "" ;
        if (Util.isStringSet(prompt) && !AssetDetailService.GENERATE_TITLE_DEFAULT_PROMPT.equals(prompt)) {
            prompt = prompt + "{{DEMAND_DESCRIPTION}} " +
                    "{{PAGE_TITLE}} " +
                    "{{PAGE_DESCRIPTION}} " +
                    "{{ADDITIONAL_PAGE_INFO}} " +
                    "Titles should be simple and effective. Limit ad title length to {{CHARACTER_LIMIT}} characters.";
        }
        JsonObject jsonResponse = OzilApiService.getOzilTitles(demandKwd, publisherPageTitle, url, prompt);
        return jsonResponse;
    }

    public static JsonObject queueTitlesGeneration(List<TitleGenerationTaskDetail> titleGenerationTaskDetailList, Map<String, Long> promptToIdMap) throws Exception {
        Long adminId = RequestGlobal.getAdmin().getAdminId();
        List<AATaskDetail> aaTaskDetailList = getAATaskDetailsForTitleGeneration(titleGenerationTaskDetailList, promptToIdMap);
        long requestId = AutoTemplateDAL.insertAATaskDetails(aaTaskDetailList, ATRequestType.TITLE_GENERATION, ATRequestState.UNPROCESSED, adminId);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("request_id", requestId);
        return jsonResponse;
    }

    public static List<AATaskDetail> getAATaskDetailsForTitleGeneration(List<TitleGenerationTaskDetail> titleGenerationTaskDetailList, Map<String, Long> promptToIdMap) {
        List<AATaskDetail> aaTaskDetailList = new ArrayList<>();

        for (TitleGenerationTaskDetail titleGenerationTaskDetail : titleGenerationTaskDetailList) {
            String demandKwds = titleGenerationTaskDetail.getDemandKeyword();
            String publisherPageTitle = titleGenerationTaskDetail.getPublisherPageTitle();
            String url = titleGenerationTaskDetail.getPublisherPageUrl();
            String prompt = titleGenerationTaskDetail.getPrompt();
            for (String demandKwd : demandKwds.split(",")) {
                TitleGenerationTaskDetail titleGenerationTaskDetailObject = new TitleGenerationTaskDetail(demandKwd, publisherPageTitle, url, promptToIdMap.get(prompt));
                AATaskDetail aaTaskDetail = new AATaskDetail(new Gson().toJson(titleGenerationTaskDetailObject));
                aaTaskDetailList.add(aaTaskDetail);
            }
        }
        return aaTaskDetailList;
    }

    public static Map<String, Long> getPromptToIdMap(List<String> promptList) throws DatabaseException {
        Map<String, Long> promptToIdMap = new HashMap<>();
        List<PromptDetail> promptDetailList = AutoTemplateDAL.insertPromptDetails(promptList);
        for (PromptDetail promptDetail : promptDetailList) {
            promptToIdMap.put(promptDetail.getPrompt(), promptDetail.getId());
        }
        return promptToIdMap;
    }

    public static Map<String, Long> getPromptToIdMapFromTitleGenerationTaskDetails(List<TitleGenerationTaskDetail> titleGenerationTaskDetailList) throws DatabaseException {
        List<String> promptList = getPromptListFromTitleGenerationTaskDetails(titleGenerationTaskDetailList);
        Map<String, Long> promptToIdMap = getPromptToIdMap(promptList);
        return promptToIdMap;
    }


    public static List<String> getPromptListFromTitleGenerationTaskDetails(List<TitleGenerationTaskDetail> titleGenerationTaskDetailList) {
        Set<String> promptSet = new HashSet<>();

        for (TitleGenerationTaskDetail titleGenerationTaskDetail : titleGenerationTaskDetailList) {
            String prompt = titleGenerationTaskDetail.getPrompt();
            if (Util.isStringSet(prompt) && !GENERATE_TITLE_DEFAULT_PROMPT.equals(prompt)) {
                promptSet.add(prompt);
            }
        }
        return new ArrayList<>(promptSet);
    }


    public static Map<Long, String> getIdToPromptMapForTitleGenerationTaskDetails(List<AATaskDetail> aaTaskDetailList) throws DatabaseException {
        Set<Long> promptIdSet = new HashSet<>();

        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            TitleGenerationTaskDetail titleGenerationTaskDetail = new Gson().fromJson(aaTaskDetail.getTaskInputDetails(), TitleGenerationTaskDetail.class);
            Long promptId = titleGenerationTaskDetail.getPromptId();
            if (Util.isSet(promptId)) {
                promptIdSet.add(promptId);
            }
        }

        return getIdToPromptMap(new ArrayList<>(promptIdSet));
    }

    public static Map<Long, String> getIdToPromptMap(List<Long> promptIdList) throws DatabaseException {
        Map<Long, String> idToPromptMap = new HashMap<>();
        List<PromptDetail> promptDetailList = AutoTemplateDAL.getPromptDetails(promptIdList);
        for (PromptDetail promptDetail : promptDetailList) {
            idToPromptMap.put(promptDetail.getId(), promptDetail.getPrompt());
        }

        return idToPromptMap;
    }


    public static JsonArray getGeneratedTitleDetailsFromAATaskDetailList(List<AATaskDetail> aaTaskDetailList) throws DatabaseException {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        JsonArray jsonArray = new JsonArray();
        Map<Long, String> promptIdToPromptMap = AssetDetailService.getIdToPromptMapForTitleGenerationTaskDetails(aaTaskDetailList);
        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            String jsonString = aaTaskDetail.getTaskOutputDetails();
            JsonObject taskOutputDetailsJsonObject = gson.fromJson(jsonString, JsonObject.class);
            String titleListString = taskOutputDetailsJsonObject.get("title_list").getAsString();
            List<String> titleList = gson.fromJson(titleListString, listType);
            TitleGenerationTaskDetail titleGenerationTaskDetail = gson.fromJson(aaTaskDetail.getTaskInputDetails(), TitleGenerationTaskDetail.class);
            for (String title : titleList) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("asset_value", title);
                jsonObject.addProperty("demand_keyword", titleGenerationTaskDetail.getDemandKeyword());
                jsonObject.addProperty("publisher_page_title", titleGenerationTaskDetail.getPublisherPageTitle());
                jsonObject.addProperty("publisher_page_url", titleGenerationTaskDetail.getPublisherPageUrl());
                jsonObject.addProperty("prompt", promptIdToPromptMap.get(titleGenerationTaskDetail.getPromptId()));
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

}


