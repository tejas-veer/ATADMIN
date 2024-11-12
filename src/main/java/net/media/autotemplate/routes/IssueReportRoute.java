package net.media.autotemplate.routes;

import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.routes.util.AbstractRoute;
import net.media.autotemplate.services.IssueReportService;
import spark.Request;
import spark.Response;

/*
    Created by shubham-ar
    on 26/6/18 3:19 PM   
*/
public class IssueReportRoute extends AbstractRoute {

    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        JsonObject responseObject = new JsonObject();
        String message = req.body();
        Admin admin = AdminFactory.getAdmin(req);
        message += "\nReported By : " + admin.getAdminName();
        boolean status = IssueReportService.reportIssue(message);
        responseObject.addProperty("postStatus",status);
        return status ? new ApiResponse(responseObject) : new ApiResponse(500, "Internal Error");
    }
    
}
