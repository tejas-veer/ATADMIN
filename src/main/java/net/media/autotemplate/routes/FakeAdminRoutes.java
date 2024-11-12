package net.media.autotemplate.routes;

import net.media.autotemplate.bean.FakeAdminService;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.services.AclService;
import spark.Filter;
import spark.RouteGroup;

import static spark.Spark.*;

/*
    Created by shubham-ar
    on 8/11/17 4:45 PM   
*/
public class FakeAdminRoutes implements RouteGroup {
    @Override
    public void addRoutes() {
        before((Filter) (request, response) -> {
            if (!AclService.isSuperAdmin(AdminFactory.getAdmin(request)))
                halt(403, "You are not welcome here");
        });

        get("/true", (request, response) -> {
            String email = request.queryParams("email");
            FakeAdminService.activate(request, email);
            return "Admin Set to " + FakeAdminService.transformIfFaked(AdminFactory.getAdmin(request));
        });

        get("/false", (request, response) -> {
            FakeAdminService.deactivate(request);
            return "Admin is now unset";
        });
    }
}
