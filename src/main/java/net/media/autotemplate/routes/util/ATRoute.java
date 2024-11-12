package net.media.autotemplate.routes.util;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.services.ConfigsService;
import spark.Request;
import spark.Response;

/*
    Created by shubham-ar
    on 14/3/18 4:27 PM   
*/
public abstract class ATRoute extends AbstractRoute {
    protected abstract ApiResponse getResponse(Request req, Response resp, ConfigsService tcs) throws Exception;

    @Override
    public ApiResponse getResponse(Request request, Response response) throws Exception {
        CreativeConstants.Level level = CreativeConstants.Level.valueOf(request.params(":level"));
        String entity = request.params(":entityId");
        Admin admin = AdminFactory.getAdmin(request);
        BusinessUnit businessUnit = BusinessUnit.getBUFromName(getBUSelected());
        return getResponse(request, response, new ConfigsService(entity, level, admin, businessUnit));
    }


}
