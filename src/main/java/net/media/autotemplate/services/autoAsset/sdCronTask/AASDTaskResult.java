package net.media.autotemplate.services.autoAsset.sdCronTask;

import net.media.autotemplate.bean.autoasset.AATaskDetail;

public class AASDTaskResult {
    private AATaskDetail aaTaskDetail;
    private String prompt;
    private String negativePrompt;

    public AASDTaskResult() {
    }

    public AATaskDetail getAATaskDetail() {
        return aaTaskDetail;
    }

    public void setAaTaskDetail(AATaskDetail aaTaskDetail) {
        this.aaTaskDetail = aaTaskDetail;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getNegativePrompt() {
        return negativePrompt;
    }

    public void setNegativePrompt(String negativePrompt) {
        this.negativePrompt = negativePrompt;
    }
}
