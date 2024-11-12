package net.media.autotemplate.routes.util;

import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.services.GeneratorService;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.BadPayloadException;
import spark.Request;
import spark.Response;

import javax.management.BadAttributeValueExpException;

/*
    Created by shubham-ar
    on 30/1/18 6:36 PM   
*/
abstract public class GeneratorRoute extends AbstractRoute {
    private static final Logger LOG = LogManager.getLogger(GeneratorRoute.class);

    protected abstract ApiResponse makeResponse(Request req, GeneratorService generatorService) throws Exception;

    @Override
    public ApiResponse getResponse(Request req, Response resp) throws Exception {
        try {
            GeneratorService generatorService = new GeneratorService(req);
            return makeResponse(req, generatorService);
        } catch (BadAttributeValueExpException e) {
            LoggingService.log(LOG, Level.error, "GENERATOR_BAD_ATTRIBUTE|" + req.url(), e);
            return new ApiResponse(HttpStatus.SC_UNPROCESSABLE_ENTITY, e.toString());
        } catch (BadPayloadException e) {
            return new ApiResponse(HttpStatus.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
