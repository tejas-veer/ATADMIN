package net.media.autotemplate.routes.util;


import net.media.autotemplate.bean.ApiResponse;

import net.media.autotemplate.services.DebugService;

import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.BadPayloadException;
import spark.Request;
import spark.Response;

import javax.security.sasl.AuthenticationException;
import java.util.concurrent.ExecutionException;

public abstract class DebugRoute extends AbstractRoute {
    private static final Logger LOG = LogManager.getLogger(EntityRoute.class);
    protected abstract String makeResponse(Request request, DebugService debugService) throws Exception;
    @Override
    public ApiResponse getResponse(Request request, Response response) throws Exception {
        String errorMessage = "";
        try {
            DebugService debugService = new DebugService();
            try {
                return new ApiResponse(HttpStatus.SC_OK, makeResponse(request, debugService));
            } catch (BadPayloadException payloadException) {
                return new ApiResponse(HttpStatus.SC_BAD_REQUEST, payloadException.getMessage());
            }
        } catch (DatabaseException | ExecutionException e) {
            response.status(HttpStatus.SC_UNPROCESSABLE_ENTITY);
            response.body(errorMessage);
            LoggingService.log(LOG, Level.error, "Error in creating Debug Service | Debug Creation ", e);
        } catch (Authentication.Failed | AuthenticationException failed) {
            response.status(HttpStatus.SC_FORBIDDEN);
            response.body("Authentication failed");
            LoggingService.log(LOG, Level.error, "Error in creating Debug Service | Authentication", failed);
        }
        return null;
    }

}
