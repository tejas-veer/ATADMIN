package net.media.autotemplate.servlets.filter;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.util.SysProperties;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/*
    Created by shubham-ar
    on 25/10/17 4:39 PM   
*/
public class ApiAuthFilter implements Filter {

    private static final Logger log = LogManager.getLogger(ApiAuthFilter.class);
    private String contextPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        contextPath = filterConfig.getServletContext().getContextPath();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LoggingService.log(log, Level.info, "SESSION_CHECK_FILTER_STARTED");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String path = req.getRequestURI();
        if (SysProperties.getPropertyAsBoolean("LOCAL")) {
            LoggingService.log(log, Level.info, "IGNORING_AUTH_FOR_LOCAL");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (path.contains("/api/search/")) {
            String apiKey = req.getParameter("api-key");
            if (Objects.nonNull(apiKey)) {
                LoggingService.log(log, Level.info, "IGNORING_AUTH_FOR_API_KEY");
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        Long adminId = null;
        try {
            Admin admin = AdminFactory.getAdmin(req);
            adminId = admin.getAdminId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (adminId == null) {
            sendError(resp);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void sendError(HttpServletResponse servletResponse) throws IOException {
        servletResponse.sendError(HttpStatus.SC_UNAUTHORIZED, "user unAuthenticated");
    }

    @Override
    public void destroy() {

    }
}