package net.media.autotemplate.routes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.FakeAdminService;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.services.AclService;
import net.media.autotemplate.util.Util;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.RouteGroup;

import static spark.Spark.get;

/*
    Created by shubham-ar
    on 24/10/17 1:06 PM   
*/
public class SessionRoutes implements RouteGroup {
    public static final Gson GSON = Util.getGson();
    private static final Logger LOG = LogManager.getLogger(SessionRoutes.class);

    @Override
    public void addRoutes() {
        get("", (req, res) -> {
            try {
                Admin admin = AdminFactory.getAdmin(req);
                admin = FakeAdminService.transformIfFaked(admin);
                JsonObject adminJson = GSON.toJsonTree(admin, Admin.class).getAsJsonObject();
                adminJson.addProperty("super_admin", AclService.isSuperAdmin(admin));
                adminJson.addProperty("super_group", AclService.checkSuperGroupAccess(admin));
                return adminJson;
            } catch (Exception e) {
                return ExceptionUtils.getStackTrace(e);
            }
        });
    }
}
