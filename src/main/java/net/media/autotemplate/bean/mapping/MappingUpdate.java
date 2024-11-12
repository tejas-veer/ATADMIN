package net.media.autotemplate.bean.mapping;

import java.util.ArrayList;
import java.util.List;

/*
    Created by shubham-ar
    on 28/3/18 4:47 PM   
*/
public class MappingUpdate<T extends MappingTemplate> {
    private List<T> updates;

    public MappingUpdate(List<T> items) {
        this.updates = items;
    }

    public MappingUpdate() {
        this(new ArrayList<>());
    }

    public List<T> getUpdates() {
        return updates;
    }

    public void setUpdates(List<T> updates) {
        this.updates = updates;
    }
}
