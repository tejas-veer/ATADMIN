package net.media.keywordlearning.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by autoopt/rohit.aga.
 */
public class Url {
    private Map<Integer, BrdKwdType> brdKwdTypes;
    private Performance performance;
    private final String canonicalHash;
    private final String url;

    public Url(String canonicalHash, Performance performance, String url) {
        this.url = url;
        this.brdKwdTypes = new HashMap<>();
        this.performance = performance;
        this.canonicalHash = canonicalHash;
    }

    public void addPerformance(Performance performance){
        this.performance.addPerformance(performance);
    }

    public Map<Integer, BrdKwdType> getBrdKwdTypes() {
        return brdKwdTypes;
    }

    public String getCanonicalHash() {
        return canonicalHash;
    }

    public String getUrl() {
        return url;
    }

    public Performance getPerformance() {
        return performance;
    }
}
