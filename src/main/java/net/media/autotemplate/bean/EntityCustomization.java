package net.media.autotemplate.bean;

/**
 * Created by sumeet
 * on 10/11/17.
 */
public class EntityCustomization {

    private final String headerText;
    private final AdAttribution adAttribution;

    public EntityCustomization(String headerText, AdAttribution adAttribution) {
        this.adAttribution = adAttribution;
        this.headerText = headerText;
    }

    public AdAttribution getAdAttribution() {
        return adAttribution;
    }

    public String getHeaderText() {
        return headerText;
    }
}
