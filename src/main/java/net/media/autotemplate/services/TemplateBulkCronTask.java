package net.media.autotemplate.services;

import com.google.common.collect.Lists;
import net.media.autotemplate.bean.ATRequestDetail;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class TemplateBulkCronTask implements Runnable {
    private static final Logger LOG = LogManager.getLogger(TemplateBulkCronTask.class);
    private static final ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    private static final int REQUEST_SIZE = 100;
    private static final int BATCH_SIZE = 20;

    @Override
    public void run() {
        try {
            List<ATRequestDetail> requestDetails = AutoTemplateDAL.getRequestDetails(REQUEST_SIZE);
            if (!Util.empty(requestDetails)) {
                Map<Long, List<ATRequestDetail>> requestToDetailsMap = requestDetails.stream().collect(Collectors.groupingBy(ATRequestDetail::getRequestId));
                List<Long> requests = requestToDetailsMap.keySet().stream().sorted().collect(Collectors.toList());
                for (Long request : requests) {
                    List<Future<?>> futures = new ArrayList<>();
                    List<List<ATRequestDetail>> batches = Lists.partition(requestToDetailsMap.get(request), BATCH_SIZE);
                    for (List<ATRequestDetail> batch : batches) {
                        futures.add(es.submit(new TemplateBulkTask(batch)));
                    }
                    for (Future<?> future : futures) {
                        try {
                            future.get();
                        } catch (Exception e) {
                            LoggingService.log(LOG, Level.error, "TemplateBulkCronTask :: processTemplateRequestDetails :: request id :: " + request, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "TemplateBulkCronTask :: processTemplateRequestDetails", e);
        }
    }
}


class TemplateBulkTask implements Runnable {
    private static final Logger LOG = LogManager.getLogger(TemplateBulkTask.class);
    List<ATRequestDetail> atRequestDetails;

    public TemplateBulkTask(List<ATRequestDetail> atRequestDetails) {
        this.atRequestDetails = atRequestDetails;
    }

    @Override
    public void run() {
        try {
            String requestType = atRequestDetails.get(0).getRequestType();
            ATRequestType.getATRequestTypeFromDbName(requestType).getAtRequestDetailConsumer().accept(atRequestDetails);
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "TemplateBulkTask :: processTemplateRequestDetails :: task id :: " + atRequestDetails.get(0).getTaskId(), e);
        }
    }
}
