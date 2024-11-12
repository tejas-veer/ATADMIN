package net.media.autotemplate.servlets;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.util.AuthenticationUtil;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by saksham on 9/5/16.
 */
public class Login extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(Login.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Long adminId = null;
        try {
            Admin admin = AdminFactory.getAdmin(request);
            if (Util.isSet(admin)) {
                adminId = admin.getAdminId();
            }
        } catch (Exception ignored) {
            LoggingService.log(LOG, Level.warn, "", ignored);
        }
        if (Util.isSet(adminId) && adminId != 0L) {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            String uri = (String) session.getAttribute("uri");

            if (!Util.isStringSet(uri) || uri.isEmpty() || uri.equals("")) {
                uri = "web/";
            }
            response.setHeader("Location", uri);
        } else {
            Pair<Boolean, String> loginStatus = AuthenticationUtil.getAuthenticationType(request).getLoginFunction().apply(request, response);
            if (!loginStatus.first) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}