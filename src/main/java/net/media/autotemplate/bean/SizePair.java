package net.media.autotemplate.bean;

//
public class SizePair {
    private String tag;
    private String status;

    public SizePair(String tag, String status) {
        this.tag = tag.trim();
        if (status != null)
            this.status = status.trim();
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof SizePair) && ((SizePair) o).getTag().equals(tag));
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "(" + tag + "->" + status + " )";
    }

    public String toXML() {
        return "<f_size>" + tag + "</f_size><f_status>" + status + "</f_status>";
    }
}
