package net.media.autotemplate.bean;

import com.google.gson.annotations.SerializedName;
import net.media.autotemplate.dal.db.DbConstants;

/*
    Created by shubham-ar
    on 18/10/17 2:44 PM   
*/
public class Template {
    @SerializedName("TemplateID")
    protected final String templateId;
    @SerializedName("Status")
    protected final boolean status;
    @SerializedName("Rank")
    protected final int rank;
    @SerializedName("Framework")
    private final String framework;
    protected final Integer source;

    public Template(String templateId, boolean status, int rank, String framework, Integer source) {
        this.templateId = templateId;
        this.status = status;
        this.rank = rank;
        this.framework = framework;
        this.source = source;
    }

    public Template(String templateId, boolean status, int rank) {
        this(templateId, status, rank, "", -1);
    }

    public String getTemplateId() {
        return templateId;
    }

    public boolean getStatus() {
        return status;
    }

    public int getRank() {
        return rank;
    }

    public String getSource() {
        switch (source) {
            case 1:
                return DbConstants.Source.MANUAL.name();
            case 2:
                return DbConstants.Source.AT_GENERATED.name();
        }
        return DbConstants.Source.UNKNOWN.name();
    }

    public String getFrameworkId() {
        return framework;
    }
}
