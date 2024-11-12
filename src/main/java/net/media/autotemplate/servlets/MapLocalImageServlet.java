package net.media.autotemplate.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.factory.AdminFactory;
import net.media.autotemplate.services.AssetDetailService;
import net.media.autotemplate.services.OzilApiService;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MultipartConfig(fileSizeThreshold = 3 * 1024 * 1024 * 10, maxFileSize = 3 * 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 5 * 5)
public class MapLocalImageServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(MapLocalImageServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

        try {
            generateResponse(request, response);
        } catch (Exception e) {
            LoggingService.log(LOG, Level.info, "Servlet Response", e.getMessage().replaceAll("[\r\n]+", " "));
            throw new ServletException(e.getMessage().replaceAll("[\r\n]+", " "));
        }
    }

    protected void generateResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonObject jsonObject = new JsonObject();
        try {
            Admin admin = AdminFactory.getAdmin(request);
            long adminId = admin.getAdminId();
            String entityName = request.getParameter("entity_name");
            String entityValueString = request.getParameter("entity_value");
            String imageSize = request.getParameter("asset_size");
            int setId = Integer.parseInt(request.getParameter("set_id"));

            List<Part> imageParts = getImageParts(request);
            Map<String, String> localImagesToUploadedImageUrlMap = AssetDetailService.getLocalToUploadedImagUrlMap(imageParts, setId);

            JsonArray jsonArray = new JsonArray();
            String[] entityValueList = entityValueString.split(",");
            for (String entityValue : entityValueList) {
                for (Part imagePart : imageParts) {
                    JsonObject item = new JsonObject();
                    item.addProperty("entity_name", entityName);
                    item.addProperty("entity_value", entityValue);
                    item.addProperty("asset_size", imageSize);
                    item.addProperty("set_id", setId);
                    item.addProperty("uploaded_asset_url", localImagesToUploadedImageUrlMap.get(OzilApiService.getFileName(imagePart)));
                    jsonArray.add(item);
                }
            }

            AssetDetailService.mapImageAssetData(jsonArray, adminId);
            response.setStatus(HttpServletResponse.SC_OK);

            jsonObject.addProperty("message", "success");
        } catch (Exception e) {
            jsonObject.addProperty("message", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LoggingService.log(LOG, Level.error, "Servlet Response", e.getMessage().replaceAll("[\r\n]+", " "));
            throw e;
        } finally {
            response.getWriter().print(jsonObject);
        }
    }

    @NotNull
    private List<Part> getImageParts(HttpServletRequest request) throws IOException, ServletException {
        List<Part> imageParts = new ArrayList<>();
        for (Part part : request.getParts()) {
            if (part.getName().equals("asset_list")) {
                imageParts.add(part);
            }
        }
        return imageParts;
    }

}
