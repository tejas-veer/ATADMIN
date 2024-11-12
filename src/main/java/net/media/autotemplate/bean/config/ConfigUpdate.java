package net.media.autotemplate.bean.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/*
    Created by shubham-ar
    on 23/3/18 4:10 PM   
*/
public class ConfigUpdate {
    private List<ConfigLine> additions;
    private List<ConfigLine> deletes;
    private Boolean reset;
    private Boolean featureMapping;

    public List<ConfigLine> getAdditions() {
        return additions;
    }

    public void setAdditions(List<ConfigLine> additions) {
        this.additions = additions;
    }

    public List<ConfigLine> getDeletes() {
        return deletes;
    }

    public void setDeletes(List<ConfigLine> deletes) {
        this.deletes = deletes;
    }

    public Boolean isReset() {
        return reset;
    }

    public void setReset(Boolean reset) {
        this.reset = reset;
    }

    public boolean isEnableFeatureMapping() {
        return Objects.nonNull(featureMapping) && featureMapping;
    }
}
