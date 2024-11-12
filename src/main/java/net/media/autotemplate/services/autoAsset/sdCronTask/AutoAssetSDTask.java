package net.media.autotemplate.services.autoAsset.sdCronTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.constants.AutoAssetConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.services.AssetDetailService;
import net.media.autotemplate.services.autoAsset.StableDiffusionService;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoAssetSDTask implements Runnable {
    private static final Logger LOG = LogManager.getLogger(AutoAssetSDTask.class);

    private final ThreadPoolExecutor es;
    private final ATRequestState taskState;
    private final int taskCount;


    public AutoAssetSDTask(int numThreads, ATRequestState taskState) {
        this.es = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        this.taskState = taskState;
        this.taskCount = numThreads;
    }

    @Override
    public void run() {
        try {
            int limit = this.taskCount;
            List<AATaskDetail> pendingAATaskDetailList = AutoTemplateDAL.getAATaskDetails(limit, ATRequestType.STABLE_DIFFUSION, this.taskState);
            LOG.info("TASK COUNT :: " + this.taskState + " :: Size :: " + pendingAATaskDetailList.size());
            Map<Long, String> IdToPromptMap = StableDiffusionService.getIdToPromptMapForSDDTaskDetails(pendingAATaskDetailList);
            if (!pendingAATaskDetailList.isEmpty()) {
                Map<Future<AASDTaskResult>, AATaskDetail> futureToTaskMap = new LinkedHashMap<>();

                for (AATaskDetail aaTaskDetail : pendingAATaskDetailList) {
                    StableDiffusionTask stableDiffusionTask = new StableDiffusionTask(aaTaskDetail, IdToPromptMap);
                    Future<AASDTaskResult> future = this.es.submit(stableDiffusionTask);
                    futureToTaskMap.put(future, aaTaskDetail);
                }

                List<AASDTaskResult> AASDTaskResultList = new ArrayList<>();
                for (Future<AASDTaskResult> listFuture : futureToTaskMap.keySet()) {
                    try {
                        AASDTaskResultList.add(listFuture.get());
                    } catch (Exception e) {
                        listFuture.cancel(true);
                        AATaskDetail failedTask = futureToTaskMap.get(listFuture);

                        if (Util.isSet(failedTask)) {
                            String reason = e.getMessage().substring(0, Math.min(1000, e.getMessage().length()));
                            failedTask = updateAATaskDetail(failedTask, reason);
                        }
                        LoggingService.log(LOG, Level.error, "Unable to process SD task id " + failedTask.getTaskId() + ":: Status :: " + this.taskState, e);
                    }
                }

                List<AATaskDetail> aaProcessedTaskList = getUpdateAATaskDetailWithPromptIds(AASDTaskResultList);
                LOG.info("aaProcessedTaskList COUNT :: " + this.taskState + " :: Size :: " + aaProcessedTaskList.size());
                AutoTemplateDAL.updateAATaskDetails(aaProcessedTaskList);
                LOG.info("aaProcessedTaskList COUNT :: " + this.taskState + " :: UPDATE COMPLETE :: " + aaProcessedTaskList.size());
            }
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "Unable To Process Task For State :: " + this.taskState);
        }
    }

    private List<AATaskDetail> getUpdateAATaskDetailWithPromptIds(List<AASDTaskResult> sdTaskResults) throws DatabaseException {
        Gson gson = new Gson();

        List<String> allPrompts = new ArrayList<>(sdTaskResults.stream()
                .flatMap(result -> Stream.of(result.getPrompt(), result.getNegativePrompt()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        Map<String, Long> promptToIdMap = AssetDetailService.getPromptToIdMap(allPrompts);

        for (AASDTaskResult taskResult : sdTaskResults) {
            AATaskDetail taskDetail = taskResult.getAATaskDetail();
            JsonObject taskInputDetailsJson = gson.fromJson(taskDetail.getTaskInputDetails(), JsonObject.class);
            updatePropertyIfAbsent(taskInputDetailsJson, "pmt_id", promptToIdMap.get(taskResult.getPrompt()));
            updatePropertyIfAbsent(taskInputDetailsJson, "npmt_id", promptToIdMap.get(taskResult.getNegativePrompt()));
            taskDetail.setTaskInputDetails(gson.toJson(taskInputDetailsJson));
        }

        return sdTaskResults.stream().map(AASDTaskResult::getAATaskDetail).collect(Collectors.toList());
    }

    private void updatePropertyIfAbsent(JsonObject jsonObject, String propertyName, Long value) {
        if (!jsonObject.has(propertyName)) {
            jsonObject.addProperty(propertyName, value);
        }
    }

    private AATaskDetail updateAATaskDetail(AATaskDetail failedTask, String reason) {
        ATRequestState state = failedTask.getState();
        if (ATRequestState.UNPROCESSED.equals(this.taskState)) {
            state = ATRequestState.ERROR;
        }

        int isActive = getDecrementedIsActive(failedTask.getIsActive());
        if (isRetriesExhausted(isActive)) {
            isActive = 0;
            state = ATRequestState.ERROR;
        }

        failedTask.setIsActive(isActive);
        failedTask.setFailureReason(reason);
        failedTask.setState(state);
        return failedTask;
    }

    public static int getRequestTaskDetailIsActive(ATRequestState state) {
        switch (state) {
            case PROCESSING:
                return 1;
            default:
                return 0;
        }
    }

    public static boolean isRetriesExhausted(int isActive) {
        return isActive <= (-1 * AutoAssetConstants.SD_MAX_RETRY);
    }

    public static int getDecrementedIsActive(int isActive) {
        return isActive == 1 ? -1 : isActive - 1;
    }
}



