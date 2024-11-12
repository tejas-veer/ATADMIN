package net.media.autotemplate.servlets.saml;

import com.onelogin.saml2.Auth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by vishal.p on 13/02/17.
 */
public class Sls extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Auth auth = new Auth(req, resp);
            auth.processSLO();
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (Exception e) {
            resp.sendError(520);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            HttpSession session = req.getSession();
            Auth auth = new Auth(req, resp);
//            auth.processSLO();
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (Exception e) {
            resp.sendError(520, "Logout Fail");
        }
    }
}