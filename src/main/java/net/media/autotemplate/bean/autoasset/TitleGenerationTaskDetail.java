package net.media.autotemplate.bean.autoasset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TitleGenerationTaskDetail {
    @Expose
    @SerializedName("dmd_kwd")
    private final String demandKeyword;
    @Expose
    @SerializedName("purl")
    private final String publisherPageUrl;
    @Expose
    @SerializedName("ptitle")
    private final String publisherPageTitle;
    @Expose
    @SerializedName("pmt_id")
    private final Long promptId;

    @Expose(serialize = false, deserialize = false)
    @SerializedName("pmt")
    private String prompt;

    public TitleGenerationTaskDetail(String demandKeyword, String publisherPageTitle, String publisherPageUrl, Long promptId) {
        this.demandKeyword = demandKeyword;
        this.publisherPageUrl = publisherPageUrl;
        this.publisherPageTitle = publisherPageTitle;
        this.promptId = promptId;
    }
    
    public String getDemandKeyword() {
        return demandKeyword;
    }

    public String getPublisherPageUrl() {
        return publisherPageUrl;
    }

    public String getPublisherPageTitle() {
        return publisherPageTitle;
    }

    public Long getPromptId() {
        return promptId;
    }

    public String getPrompt() {
        return prompt;
    }
}
