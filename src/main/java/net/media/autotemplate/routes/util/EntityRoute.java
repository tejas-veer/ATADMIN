package net.media.autotemplate.routes.util;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.services.EntityService;
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

/*
    Created by shubham-ar
    on 8/1/18 1:34 PM   
*/
//todo rename this to AbstractEntityRoute
public abstract class EntityRoute extends AbstractRoute {
    private static final Logger LOG = LogManager.getLogger(EntityRoute.class);


    protected abstract String makeResponse(Request request, EntityService entityService) throws Exception;

    @Override
    public ApiResponse getResponse(Request request, Response response) throws Exception {
        String errorMessage = "";
        try {
            errorMessage = "Entity Id not found";
            Long entityId = Long.parseLong(request.params(":entityId"));
            Entity entity = AutoTemplateDAL.getEntityInfo(entityId);
            errorMessage = "Admin could not be created from session";
            Admin admin = AdminFactory.getAdmin(request);
            BusinessUnit businessUnit = BusinessUnit.getBUFromName(getBUSelected());
            EntityService entityService = new EntityService(entity, admin, businessUnit);
            try {
                return new ApiResponse(HttpStatus.SC_OK, makeResponse(request, entityService));
            } catch (BadPayloadException payloadException) {
                //todo replace the above exception with custom if no appropriate exception is available in j2se
                return new ApiResponse(HttpStatus.SC_BAD_REQUEST, payloadException.getMessage());
            }
        } catch (DatabaseException | ExecutionException e) {
            response.status(HttpStatus.SC_UNPROCESSABLE_ENTITY);
            //todo return a json, instead of normal string
            response.body(errorMessage);
            LoggingService.log(LOG, Level.error, "Error in creating Entity Service | Entity Creation ", e);
        } catch (Authentication.Failed | AuthenticationException failed) {
            response.status(HttpStatus.SC_FORBIDDEN);
            //todo return a json, instead of normal string
            response.body("Authentication failed");
            LoggingService.log(LOG, Level.error, "Error in creating Entity Service | Authentication", failed);
        }
        //todo replace this with something more meaningful
        return null;
    }
}
