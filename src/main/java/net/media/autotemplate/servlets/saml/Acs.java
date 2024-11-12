package net.media.autotemplate.servlets.saml;

import com.onelogin.saml2.Auth;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.enums.AuthenticationType;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.util.AuthenticationUtil;
import net.media.autotemplate.util.Util;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by saksham on 6/2/17.
 */
public class Acs extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Auth auth = new Auth(request, response);
            try {
                auth.processResponse();
            } catch (Exception e) {
                throw new NullPointerException();
            }
            if (auth.isAuthenticated() && auth.getErrors().isEmpty()) {
                Map<String, List<String>> attributes = auth.getAttributes();
                String nameId = auth.getNameId();
                String canonicalName = attributes.get("cn").get(0);
                String email = attributes.get("mail").get(0);

                List<String> emailList = Util.getEmailList(canonicalName, email);
                HttpSession session = request.getSession();
                Admin admin = AdminFactory.getAdminWithEmailAndId(emailList);

                if (AuthenticationUtil.getAuthenticationType(request).equals(AuthenticationType.VPN_AUTHENTICATION)) {
                    session.setAttribute("admin_id", admin.getAdminId());
                    session.setAttribute("nameId", nameId);
                    session.setAttribute("admin_name", canonicalName);
                    session.setAttribute("admin_email", admin.getAdminEmail());
                    session.setAttribute("sessionId", auth.getSessionIndex());
                    session.setAttribute("auth", auth);
                    session.setAttribute("attributes", attributes);
                }
                String uri = (String) session.getAttribute("redirect-url");
                if (uri != null) {
                    response.sendRedirect(uri);
                } else {
                    response.sendRedirect(request.getContextPath() + "/web/");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }
}