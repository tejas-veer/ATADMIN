package net.media.autotemplate.bean;

import net.media.autotemplate.util.Separator;

import java.util.HashSet;
import java.util.Iterator;

public class Framework implements Cloneable {
    private String id;
    private HashSet<SizePair> sizes;

    public String getId() {
        return id;
    }

    public Framework(String id) {
        this.id = id;
        this.sizes = new HashSet<>();
    }

    public void addSize(SizePair size) {
        sizes.add(size);
    }

    public void removeSize(SizePair size) {
        sizes.remove(size);
    }

    public boolean hasSize(String size) {
        SizePair sp = new SizePair(size, null);
        return sizes.contains(sp);
    }

    @Override
    public String toString() {
        Iterator<SizePair> iterator = sizes.iterator();
        StringBuilder tuppleStr = new StringBuilder();
        tuppleStr.append(id).append(Separator.TYPE);
        if (!iterator.hasNext()) return null;
        while (iterator.hasNext()) {
            tuppleStr.append(iterator.next().getTag());
            if (iterator.hasNext()) {
                tuppleStr.append(Separator.DATA);
            }
        }
        return tuppleStr.toString();
    }

    public String getString(String status) {
        Iterator<SizePair> iterator = sizes.iterator();
        StringBuilder tuppleStr = new StringBuilder();
        while (iterator.hasNext()) {
            SizePair sizePair = iterator.next();
            if (sizePair.getStatus().equals(status)) {
                tuppleStr.append(sizePair.getTag());
                if (iterator.hasNext()) {
                    tuppleStr.append(Separator.DATA);
                }
            }
        }
        if (tuppleStr.length() == 0) return null;

        return id + Separator.TYPE + tuppleStr.toString();
    }

    public String getStatus(String size_tag) {
        Iterator<SizePair> iterator = sizes.iterator();
        while (iterator.hasNext()) {
            SizePair sp = iterator.next();
            if (sp.getTag().equals(size_tag)) return sp.getStatus();
        }
        return null;
    }

    public StringBuilder getXML() {
        Iterator<SizePair> iterator = sizes.iterator();
        StringBuilder tuppleStr = new StringBuilder();
        while (iterator.hasNext()) {
            SizePair sizePair = iterator.next();
            tuppleStr.append("<r><f_id>").append(id).append("</f_id>");
            tuppleStr.append(sizePair.toXML());
            tuppleStr.append("</r>");
        }
        if (tuppleStr.length() == 0) return null;
        return tuppleStr;
    }
}
