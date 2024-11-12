package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.bean.blocking.BlockingInfo;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.dal.druid.DruidUtil;
import net.media.autotemplate.util.Util;
import net.media.database.DatabaseException;

import java.util.*;


/*
    Created by shubham-ar
    on 9/2/18 12:57 PM   
*/
public class BlockingService {
    public static final Gson GSON = Util.getGson();
    private Entity entity;
    private final Map<CreativeConstants.Level, Set<String>> blacklistedFrameworksMap, blacklistedTemplatesMap;
    private final Map<CreativeConstants.Level, String> idMap;
    public static final CreativeConstants.Level[] hierarchy = {CreativeConstants.Level.GLOBAL, CreativeConstants.Level.PARTNER, CreativeConstants.Level.CUSTOMER, CreativeConstants.Level.ADTAG, CreativeConstants.Level.DOMAIN, CreativeConstants.Level.ENTITY};
    private String businessUnit;

    public BlockingService(Entity entity, String businessUnit) throws DatabaseException {
        this.entity = entity;
        this.businessUnit = businessUnit;
        blacklistedFrameworksMap = getRules(CreativeConstants.Type.FRAMEWORK, this.businessUnit);
        blacklistedTemplatesMap = getRules(CreativeConstants.Type.TEMPLATE, this.businessUnit);
        idMap = new HashMap<CreativeConstants.Level, String>() {{
            for (CreativeConstants.Level level : hierarchy) {
                this.put(level, level.getter().apply(entity));
            }
        }};
    }

    private Map<CreativeConstants.Level, Set<String>> getRules(CreativeConstants.Type type, String buSelected) throws DatabaseException {
        Map<CreativeConstants.Level, Set<String>> templateRules = new HashMap<>();
        List<BlockingInfo> blockingInfoList = AutoTemplateDAL.getCreativeStatus(entity, type, hierarchy, buSelected);
        for (BlockingInfo blockinginfo :
                blockingInfoList) {

            if (!blockinginfo.getStatus().equals(CreativeConstants.Status.B))
                continue;

            if (!blockinginfo.getSize().equals(entity.getSize()) && !blockinginfo.getSize().equals("ALL"))
                continue;

            Set<String> blockedIds = templateRules.computeIfAbsent(blockinginfo.getSupplyDemandHierarchy().getSupplyLevel(), k -> new HashSet<>());
            blockedIds.add(blockinginfo.getCreative());
        }

        return templateRules;
    }

    public JsonObject applyBlocking(JsonObject druidResponse) {
        JsonArray druidData = druidResponse.get("data").getAsJsonArray();
        for (JsonElement druidEle : druidData) {
            List<BlockingInfo> rules = new ArrayList<>();
            JsonObject templateRow = druidEle.getAsJsonObject();
            String framework = "800087293";
            String template = DruidDAL.getBracedId(templateRow.get("Template").getAsString());
            BlockingInfo topLevelRule = null;

            //framework wise blocking
            for (CreativeConstants.Level level :
                    hierarchy) {
                if (blacklistedFrameworksMap.getOrDefault(level, new HashSet<>()).contains(framework)) {

                    topLevelRule = new BlockingInfo(idMap.get(level), framework, CreativeConstants.Type.FRAMEWORK, entity.getSize(), CreativeConstants.Status.B, level);
                    rules.add(topLevelRule);
                }
            }

            //template wise blocking
            for (CreativeConstants.Level level :
                    hierarchy) {

                if (level == CreativeConstants.Level.ENTITY)
                    continue;

                if (blacklistedTemplatesMap.getOrDefault(level, new HashSet<>()).contains(template)) {
                    topLevelRule = new BlockingInfo(idMap.get(level), framework, CreativeConstants.Type.TEMPLATE, entity.getSize(), CreativeConstants.Status.B, level);
                    rules.add(topLevelRule);
                }

            }

            if (!rules.isEmpty()) {
                templateRow.addProperty("Status", CreativeConstants.ServingStatus.BLOCKED.name());
                templateRow.addProperty("blockingReason", topLevelRule.getMessage());
            }

            //Mark Template Status as False for Entity Blocking
            Set<String> entityBlacklists = blacklistedTemplatesMap.getOrDefault(CreativeConstants.Level.ENTITY, new HashSet<>());
            if (rules.isEmpty() && entityBlacklists.contains(template)) {
                templateRow.addProperty("Status", CreativeConstants.ServingStatus.DISABLED.name());
            }

            templateRow.add("blocking", GSON.toJsonTree(rules));

        }

        DruidUtil.removeMeta(druidResponse.get("meta").getAsJsonArray(), "Framework");

        return druidResponse;
    }
}
