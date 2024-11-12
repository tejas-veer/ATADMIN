package net.media.keywordlearning.beans;

import java.util.List;

/**
 * Created by autoopt/rohit.aga.
 */
public class DomainAdTag {
    private final String domainName;
    private final List<String> adTagList;

    public DomainAdTag(String domainName, List<String> adTagList) {
        this.domainName = domainName;
        this.adTagList = adTagList;
    }

    public List<String> getAdTagList() {
        return adTagList;
    }

    public String getDomainName() {
        return domainName;
    }
}
