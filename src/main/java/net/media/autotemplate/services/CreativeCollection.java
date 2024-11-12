package net.media.autotemplate.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Framework;
import net.media.autotemplate.bean.FrameworkStatus;
import net.media.autotemplate.bean.SizePair;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.util.Separator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CreativeCollection {
    private String entity;
    private HashMap<String, Framework> frameworks;
    private static HashMap<String, Integer> typeMap = new HashMap<>();

    static {
        typeMap.put(ConfigConstants.BLACK_LIST, 2);
        typeMap.put(ConfigConstants.WHITE_LIST, 1);
    }

    public CreativeCollection(String entity) {
        this.entity = entity;
        frameworks = new HashMap<>();
    }

    public String getStatus(String id, String size) {
        Framework framework = frameworks.get(id);
        return framework.getStatus(size);
    }

    public void addTupple(FrameworkStatus frameworkStatus) {
        if (!frameworks.containsKey(frameworkStatus.getFramework())) {
            frameworks.put(frameworkStatus.getFramework(), new Framework(frameworkStatus.getFramework()));
        }
        frameworks.get(frameworkStatus.getFramework()).addSize(new SizePair(frameworkStatus.getSize(), frameworkStatus.getStatus()));
    }

    public void superImpose(JsonObject globalData) {
        JsonArray frameworksArray = globalData.get("frameworks").getAsJsonArray();
        for (JsonElement frameworkEle : frameworksArray) {
            JsonObject frameworkObj = frameworkEle.getAsJsonObject();
            JsonArray size_tags = frameworkObj.get("size_tags").getAsJsonArray();
            Framework framework = frameworks.get(frameworkObj.get("id").getAsString());
            if (framework != null) {
                for (JsonElement sizeEle : size_tags) {
                    JsonObject sizeObj = sizeEle.getAsJsonObject();
                    String size_tag = sizeObj.get("size_tag").getAsString();
                    if (framework.hasSize(size_tag)) {
                        sizeObj.addProperty("status", typeMap.get(framework.getStatus(size_tag)));
                    }
                }
            }
        }
    }

    public String getFrameworksString(String status) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        Set<Map.Entry<String, Framework>> entries = frameworks.entrySet();
        for (Map.Entry<String, Framework> entry : entries) {
            Framework framework = entry.getValue();
            String frameworkString = framework.getString(status);
            if (frameworkString != null) {
                if (!first) builder.append(Separator.ROW);
                builder.append(frameworkString);
                first = false;
            }
        }
        return builder.toString();
    }

    public String getEntity() {
        return entity;
    }

    public String getXML() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        Set<Map.Entry<String, Framework>> entries = frameworks.entrySet();

        for (Map.Entry<String, Framework> entry : entries) {
            Framework framework = entry.getValue();
            builder.append(framework.getXML());
        }

        return "<rt>" + builder.toString() + "</rt>";
    }

    public boolean isEmpty() {
        return frameworks.isEmpty();
    }
}
