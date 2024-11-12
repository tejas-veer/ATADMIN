package net.media.autotemplate.factory;

import com.google.gson.reflect.TypeToken;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.FakeAdminService;
import net.media.autotemplate.dal.db.LoginDbUtil;
import net.media.autotemplate.enums.AuthenticationType;
import net.media.autotemplate.util.AuthenticationUtil;
import net.media.autotemplate.util.SysProperties;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/*
    Created by shubham-ar
    on 18/12/17 10:38 AM   
*/
public class AdminFactory {
    private static final Logger LOG = LogManager.getLogger(AdminFactory.class);

    public static Admin getAdminFromSession(HttpSession session) throws Exception {
        if (!Util.isSet(session))
            throw new AuthenticationException("Session not Set");
        String adminEmail = Util.getSessionAttributeAsString(session, "admin_email");
        String adminName = Util.getSessionAttributeAsString(session, "admin_name");
        Long adminId = Util.getSessionAttributeAsLong(session, "admin_id");
        if (!Util.isStringSet(adminName) || !Util.isSet(adminId))
            throw new AuthenticationException("Admin Not Set Try Refreshing the page");
        return new Admin(adminEmail, adminId, adminName);
    }

    public static Admin getAdminIdFromCookie(Cookie cookie) throws Exception {
        if (Util.isSet(cookie)) {
            String cfCookieToken = cookie.getValue();
            byte[] decoded = java.util.Base64.getDecoder().decode(cfCookieToken.split("\\.")[1]);
            Map<String, Object> inputMap = Util.getGson().fromJson(new String(decoded, StandardCharsets.UTF_8),
                    new TypeToken<Map<String, Object>>() {
                    }.getType());
            String adminEmail = (String) inputMap.get("email");
            String adminName = adminEmail.split("@")[0];
            List<String> emailList = Util.getEmailList(adminName, adminEmail);
            Admin admin = getAdminWithEmailAndId(emailList);
            if (!Util.isSet(admin))
                throw new AuthenticationException("Admin Not Set Try Refreshing the page");
            return new Admin(adminEmail, admin.getAdminId(), adminName);
        } else {
            throw new AuthenticationException("Not a valid admin");
        }
    }

    public static Admin getAdminFromEmail(String nameId) throws DatabaseException {
        long adminId = LoginDbUtil.getAdminId(nameId);
        return new Admin(adminId, nameId.split("@")[0]);
    }

    public static Admin getAdmin(Request request) throws Exception {
        return getAdmin(request.raw());
    }

    public static Admin getAdmin(HttpServletRequest request) throws Exception {
        Admin admin;
        if (SysProperties.getPropertyAsBoolean("LOCAL")) {
            LoggingService.log(LOG, Level.info, "LOCAL:USING DEFAULT ADMIN", FakeAdminService.DEFAULT_ADMIN);
            admin = FakeAdminService.DEFAULT_ADMIN;
        } else if (AuthenticationUtil.getAuthenticationType(request).equals(AuthenticationType.CLOUD_FLARE_AUTHENTICATION)) {
            admin = getAdminIdFromCookie(Util.getCookie(request, AuthenticationType.CLOUD_FLARE_AUTHENTICATION.getCookieName()).get());
        } else {
            admin = getAdminFromSession(request.getSession());
        }
        return admin;
    }


    public static Admin getAdminWithEmailAndId(List<String> emailList) throws Exception {
        for (String email : emailList) {
            long adminId = LoginDbUtil.getAdminId(email);
            if (adminId != -1) {
                return new Admin(email, adminId);
            }
        }
        LoggingService.log(LOG, Level.error, "adminId not found for user : " + emailList.get(0));
//        if local then return .. else null -> do this only if failing on local
        return null;
    }
}
