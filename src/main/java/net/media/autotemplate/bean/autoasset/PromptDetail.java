package net.media.autotemplate.bean.autoasset;

public class PromptDetail {
    private final Long id;
    private final String prompt;
    private final String hash;

    public PromptDetail(Long id, String prompt, String hash) {
        this.id = id;
        this.prompt = prompt;
        this.hash = hash;
    }

    public Long getId() {
        return id;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getHash() {
        return hash;
    }


}
