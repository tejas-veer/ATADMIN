package net.media.autotemplate.services.autoAsset.sdCronTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.SDTaskDetail;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.services.OzilApiService;
import net.media.autotemplate.services.autoAsset.StableDiffusionService;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

class StableDiffusionTask implements Callable<AASDTaskResult> {
    private static final Logger LOG = LogManager.getLogger(StableDiffusionTask.class);
    private AATaskDetail aaTaskDetail;
    private Map<Long, String> idToPromptMap;

    public StableDiffusionTask(AATaskDetail aaTaskDetail, Map<Long, String> idToPromptMap) {
        this.aaTaskDetail = aaTaskDetail;
        this.idToPromptMap = idToPromptMap;
    }

    @Override
    public AASDTaskResult call() {
        Gson gson = new Gson();
        SDTaskDetail sdTaskDetail = gson.fromJson(aaTaskDetail.getTaskInputDetails(), SDTaskDetail.class);
        JsonObject sdTaskOutputDetail = new JsonObject();
        AASDTaskResult result = new AASDTaskResult();
        if (Util.isStringSet(aaTaskDetail.getTaskOutputDetails()) && !aaTaskDetail.getTaskOutputDetails().equals("null")) {
            sdTaskOutputDetail = gson.fromJson(aaTaskDetail.getTaskOutputDetails(), JsonObject.class);
        }

        ATRequestState state = ATRequestState.PROCESSING.equals(aaTaskDetail.getState()) ? ATRequestState.PROCESSING : ATRequestState.ERROR;
        int isActive = aaTaskDetail.getIsActive();
        String reason;
        JsonObject SDOzilResponse;

        try {
            SDOzilResponse = getSDOzilResponse(sdTaskDetail, sdTaskOutputDetail);
            String ozilErrorMsg = Util.isSet(SDOzilResponse.get("error_msg")) ? SDOzilResponse.get("error_msg").getAsString() : null;
            Integer ozilStatusCode = Util.isSet(SDOzilResponse.get("statuscode")) ? SDOzilResponse.get("statuscode").getAsInt() : null;

            if (Util.isStringSet(ozilErrorMsg) || Util.isSet(ozilStatusCode) && HttpStatus.SC_OK != ozilStatusCode) {
                String message = "ERROR_AT_OZIL_END :: " + (Util.isStringSet(ozilErrorMsg) ? ozilErrorMsg : "Status Code ::" + ozilStatusCode);
                throw new Exception(message);
            } else {
                state = ATRequestState.getATRequestStateFromDbName(SDOzilResponse.get("status").getAsString().toUpperCase());
                if(state.equals(ATRequestState.SUCCESS) || state.equals(ATRequestState.PROCESSING)){
                    JsonObject sdTaskInputDetails = gson.fromJson(aaTaskDetail.getTaskInputDetails(), JsonObject.class);

                    if(!sdTaskInputDetails.has("pmt_id") && SDOzilResponse.has("prompt")){
                        result.setPrompt(SDOzilResponse.get("prompt").getAsString());
                    }

                    if(!sdTaskInputDetails.has("npmt_id") && SDOzilResponse.has("negative_prompt")){
                        result.setNegativePrompt(SDOzilResponse.get("negative_prompt").getAsString());
                    }

                    if(!sdTaskOutputDetail.has("image_fetch_id")){
                        sdTaskOutputDetail.addProperty("image_fetch_id", SDOzilResponse.get("img_fetch_id").getAsLong());
                    }

                    if (ATRequestState.SUCCESS.equals(state)) {
                        List<String> imageUrls = StableDiffusionService.extractImageUrlsFromSDResponse(SDOzilResponse);
                        if(imageUrls.isEmpty()){
                            state = ATRequestState.ERROR;
                            throw new Exception("Image Urls List IS Empty");
                        }
                        sdTaskOutputDetail.addProperty("image_urls", gson.toJson(imageUrls));
                    }

                    reason = SDOzilResponse.has("message") ? SDOzilResponse.get("message").getAsString() : null;
                    isActive = AutoAssetSDTask.getRequestTaskDetailIsActive(state);
                }
                else{
                    state = ATRequestState.ERROR;
                    String message = SDOzilResponse.get("message").getAsString();
                    throw new Exception(message);
                }
            }
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "TaskId :: " + aaTaskDetail.getTaskId() + " || ERROR :: " + e.getMessage() );
            isActive = AutoAssetSDTask.getDecrementedIsActive(isActive);
            reason = e.getMessage().substring(0, Math.min(1000, e.getMessage().length()));
        }

        if (AutoAssetSDTask.isRetriesExhausted(isActive)) {
            isActive = 0;
            state = ATRequestState.ERROR;
        }

        String taskOutputDetail = gson.toJson(sdTaskOutputDetail);
        aaTaskDetail.setTaskOutputDetails(taskOutputDetail);
        aaTaskDetail.setState(state);
        aaTaskDetail.setFailureReason(reason);
        aaTaskDetail.setIsActive(isActive);
        result.setAaTaskDetail(aaTaskDetail);
        return result;
    }

    private JsonObject getSDOzilResponse(SDTaskDetail sdTaskDetail, JsonObject sdTaskOutputDetail) throws Exception {
        JsonObject stableDiffusionOzilJsonObject;
        if (ATRequestState.PROCESSING.equals(aaTaskDetail.getState())) {
            stableDiffusionOzilJsonObject = getOzilStableDiffusionImagesFromImageFetchId(sdTaskOutputDetail);
        } else {
            stableDiffusionOzilJsonObject = getOzilStableDiffusionImages(sdTaskDetail);
        }
        return stableDiffusionOzilJsonObject;
    }

    private JsonObject getOzilStableDiffusionImages(SDTaskDetail sdTaskDetail) throws IOException {
        Long negativePromptId = sdTaskDetail.getNegativePromptId();
        Long promptId = sdTaskDetail.getPromptId();

        return OzilApiService.getOzilStableDiffusionImages(
                new SDTaskDetail(sdTaskDetail.getKeyword(), this.idToPromptMap.get(promptId), idToPromptMap.get(negativePromptId), sdTaskDetail.getVersion())
        );
    }

    private JsonObject getOzilStableDiffusionImagesFromImageFetchId(JsonObject sdTaskOutputDetail) throws Exception {
        Long imageFetchId = sdTaskOutputDetail.has("image_fetch_id") ? sdTaskOutputDetail.get("image_fetch_id").getAsLong() : null;
        if(!Util.isSet(imageFetchId)){
            throw new Exception("Image Fetch Id Not Found");
        }
        return OzilApiService.getOzilStableDiffusionImagesForImageFetchId(imageFetchId);
    }
}
