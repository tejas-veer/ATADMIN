package net.media.autotemplate.bean;

import net.media.autotemplate.factory.AdminFactory;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

/*
    Created by shubham-ar
    on 24/11/17 7:35 PM   
*/

public class FakeAdminService {
    private static Map<Admin, Admin> adminMap = new HashMap<>();
    public static final Admin DEFAULT_ADMIN = new Admin("kushal.si-local@media.net",-16258, "kushal.si-local");

    public static boolean isActive(Admin admin) {
        return adminMap.containsKey(admin);
    }

    public static void activate(Request request, String emailId) throws Exception {
        Admin alias = AdminFactory.getAdminFromEmail(emailId), impersonator = AdminFactory.getAdmin(request);
        adminMap.put(impersonator, alias);
    }

    public static void deactivate(Request request) throws Exception {
        Admin impersonator = AdminFactory.getAdmin(request);
        adminMap.remove(impersonator);
    }

    public static Admin getAdmin(Admin admin) {
        return adminMap.get(admin);
    }

    public static Admin transformIfFaked(Admin admin) {
        if (isActive(admin))
            return getAdmin(admin);
        return admin;
    }
}
