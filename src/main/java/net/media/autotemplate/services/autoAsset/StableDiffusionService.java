package net.media.autotemplate.services.autoAsset;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.SDTaskDetail;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.services.AssetDetailService;
import net.media.autotemplate.util.Util;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import spark.Request;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Jatin Warade
 * on 19/01/24
 */
public class StableDiffusionService {
    private static final Logger LOG = LogManager.getLogger(StableDiffusionService.class);


    public static ApiResponse queueStableDiffusionRequest(Request request, long adminId) throws Exception {
        ATRequestState state = ATRequestState.UNPROCESSED;
        ATRequestType requestType = ATRequestType.STABLE_DIFFUSION;
        Gson gson = new Gson();
        JsonArray requestBodyJsonArray = gson.fromJson(request.body(), JsonArray.class);
        List<SDTaskDetail> sdTaskDetailList = getSdTaskDetailListFromRequest(requestBodyJsonArray);
        List<AATaskDetail> aaTaskDetailList = getAATaskDetailList(sdTaskDetailList);

        // TODO: 10/01/24 Split in partitions
        // change proc -> if requestId is given then don't generate requestId -> else generate equest_id and insert data
        // first call will be without requestId this will return request_id
        // all subsequent calls will give request_id as param and data will be inserted for this request_id
        // logic of rows_generated will also changed
        long requestId = AutoTemplateDAL.insertAATaskDetails(aaTaskDetailList, requestType, state, adminId);
        JsonObject response = new JsonObject();
        response.addProperty("request_id", requestId);
        return new ApiResponse(response);
    }

    @NotNull
    private static List<SDTaskDetail> getSdTaskDetailListFromRequest(JsonArray requestBodyJsonArray) {
        List<SDTaskDetail> sdTaskDetailList = new ArrayList<>();

        for(JsonElement jsonElement : requestBodyJsonArray){
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String kwd = Util.isStringSet(jsonObject.get("kwd").getAsString()) ? jsonObject.get("kwd").getAsString() : "";
            if (Util.isStringSet(kwd)){
                String[] keywords = kwd.split(",");
                for (String keyword : keywords) {
                    SDTaskDetail sdTaskDetail = new SDTaskDetail(keyword, jsonObject.get("pmt"), jsonObject.get("npmt"), jsonObject.get("ver"));
                    sdTaskDetailList.add(sdTaskDetail);
                }
            }else{
                SDTaskDetail sdTaskDetail = new SDTaskDetail(null, jsonObject.get("pmt"), jsonObject.get("npmt"), jsonObject.get("ver"));
                sdTaskDetailList.add(sdTaskDetail);
            }
        }
        return sdTaskDetailList;
    }

    private static List<AATaskDetail> getAATaskDetailList(List<SDTaskDetail> sdTaskDetailList) throws DatabaseException {
        List<AATaskDetail> aaTaskDetailList = new ArrayList<>();
        Map<String, Long> promptToIdMap = getPromptToIdMapFromSDDTaskDetails(sdTaskDetailList);
        for (SDTaskDetail sdTaskDetail : sdTaskDetailList) {
            Long promptId = promptToIdMap.get(sdTaskDetail.getPrompt());
            Long negativePromptId = promptToIdMap.get(sdTaskDetail.getNegativePrompt());
            sdTaskDetail.setPromptId(promptId);
            sdTaskDetail.setNegativePromptId(negativePromptId);
            AATaskDetail aaTaskDetail = new AATaskDetail(sdTaskDetail.getTaskInputDetailString());
            aaTaskDetailList.add(aaTaskDetail);
        }
        return aaTaskDetailList;
    }

    public static List<String> extractImageUrlsFromSDResponse(JsonObject SDOzilResponse) {
        List<String> imageUrlList = new ArrayList<>();
        JsonArray imageUrlJsonArray = SDOzilResponse.getAsJsonArray("imgs");
        for (JsonElement element : imageUrlJsonArray) {
            String imageUrl = element.getAsString();
            imageUrlList.add(imageUrl);
        }
        return imageUrlList;
    }

    public static Map<String, Long> getPromptToIdMapFromSDDTaskDetails(List<SDTaskDetail> sdTaskDetailList) throws DatabaseException {
        List<String> promptList = getPromptListFromSDTaskDetails(sdTaskDetailList);
        Map<String, Long> promptToIdMap = AssetDetailService.getPromptToIdMap(promptList);
        return promptToIdMap;
    }

    public static List<String> getPromptListFromSDTaskDetails(List<SDTaskDetail> SDTaslDetailList) {
        Set<String> promptSet = new HashSet<>();

        for (SDTaskDetail sdTaskDetail : SDTaslDetailList) {
            String prompt = sdTaskDetail.getPrompt();
            String negativePrompt = sdTaskDetail.getNegativePrompt();
            if (Util.isStringSet(prompt)) {
                promptSet.add(prompt);
            }
            if (Util.isStringSet(negativePrompt)) {
                promptSet.add(negativePrompt);
            }
        }
        return new ArrayList<>(promptSet);
    }

    public static Map<Long, String> getIdToPromptMapForSDDTaskDetails(List<AATaskDetail> aaTaskDetailList) throws DatabaseException {
        Set<Long> promptIdSet = new HashSet<>();

        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            SDTaskDetail sdTaskDetail = null;
            String taskInputDetailsJson = aaTaskDetail.getTaskInputDetails();
            if (Util.isStringSet(taskInputDetailsJson)) {
                sdTaskDetail = new Gson().fromJson(taskInputDetailsJson, SDTaskDetail.class);
                Long promptId = sdTaskDetail.getPromptId();
                Long negativePromptId = sdTaskDetail.getNegativePromptId();
                if (Util.isSet(promptId)) {
                    promptIdSet.add(promptId);
                }
                if (Util.isSet(negativePromptId)) {
                    promptIdSet.add(negativePromptId);
                }
            }
        }

        return AssetDetailService.getIdToPromptMap(new ArrayList<>(promptIdSet));
    }


    public static JsonArray getStableDiffusionImageListFromAATaskDetailList(List<AATaskDetail> aaTaskDetailList) throws DatabaseException {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        Map<Long, String> idToPromptMap = getIdToPromptMapForSDDTaskDetails(aaTaskDetailList);
        JsonArray stableDiffusionImageDetail = new JsonArray();
        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            String taskOutputDetails = aaTaskDetail.getTaskOutputDetails();
            String taskInputDetails = aaTaskDetail.getTaskInputDetails();
            SDTaskDetail sdTaskDetail = gson.fromJson(taskInputDetails, SDTaskDetail.class);

            if (Util.isStringSet(taskOutputDetails)) {
                JsonObject taskOutputDetailsObject = gson.fromJson(taskOutputDetails, JsonObject.class);
                String imageUrlsString = taskOutputDetailsObject.get("image_urls").getAsString();
                List<String> imageUrls = gson.fromJson(imageUrlsString, listType);

                for (String imageUrl : imageUrls) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("keyword", sdTaskDetail.getKeyword());
                    jsonObject.addProperty("prompt", idToPromptMap.get(sdTaskDetail.getPromptId()));
                    jsonObject.addProperty("negativePrompt", idToPromptMap.get(sdTaskDetail.getNegativePromptId()));
                    jsonObject.addProperty("version", sdTaskDetail.getVersion());
                    jsonObject.addProperty("imageUrl", imageUrl);
                    stableDiffusionImageDetail.add(jsonObject);
                }
            }
        }
        return stableDiffusionImageDetail;
    }
}
