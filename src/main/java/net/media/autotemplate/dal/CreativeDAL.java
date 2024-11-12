package net.media.autotemplate.dal;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.bean.Template;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.DbConstants;
import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.dal.druid.DruidUtil;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.XmlUtil;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CreativeDAL {

    private static Gson gson = new Gson();
    private static final Logger LOG = LogManager.getLogger(CreativeDAL.class);

    public static void combineDataFromSources(Map<String, Template> templateMap, JsonObject druidResponse) {
        JsonArray druidMeta = druidResponse.get("meta").getAsJsonArray();
        JsonArray druidData = druidResponse.get("data").getAsJsonArray();

        for (JsonElement jsEle : druidData) {
            JsonObject item = jsEle.getAsJsonObject();
            String templateId = DruidDAL.getBracedId(item.get(DruidConstants.TEMPLATE_TITLE).getAsString()).trim();
            item.addProperty("isAT", false);
            item.addProperty("Source", DbConstants.Source.UNKNOWN.name());
            item.addProperty("Status", CreativeConstants.ServingStatus.NA.name());

            item.addProperty("TemplateID", templateId);
            String state = item.get("Auto Template State").getAsString();
            if (state.equals(DruidConstants.ATState.ENABLED.getVal())) {
                item.addProperty("isAT", true);
                item.addProperty("Source", DbConstants.Source.BANDIT_MAPPED.name());
                item.addProperty("Status", CreativeConstants.ServingStatus.ENABLED.name());
            }

            if (templateMap.containsKey(templateId)) {
                Template template = templateMap.get(templateId);
                item.addProperty("isCMInfo", true);
                item.addProperty("Source", template.getSource());
                item.addProperty("Status", template.getStatus() ? CreativeConstants.ServingStatus.ENABLED.name() : CreativeConstants.ServingStatus.DISABLED.name());
                templateMap.remove(templateId);
            }
        }

        DruidUtil.removeMeta(druidMeta, "Auto Template State");

        DruidUtil.addMeta(druidMeta, "Rank", "metric");
        DruidUtil.addMeta(druidMeta, "Status", "dimension");
        DruidUtil.addMeta(druidMeta, "TemplateID", "dimension");
        //adding remaining db templates back
        Set<Map.Entry<String, Template>> remainingTemplates = templateMap.entrySet();
        LoggingService.log(LOG, Level.info, "Unmatched templates " + remainingTemplates.size());
        for (Map.Entry<String, Template> entry : remainingTemplates) {
            JsonObject ent = Util.getJsonObject(entry.getValue(), JsonObject.class);
            ent.addProperty("Template", ent.get("TemplateID").getAsString());
            ent.addProperty("isAT", true);
            ent.addProperty("Source", entry.getValue().getSource());
            ent.addProperty("Auto Template State", "AUTO_TEMPLATE_ENABLED");
            ent.addProperty("Status", entry.getValue().getStatus() ? CreativeConstants.ServingStatus.ENABLED.name() : CreativeConstants.ServingStatus.DISABLED.name());
            for (int i = 0; i < druidMeta.size(); i++) {
                JsonObject mdata = druidMeta.get(i).getAsJsonObject();
                String metaName = mdata.get("name").getAsString();
                if (!ent.has(metaName)) {
                    ent.addProperty(metaName, 0);
                }
            }
            druidData.add(ent);
        }
    }

    public static Map<String, Template> getTemplateMap(List<Template> templates) {
        Map<String, Template> templateMap = new HashMap<>();
        for (Template template : templates) {
            templateMap.put(template.getTemplateId(), template);
        }
        return templateMap;
    }
}
