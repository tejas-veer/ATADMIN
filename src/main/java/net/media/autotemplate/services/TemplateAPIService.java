package net.media.autotemplate.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.dal.druid.DruidUtil;
import net.media.autotemplate.util.generator.TemplateAPIUtil;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/*
    Created by shubham-ar
    on 12/2/18 6:41 PM   
*/
public class TemplateAPIService {
    private static final Logger LOG = LogManager.getLogger(TemplateAPIService.class);
    private final Entity entity;
    private final JsonObject druidResponse;
    private JsonArray templates;
    private Map<String, Double> percentageMap;

    public TemplateAPIService(Entity entity, JsonObject druidResponse) {
        this.entity = entity;
        this.druidResponse = druidResponse;
        this.templates = druidResponse.getAsJsonArray("data");
        percentageMap = new HashMap<>();
        try {//get KBB Template API Data Percentage
            percentageMap = TemplateAPIUtil.getAggregatedTemplatePercentage(entity, 1);
        } catch (Exception e) {
            String message = "Error in getting percentages";
            LoggingService.log(LOG, Level.error, message, e);
            this.druidResponse.addProperty("template-api-error", message);
        }
    }

    public JsonObject mapPercentages() {
        long baseTemplateCount = 0;
        long errorTemplateCount = 0;
        Double basePc = 100.0;

        for (JsonElement templateRow : templates) {
            JsonObject template = templateRow.getAsJsonObject();
            template.addProperty("%", 0);
            String state = template.get("Auto Template State").getAsString();
            if (state.equals(DruidConstants.ATState.ENABLED.getVal())) {
                String templateId = DruidDAL.getBracedId(template.get("Template").getAsString());
                double percentage = percentageMap.getOrDefault(templateId, 0.0);
                template.addProperty("Serving Percentage", percentage);
                basePc -= percentage;
            } else if (state.equals(DruidConstants.ATState.BASE.getVal())) {
                baseTemplateCount++;
            } else {
                errorTemplateCount++;
            }
        }

        for (JsonElement templateRow : templates) {
            JsonObject template = templateRow.getAsJsonObject();
            template.addProperty("%", 0);
            String state = template.get("Auto Template State").getAsString();
            if (!state.equals(DruidConstants.ATState.ENABLED.getVal())) {
                if (state.equals(DruidConstants.ATState.BASE.getVal()))
                    template.addProperty("Serving Percentage", basePc / baseTemplateCount);
                else if (baseTemplateCount == 0)
                    template.addProperty("Serving Percentage", basePc / errorTemplateCount);
            }
        }
        //add Meta to DruidResponse
        DruidUtil.addMeta(druidResponse.get("meta").getAsJsonArray(), "Serving Percentage", "metric");
        return druidResponse;
    }

    public JsonObject addUrl() {
        druidResponse.addProperty("kbbUrl", TemplateAPIUtil.getKbbURL(entity));
        return druidResponse;
    }
}
