package net.media.autotemplate.services.autoAsset;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.media.autotemplate.bean.AssetDetail;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.AssetMappingTaskDetail;
import net.media.autotemplate.constants.AutoAssetConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.AutoAssetDAL;
import net.media.autotemplate.enums.AAImageSource;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.services.AssetDetailService;
import net.media.autotemplate.services.OzilApiService;
import net.media.autotemplate.services.autoAsset.sdCronTask.AutoAssetSDTask;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static net.media.autotemplate.services.OzilApiService.OZIL_IMAGE_UPLOAD_MAX_RETRY;
import static net.media.autotemplate.services.OzilApiService.OZIL_URL_UPLOAD_BATCH_SIZE;

public class AutoAssetMappingTask implements Runnable {
    private static final Logger LOG = LogManager.getLogger(AutoAssetSDTask.class);
    private final ThreadPoolExecutor es;

    public AutoAssetMappingTask(int numThreads) {
        this.es = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    }


    @Override
    public void run() {
        try {
            int limit = 3 * OZIL_URL_UPLOAD_BATCH_SIZE;
            ATRequestType requestType = ATRequestType.ASSET_MAPPING;
            ATRequestState taskState = ATRequestState.UNPROCESSED;
            List<AATaskDetail> aaTaskDetailList = getAssetDetails(limit, requestType, taskState);
            if (aaTaskDetailList.size() != 0) {
                Map<Integer, List<String>> setIdToImageUrlMap = getSetIdToImageUrlMap(aaTaskDetailList);
                List<Future<Map<Integer, Map<String, String>>>> futures = new ArrayList<>();
                for (Map.Entry<Integer, List<String>> entry : setIdToImageUrlMap.entrySet()) {
                    Integer setId = entry.getKey();
                    List<String> imageUrls = entry.getValue();
                    List<List<String>> imageUrlsPartitions = Lists.partition(imageUrls, OZIL_URL_UPLOAD_BATCH_SIZE);
                    for (List<String> imageUrlList : imageUrlsPartitions) {
                        HostedAssetUploadTask hostedAssetUploadTask = new HostedAssetUploadTask(setId, imageUrlList);
                        Future<Map<Integer, Map<String, String>>> future = this.es.submit(hostedAssetUploadTask);
                        futures.add(future);
                    }
                }

                Map<Integer, Map<String, String>> combinedResultMap = new HashMap<>();

                for (Future<Map<Integer, Map<String, String>>> future : futures) {
                    try {
                        Map<Integer, Map<String, String>> resultMap = future.get();

                        for (Integer setId : resultMap.keySet()) {
                            Map<String, String> hostedToUploadedImageUrlMap = combinedResultMap.getOrDefault(setId, new HashMap<>());
                            hostedToUploadedImageUrlMap.putAll(resultMap.get(setId));
                            combinedResultMap.put(setId, hostedToUploadedImageUrlMap);
                        }
                    } catch (Exception e) {
                        LoggingService.log(LOG, Level.error, "Unable to process Asset Mapping task", e);
                    }
                }

                List<AATaskDetail> aaUpdatedTaskDetailList = getUpdatedAATaskDetails(aaTaskDetailList, combinedResultMap);
                Map<Long, List<AssetDetail>> adminIdToAssetDetailMap = getAdminIdToAssetDetailsMap(aaUpdatedTaskDetailList, combinedResultMap);
                processAdminIdToAssetDetailsMap(adminIdToAssetDetailMap);
                AutoTemplateDAL.updateAATaskDetails(aaUpdatedTaskDetailList);
            }

        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "Unable to process Asset Mapping task", e);
        }
    }

    public static List<AATaskDetail> getAssetDetails(int limit, ATRequestType requestType, ATRequestState taskState) throws DatabaseException {
        return AutoTemplateDAL.getAATaskDetails(limit, requestType, taskState);
    }

    private static Map<Integer, List<String>> getSetIdToImageUrlMap(List<AATaskDetail> aaTaskDetailList) {
        Map<Integer, List<String>> setIdToImageUrlsMap = new HashMap<>();
        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            AssetMappingTaskDetail assetMappingTaskDetail = getAssetMappingTaskDetailFromTaskInputDetails(aaTaskDetail.getTaskInputDetails());
            String assetValue = assetMappingTaskDetail.getAssetValue();
            int setId = assetMappingTaskDetail.getSetId();
            setIdToImageUrlsMap.computeIfAbsent(setId, k -> new ArrayList<>()).add(assetValue);
        }
        return setIdToImageUrlsMap;
    }

    public static List<AATaskDetail> getUpdatedAATaskDetails(List<AATaskDetail> aaTaskDetailList, Map<Integer, Map<String, String>> combinedResultMap) {
        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            AssetMappingTaskDetail assetMappingTaskDetail = getAssetMappingTaskDetailFromTaskInputDetails(aaTaskDetail.getTaskInputDetails());
            String assetValue = assetMappingTaskDetail.getAssetValue();
            int setId = assetMappingTaskDetail.getSetId();
            Map<String, String> hostedImageToUploadedImageMap = combinedResultMap.get(setId);
            if (Util.isSet(hostedImageToUploadedImageMap) && Util.isStringSet(hostedImageToUploadedImageMap.get(assetValue))) {
                aaTaskDetail.setState(ATRequestState.SUCCESS);
            } else {
                aaTaskDetail.setState(ATRequestState.ERROR);
            }
            aaTaskDetail.setIsActive(0);
        }
        return aaTaskDetailList;
    }

    public static AssetMappingTaskDetail getAssetMappingTaskDetailFromTaskInputDetails(String taskInputDetails) {
        return new Gson().fromJson(taskInputDetails, AssetMappingTaskDetail.class);
    }

    public static Map<Long, List<AssetDetail>> getAdminIdToAssetDetailsMap(List<AATaskDetail> aaTaskDetailList, Map<Integer, Map<String, String>> setIdHostedImageToUploadedMap) {
        Map<Long, List<AssetDetail>> adminIdToAssetDetailsMap = new HashMap<>();
        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            AssetMappingTaskDetail assetMappingTaskDetail = getAssetMappingTaskDetailFromTaskInputDetails(aaTaskDetail.getTaskInputDetails());
            String hostedAssetValue = assetMappingTaskDetail.getAssetValue();
            int setId = assetMappingTaskDetail.getSetId();
            Map<String, String> hostedImageToUploadedImageMap = setIdHostedImageToUploadedMap.get(setId);
            if (ATRequestState.SUCCESS.equals(aaTaskDetail.getState())) {
                String assetValue = hostedImageToUploadedImageMap.get(hostedAssetValue);
                String assetType = AutoAssetConstants.ASSET_TYPE_IMAGE;
                float score = 0;
                String entityName = assetMappingTaskDetail.getEntityName();
                String entityValue = assetMappingTaskDetail.getEntityValue();
                String keyValue = (assetType + AutoAssetConstants.KEY_VALUE_SEPARATOR + entityName + AutoAssetConstants.KEY_VALUE_SEPARATOR + entityValue).toLowerCase();
                String[] entityValueSplits = entityValue.split(AutoAssetConstants.ENTITY_VALUE_WITH_SIZE_SEPARATOR);
                String size = entityValueSplits.length > 1 ? entityValueSplits[1] : null;
                String basis = "";
                int isActive = 1;
                Long extAssetId = null;
                AssetDetail assetDetail = new AssetDetail(entityName, entityValue, keyValue, assetType, extAssetId, assetValue, basis, setId, size, score, isActive);
                adminIdToAssetDetailsMap.computeIfAbsent(aaTaskDetail.getAdminId(), k -> new ArrayList<>()).add(assetDetail);
            }
        }
        return adminIdToAssetDetailsMap;
    }

    public void processAdminIdToAssetDetailsMap(Map<Long, List<AssetDetail>> adminIdToAssetDetailsMap) throws Exception {
        for (Map.Entry<Long, List<AssetDetail>> entry : adminIdToAssetDetailsMap.entrySet()) {
            Long adminId = entry.getKey();
            List<AssetDetail> assetDetailList = entry.getValue();
            AutoAssetDAL.upsertAutoAssetData(assetDetailList, AssetDetailService.ASSET_MASTER_BASIS, AssetDetailService.ASSET_MASTER_STATE, adminId);
        }
    }

}

