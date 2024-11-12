package net.media.autotemplate.routes.util;

import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.factory.ServiceFactory;
import net.media.autotemplate.services.mapping.MappingService;
import spark.Request;
import spark.Response;

/*
    Created by shubham-ar
    on 27/3/18 2:57 PM   
*/
public abstract class MappingRoute extends AbstractATRoute {
    protected abstract ApiResponse doTask(Request req, Response resp, MappingService mappingService) throws Exception;

    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        CreativeConstants.MappingType mappingType = CreativeConstants.MappingType.valueOf(req.params(":mappingType").toUpperCase());
        MappingService mappingService = ServiceFactory.getMappingService(getSupplyDemandHierarchy(), mappingType, getBUSelected());
        return doTask(req, resp, mappingService);
    }
}
