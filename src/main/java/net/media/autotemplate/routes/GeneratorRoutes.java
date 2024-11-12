package net.media.autotemplate.routes;

import com.google.gson.Gson;
import net.media.autotemplate.bean.ApiResponse;
import net.media.autotemplate.constants.GeneratorType;
import net.media.autotemplate.routes.util.AbstractRoute;
import net.media.autotemplate.routes.util.GeneratorRoute;
import net.media.autotemplate.services.GeneratorService;
import net.media.autotemplate.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Response;
import spark.RouteGroup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static spark.Spark.*;

/*
    Created by shubham-ar
    on 25/10/17 1:48 PM   
*/
public class GeneratorRoutes implements RouteGroup {
    private static final Gson GSON = Util.getGson();
    private static final ExecutorService KBB_HITTER_EXECUTOR_SERVICE = Executors.newFixedThreadPool(20);
    private static final Logger LOG = LogManager.getLogger(GeneratorRoutes.class);
    public static final String GENERATOR_INSERT_URL = "http://at.internal.media.net/TemplateGenerator/insertTemplate";
    private static final String KBB_CALL_BASE = "http://c8-kbb-1.srv.media.net:8989/kbb/template_api.php?isactive=0&nocache=1";
    private static final int MAX_RETRIES_PER_CALL = 3;


    @Override
    public void addRoutes() {


        path("/zeroColor", new ZeroColorRoutes());

        get("/mockPoll/:entityId", new GeneratorRoute() {
            @Override
            protected ApiResponse makeResponse(Request req, GeneratorService generatorService) throws Exception {
                return new ApiResponse(generatorService.mockResponse());
            }
        });

        post("/entity/:entityId/insert", new GeneratorRoute() {

            @Override
            protected ApiResponse makeResponse(Request req, GeneratorService generatorService) throws Exception {
                return new ApiResponse(generatorService.insertTask(req.body(), GeneratorType.OLD));
            }

        });

        post("/v2/entity/:entityId/insert", new GeneratorRoute() {

            @Override
            protected ApiResponse makeResponse(Request req, GeneratorService generatorService) throws Exception {
                return new ApiResponse(generatorService.insertTask(req.body(), GeneratorType.NEW));
            }

        });

        get("/entity/:entityId/new", new GeneratorRoute() {
            @Override
            protected ApiResponse makeResponse(Request req, GeneratorService generatorService) throws Exception {
                return new ApiResponse(generatorService.queueTask(GeneratorType.OLD));
            }
        });

        get("/v2/generate/entity/:entityId", new GeneratorRoute() {
            @Override
            protected ApiResponse makeResponse(Request req, GeneratorService generatorService) throws Exception {
                return new ApiResponse(generatorService.queueTask(GeneratorType.NEW));
            }
        });

        get("/v2/poll/:requestId", new AbstractRoute() {
            @Override
            public ApiResponse getResponse(Request req, Response resp) throws Exception {
                return GeneratorService.pollStatus(req.params(":requestId")).getApiResponse();
            }
        });

        get("/poll/:requestId", new AbstractRoute() {
            @Override
            public ApiResponse getResponse(Request req, Response resp) throws Exception {
                return GeneratorService.poll(req.params(":requestId")).getApiResponse();
            }
        });
    }
}
