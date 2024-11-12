package net.media.autotemplate.bean;

/**
 * Created by sumeet
 * on 10/11/17.
 */
public class AdAttribution {

    private final String text;
    private final String link;

    public AdAttribution(String text, String link) {
        this.link = link;
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public String getText() {
        return text;
    }

}
