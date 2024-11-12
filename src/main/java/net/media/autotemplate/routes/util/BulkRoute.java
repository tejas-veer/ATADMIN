package net.media.autotemplate.routes.util;

import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.services.TemplateBulkService;
import spark.Request;
import spark.Response;

public abstract class BulkRoute extends AbstractRoute {
    protected abstract ApiResponse doTask(Request req, Response resp, TemplateBulkService templateBulkService) throws Exception;

    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        TemplateBulkService templateBulkService = new TemplateBulkService();
        return doTask(req, resp , templateBulkService);
    }
}
