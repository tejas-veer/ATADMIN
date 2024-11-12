package net.media.autotemplate.servlets;


import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.util.AuthenticationUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by saksham on 10/5/16.
 */
public class Logout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Pair<Boolean, String> loginStatus = AuthenticationUtil.getAuthenticationType(request).getLogOutFunction().apply(request, response);
        if(!loginStatus.first){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}