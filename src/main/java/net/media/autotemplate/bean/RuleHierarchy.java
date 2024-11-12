package net.media.autotemplate.bean;

import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.util.Separator;
import net.media.autotemplate.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
    Created by shubham-ar
    on 11/12/17 4:13 PM   
*/
public class RuleHierarchy {
    private List<Pair<CreativeConstants.Level, String>> hierarchy;
    private Iterator<Pair<CreativeConstants.Level, String>> iterator;
    private Pair<CreativeConstants.Level, String> currentElement = null;
    private Entity entity;

    public RuleHierarchy(Entity entity, String hierarchyStr) {
        hierarchy = new ArrayList<>();
        iterator = hierarchy.iterator();
        this.entity = entity;
        this.setHierarchy(hierarchyStr);
    }

    private void setHierarchy(String hierarchyString) {
        /*
        expects hierarchy string to be like "P,C,A,D,E"
         */
        String[] hierarchyArray = hierarchyString.split(Separator.DATA);
        for (String levelIdentifier : hierarchyArray) {
            CreativeConstants.Level level = null;
            String id = null;

            switch (levelIdentifier) {
                case "P":
                    level = CreativeConstants.Level.PARTNER;
                    id = entity.getPartnerId();
                    break;
                case "C":
                    level = CreativeConstants.Level.CUSTOMER;
                    id = entity.getCustomerId();
                    break;
                case "D":
                    level = CreativeConstants.Level.DOMAIN;
                    id = entity.getDomain();
                    break;
                case "E":
                    level = CreativeConstants.Level.ENTITY;
                    id = String.valueOf(entity.getEntityId());
                    break;
                case "A":
                    level = CreativeConstants.Level.ADTAG;
                    id = entity.getAdtagId();
                    break;
            }

            if (Util.isSet(level) && Util.isStringSet(id)) {
                hierarchy.add(new Pair<>(level, id));
            } else {
                throw new UnsupportedOperationException("bad input for entity | level -> " + level + "| id -> " + id);
            }

        }
        iterator = hierarchy.iterator();

    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Pair<CreativeConstants.Level, String> next() {
        currentElement = iterator.next();
        return currentElement;
    }

    public CreativeConstants.Level getCurrentLevel() {
        return currentElement.first;
    }

    public String getCurrentId() {
        return currentElement.second;
    }
}