class HostedAssetUploadTask implements Callable<Map<Integer, Map<String, String>>> {
    private static final Logger LOG = LogManager.getLogger(HostedAssetUploadTask.class);

    int setId;
    List<String> imageUrlList;

    public HostedAssetUploadTask(Integer setId, List<String> imageUrlList) {
        this.setId = setId;
        this.imageUrlList = imageUrlList;
    }

    @Override
    public Map<Integer, Map<String, String>> call() {
        Map<Integer, Map<String, String>> setIdToOriginalToUploadedUrlMap = new HashMap<>();
        int retryCount = 0;

        while (retryCount < OZIL_IMAGE_UPLOAD_MAX_RETRY) {
            try {
                Map<String, String> originalToUploadedUrlMap = getOzilImageUrlForHostedImages(setId, imageUrlList);
                if (originalToUploadedUrlMap.isEmpty()) {
                    retryCount++;
                } else {
                    setIdToOriginalToUploadedUrlMap.put(setId, originalToUploadedUrlMap);
                    break;
                }
            } catch (Exception e) {
                retryCount++;
                LoggingService.log(LOG, Level.error, "Unable to process image upload task", e);
            }
        }
        return setIdToOriginalToUploadedUrlMap;
    }

    public static Map<String, String> getOzilImageUrlForHostedImages(int setId, List<String> imageUrls) throws Exception {
        String dirPath = AAImageSource.getImageSourceDirPathFromSetId(setId);
        return OzilApiService.getOzilImageUrlForHostedImages(imageUrls, dirPath);
    }
}
