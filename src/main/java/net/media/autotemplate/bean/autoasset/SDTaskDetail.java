package net.media.autotemplate.bean.autoasset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.media.autotemplate.util.Util;

import static net.media.autotemplate.constants.AutoAssetConstants.GPT_GENERATED_PROMPT;

public class SDTaskDetail {
    @SerializedName("kwd")
    private final String keyword;
    @SerializedName("pmt")
    private String prompt;
    @SerializedName("pmt_id")
    private Long promptId;
    @SerializedName("npmt")
    private String negativePrompt;
    @SerializedName("npmt_id")
    private Long negativePromptId;
    @SerializedName("ver")
    private final String version;

    public SDTaskDetail(String keyword, String prompt, String negativePrompt, String version) {
        this.keyword = keyword;
        this.prompt = prompt;
        this.negativePrompt = negativePrompt;
        this.version = version;
    }

    public SDTaskDetail(String keyword, JsonElement promptElement, JsonElement negativePromptElement, JsonElement versionElement) {
        this.keyword = keyword;
        this.version = Util.isStringSet(versionElement.getAsString()) ? versionElement.getAsString() : GPT_GENERATED_PROMPT;
        this.prompt = Util.isStringSet(promptElement.getAsString()) & !version.equals(GPT_GENERATED_PROMPT) ? promptElement.getAsString() : "";
        this.negativePrompt = Util.isStringSet(negativePromptElement.getAsString()) && !version.equals(GPT_GENERATED_PROMPT) ? negativePromptElement.getAsString() : "";
    }

    public String getKeyword() {
        return keyword;
    }

    public String getPrompt() {
        return prompt;
    }

    public Long getPromptId() {
        return promptId;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    public String getNegativePrompt() {
        return negativePrompt;
    }

    public Long getNegativePromptId() {
        return negativePromptId;
    }

    public void setNegativePromptId(Long negativePromptId) {
        this.negativePromptId = negativePromptId;
    }

    public String getVersion() {
        return version;
    }

    public String getTaskInputDetailString() {
        JsonObject jsonObject = new JsonObject();
        checkAndAddProperty(jsonObject, "kwd", keyword);
        checkAndAddProperty(jsonObject, "pmt_id", promptId != null ? String.valueOf(promptId) : null);
        checkAndAddProperty(jsonObject, "npmt_id", negativePromptId != null ? String.valueOf(negativePromptId) : null);
        checkAndAddProperty(jsonObject, "ver", version);
        return jsonObject.toString();
    }

    private static void checkAndAddProperty(JsonObject jsonObject, String propertyName, String propertyValue) {
        if (!jsonObject.has(propertyName) && Util.isStringSet(propertyValue) && !propertyValue.equals("null")) {
            jsonObject.addProperty(propertyName, propertyValue);
        }
    }
}
