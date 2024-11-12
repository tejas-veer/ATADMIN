package net.media.autotemplate.servlets.filter;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.util.SysProperties;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by saksham
 * on 9/5/16.
 */
public class SessionCheckFilter implements Filter {

    private static final Logger log = LogManager.getLogger(SessionCheckFilter.class);
    private String contextPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        contextPath = filterConfig.getServletContext().getContextPath();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (SysProperties.getPropertyAsBoolean("LOCAL")) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        LoggingService.log(log, Level.info, "SESSION_CHECK_FILTER_STARTED");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String uri = Util.getUri(req);
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        if (!isLoggedIn(req)) {
            req.getSession().setAttribute("uri", uri);
            resp.sendRedirect(contextPath + "/login");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

    public boolean isLoggedIn(HttpServletRequest request) {
        try {
            Admin admin = AdminFactory.getAdmin(request);
            return Util.isSet(admin) && admin.getAdminId() != 0L;
        } catch (Exception e) {
            return false;
        }
    }
}