package net.media.autotemplate.dal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.blocking.BlockingInfo;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.blocking.TemplateBlockingDal;
import net.media.autotemplate.dal.druid.DruidUtil;
import net.media.database.DatabaseException;

import java.util.*;

/*
    Created by shubham-ar
    on 14/2/18 11:55 PM   
*/
public class ATMappingUtil {
    public static Map<String, BlockingInfo> getBlockingData(String id, String level, CreativeConstants.Type type, BusinessUnit buSelected) throws DatabaseException {
        List<BlockingInfo> blockingInfos = TemplateBlockingDal.getEntityWiseCreativeStatus(id, level, buSelected);
        Map<String, BlockingInfo> creativesMap = new HashMap<>();
        for (int i = 0; i < blockingInfos.size(); i++) {
            BlockingInfo blockingInfo = blockingInfos.get(i);
            if (blockingInfo.getType().equals(type) && blockingInfo.getStatus().equals(CreativeConstants.Status.B)) {
                creativesMap.put(blockingInfo.getCreative() + blockingInfo.getSize(), blockingInfo);
            }
        }
        return creativesMap;
    }

    public static JsonObject mapData(JsonObject druidResponse, Map<String, BlockingInfo> blockingInfoMap, String idName) {
        JsonArray data = druidResponse.get("data").getAsJsonArray();
        JsonArray meta = druidResponse.get("meta").getAsJsonArray();
        if (data.size() != 0) {
            for (Iterator<JsonElement> iterator = data.iterator(); iterator.hasNext(); ) {
                JsonObject creativeRow = iterator.next().getAsJsonObject();
                String id = creativeRow.get(idName).getAsString();
                if (blockingInfoMap.containsKey(id)) {
                    BlockingInfo blockingInfo = blockingInfoMap.get(id);
                    creativeRow.addProperty("Status", blockingInfo.getStatus().name());
                    blockingInfoMap.remove(id);
                } else {
                    creativeRow.addProperty("Status", CreativeConstants.Status.NA.name());
                }
            }
        } else {
            DruidUtil.addMeta(meta, idName, "dimension");
            DruidUtil.addMeta(meta, "Template Size", "dimension");
            DruidUtil.addMeta(meta, "Admin Name", "dimension");
            DruidUtil.addMeta(meta, "Date Added", "dimension");
        }

        Set<Map.Entry<String, BlockingInfo>> remainingRules = blockingInfoMap.entrySet();
        for (Map.Entry<String, BlockingInfo> rule : remainingRules) {
            BlockingInfo blockingInfo = rule.getValue();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(idName, blockingInfo.getCreative());
            jsonObject.addProperty("Status", blockingInfo.getStatus().name());
            jsonObject.addProperty("Template Size", blockingInfo.getSize());
            jsonObject.addProperty("Admin Name", blockingInfo.getAdminName());
            jsonObject.addProperty("Date Added", blockingInfo.getCreationDate());
            for (JsonElement metaElements : meta) {
                JsonObject metaJson = metaElements.getAsJsonObject();
                String type = metaJson.get("type").getAsString();

                if (type.contains("metric")) {
                    jsonObject.addProperty(metaJson.get("name").getAsString(), 0);
                }
            }
            data.add(jsonObject);
        }

        return druidResponse;
    }

}
