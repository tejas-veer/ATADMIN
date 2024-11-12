package net.media.autotemplate.routes.util;

import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.InboundParams;
import net.media.autotemplate.bean.RequestGlobal;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.util.ResponseUtil;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Authentication;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.security.sasl.AuthenticationException;

public abstract class AbstractRoute implements Route {
    private static final Logger LOG = LogManager.getLogger(AbstractRoute.class);

    public abstract ApiResponse getResponse(Request req, Response resp) throws Exception;

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            RequestGlobal.setRequest(request);
            RequestGlobal.setAdmin(AdminFactory.getAdmin(request));
            ApiResponse apiResponse = getResponse(request, response);
            response.status(apiResponse.getStatusCode());
            return apiResponse.getResponse();
        } catch (Authentication.Failed e) {
            e.printStackTrace();
            response.status(HttpStatus.SC_FORBIDDEN);
            return e.getMessage();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            response.status(HttpStatus.SC_UNAUTHORIZED);
            return e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            LoggingService.log(LOG, Level.error, "INTERNAL_ERROR", e);
            return ResponseUtil.getErrorResponse(e, LOG, request);
        } finally {
            cleanResources();
        }
    }

    protected void cleanResources() {
        RequestGlobal.destroy();
    }

    public String getBUSelected(){
        return InboundParams.getInstance().getBuSelected();
    }


}
