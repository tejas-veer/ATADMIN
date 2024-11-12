package net.media.autotemplate.servlets;

import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.services.TemplateBulkCronTask;
import net.media.autotemplate.services.autoAsset.AATitleGenerationTask;
import net.media.autotemplate.services.autoAsset.AutoAssetMappingTask;
import net.media.autotemplate.services.autoAsset.sdCronTask.AutoAssetSDTask;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jatin Warade
 * on 10-March-2023
 * at 01:12
 */
public class InitServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(InitServlet.class);
    private static final ScheduledExecutorService CRON_TASK_EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService AUTO_ASSET_UNPROCESSED_EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService AUTO_ASSET_PENDING_EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService AUTO_ASSET_ERROR_EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService AUTO_ASSET_REQUEST_EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService AUTO_ASSET_MAPPER_EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService AUTO_ASSET_TITLE_GENERATOR_EXECUTOR = Executors.newScheduledThreadPool(1);

    @Override
    public void init() throws ServletException {
        if (ConfigConstants.IS_CRON) {
            try {
                CRON_TASK_EXECUTOR.scheduleWithFixedDelay(new TemplateBulkCronTask(), 0, 30, TimeUnit.SECONDS);
                AUTO_ASSET_ERROR_EXECUTOR.scheduleWithFixedDelay(new AutoAssetSDTask(1, ATRequestState.ERROR), 0, 1, TimeUnit.SECONDS);
                AUTO_ASSET_PENDING_EXECUTOR.scheduleWithFixedDelay(new AutoAssetSDTask(1, ATRequestState.PROCESSING), 0, 1, TimeUnit.SECONDS);
                AUTO_ASSET_UNPROCESSED_EXECUTOR.scheduleWithFixedDelay(new AutoAssetSDTask(1, ATRequestState.UNPROCESSED), 0, 1, TimeUnit.SECONDS);
                AUTO_ASSET_MAPPER_EXECUTOR.scheduleWithFixedDelay(new AutoAssetMappingTask(3), 0, 1, TimeUnit.SECONDS);
                AUTO_ASSET_TITLE_GENERATOR_EXECUTOR.scheduleWithFixedDelay(new AATitleGenerationTask(3), 0, 5, TimeUnit.SECONDS);
                AUTO_ASSET_REQUEST_EXECUTOR.scheduleWithFixedDelay(this::updateAARequest, 0, 10, TimeUnit.SECONDS);
            } catch (Exception e) {
                LoggingService.log(LOGGER, Level.error, "InitServlet::init()", e);
                throw new ServletException();
            }
        }
    }

    public void updateAARequest() {
        try {
            AutoTemplateDAL.updateATRequestMaster();
        } catch (Exception e) {
            LoggingService.log(LOGGER, Level.error, "InitServlet::updateRequestMaster()", e);
        }
    }
}
