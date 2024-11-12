package net.media.autotemplate.services.autoAsset;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.TitleGenerationTaskDetail;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.services.AssetDetailService;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class AATitleGenerationTask implements Runnable {

    private static final Logger LOG = LogManager.getLogger(AATitleGenerationTask.class);

    ThreadPoolExecutor es;
    int taskCount;

    public AATitleGenerationTask(int numThreads) {
        this.es = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
        this.taskCount = numThreads;
    }


    @Override
    public void run() {
        try {
            int limit = taskCount;
            ATRequestType requestType = ATRequestType.TITLE_GENERATION;
            ATRequestState taskState = ATRequestState.UNPROCESSED;
            List<AATaskDetail> aaTaskDetailList = getAATaskDetailList(limit, requestType, taskState);
            if(!aaTaskDetailList.isEmpty()) {
                Map<Long, String> idToPromptMap = AssetDetailService.getIdToPromptMapForTitleGenerationTaskDetails(aaTaskDetailList);
                List<Future<AATaskDetail>> futureList = new ArrayList<>();
                for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
                    Future future = es.submit(new TitleGenerationTask(aaTaskDetail, idToPromptMap));
                    futureList.add(future);
                }

                List<AATaskDetail> processedAATaskDetailSet = new ArrayList<>();
                for (Future<AATaskDetail> future : futureList) {
                    try {
                        processedAATaskDetailSet.add(future.get());
                    } catch (Exception e) {
                        LoggingService.log(LOG, Level.error, "Unable to process Asset Mapping task", e);
                    }
                }
                AutoTemplateDAL.updateAATaskDetails(processedAATaskDetailSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<AATaskDetail> getAATaskDetailList(int limit, ATRequestType requestType, ATRequestState taskState) throws DatabaseException {
        List<AATaskDetail> aaTaskDetailList = AutoTemplateDAL.getAATaskDetails(limit, requestType, taskState);
        return aaTaskDetailList;
    }

}

class TitleGenerationTask implements Callable<AATaskDetail> {

    AATaskDetail aaTaskDetail;
    Map<Long, String> idToPromptMap;

    public TitleGenerationTask(AATaskDetail aaTaskDetail, Map<Long, String> idToPromptMap) {
        this.aaTaskDetail = aaTaskDetail;
        this.idToPromptMap = idToPromptMap;
    }

    @Override
    public AATaskDetail call() throws Exception {
        JsonObject taskOutputDetails = new JsonObject();
        ATRequestState state = ATRequestState.ERROR;
        try {
            Gson gson = new Gson();
            TitleGenerationTaskDetail titleGenerationTaskDetail = gson.fromJson(aaTaskDetail.getTaskInputDetails(), TitleGenerationTaskDetail.class);
            JsonObject jsonResponse = null;
            int retryCount = 0;
            while (retryCount < 3) {
                jsonResponse = generateTitle(titleGenerationTaskDetail, idToPromptMap);
                if (jsonResponse.getAsJsonArray("ad_titles").size() > 0) {
                    JsonArray adTitlesArray = jsonResponse.getAsJsonArray("ad_titles");
                    if (adTitlesArray.size() > 0) {
                        state = ATRequestState.SUCCESS;
                        String titleListString = gson.toJson(adTitlesArray);
                        taskOutputDetails.addProperty("title_list", titleListString);
                        aaTaskDetail.setTaskOutputDetails(gson.toJson(taskOutputDetails));
                        break;
                    }
                }
                retryCount++;
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        aaTaskDetail.setState(state);
        aaTaskDetail.setIsActive(0);
        return aaTaskDetail;
    }

    public JsonObject generateTitle(TitleGenerationTaskDetail titleGenerationTaskDetail, Map<Long, String> idToPromptMap) throws Exception {
        String demandKwd = titleGenerationTaskDetail.getDemandKeyword();
        String publisherPageTitle = titleGenerationTaskDetail.getPublisherPageTitle();
        String url = titleGenerationTaskDetail.getPublisherPageUrl();
        Long promptId = titleGenerationTaskDetail.getPromptId();
        String prompt = null;
        if (Util.isSet(promptId) && idToPromptMap.containsKey(promptId)) {
            prompt = idToPromptMap.get(promptId);
        }
        JsonObject jsonResponse = AssetDetailService.generateTitleFromOzil(demandKwd, publisherPageTitle, url, prompt);
        return jsonResponse;
    }

}










