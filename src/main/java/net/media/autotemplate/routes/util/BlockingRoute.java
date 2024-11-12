package net.media.autotemplate.routes.util;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.services.ATBlockingService;
import net.media.autotemplate.util.Util;
import spark.Request;
import spark.Response;

/*
    Created by shubham-ar
    on 14/2/18 6:56 PM   
*/
public abstract class BlockingRoute extends AbstractATRoute {
    protected abstract ApiResponse makeResponse(Request req, ATBlockingService atMappingService) throws Exception;

    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        String typeVal = req.params(":type");
        CreativeConstants.Type type = Util.isStringSet(typeVal) ? CreativeConstants.Type.valueOf(typeVal) : CreativeConstants.Type.ALL;
        ATBlockingService atMappingService = new ATBlockingService(getSupplyDemandHierarchy(), type, getBUSelected());
        return makeResponse(req, atMappingService);
    }
}
