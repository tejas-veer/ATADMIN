package net.media.autotemplate.bean;

/**
 * Created by Jatin Warade
 * on 22/05/24
 */
public class TemplateMetaInfo {
    private final long templateId;
    private String templateName;
    private final long frameworkId;
    private String frameworkName;
    private final String templateKey;

    public TemplateMetaInfo(long templateId, long frameworkId, String templateKey) {
        this.templateId = templateId;
        this.frameworkId = frameworkId;
        this.templateKey = templateKey;
    }

    public TemplateMetaInfo(long templateId, String templateName, long frameworkId, String frameworkName, String templateKey) {
        this.templateId = templateId;
        this.templateName = templateName;
        this.frameworkId = frameworkId;
        this.frameworkName = frameworkName;
        this.templateKey = templateKey;
    }

    public long getTemplateId() {
        return templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public long getFrameworkId() {
        return frameworkId;
    }

    public String getTemplateKey() {
        return templateKey;
    }
}
