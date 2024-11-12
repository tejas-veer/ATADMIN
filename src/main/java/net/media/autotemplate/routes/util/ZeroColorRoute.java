package net.media.autotemplate.routes.util;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.services.ZeroColorGenerationService;
import spark.Request;
import spark.Response;

/*
    Created by shubham-ar
    on 4/4/18 10:30 PM   
*/
public abstract class ZeroColorRoute extends AbstractRoute {
    public abstract ApiResponse makeResponse(Request req, ZeroColorGenerationService zeroColorGenerationService) throws Exception;

    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        Admin admin = AdminFactory.getAdmin(req);
        String entity = req.params(":entityId");
        CreativeConstants.Level level = CreativeConstants.Level.valueOf(req.params(":level").toUpperCase());
        BusinessUnit businessUnit = BusinessUnit.getBUFromName(getBUSelected());
        ZeroColorGenerationService zeroColorGenerationService = new ZeroColorGenerationService(entity, level, admin, businessUnit);
        return makeResponse(req, zeroColorGenerationService);
    }
}
