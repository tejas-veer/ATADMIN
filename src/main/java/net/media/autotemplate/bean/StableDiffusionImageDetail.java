package net.media.autotemplate.bean;

import net.media.autotemplate.enums.ATRequestState;

public class StableDiffusionImageDetail {
    private long taskId;
    private String imageUrl;
    private String keyword;
    private String prompt;
    private String negativePrompt;
    private String version;
    private ATRequestState taskState;

    public StableDiffusionImageDetail(long taskId, String imageUrl, String keyword, String prompt, String negativePrompt, String version, ATRequestState state) {
        this.taskId = taskId;
        this.imageUrl = imageUrl;
        this.keyword = keyword;
        this.prompt = prompt;
        this.negativePrompt = negativePrompt;
        this.version = version;
        this.taskState = state;
    }

    public long getTaskId() {
        return taskId;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getVersion() {
        return version;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ATRequestState getTaskState() {
        return taskState;
    }
}
