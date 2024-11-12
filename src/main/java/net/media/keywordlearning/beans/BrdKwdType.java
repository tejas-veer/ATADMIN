package net.media.keywordlearning.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by autoopt/rohit.aga.
 */
public class BrdKwdType {
    private Map<String, Basis> basis;
    private Performance performance;
    private final Double pirateScore;
    private final Integer brdKwdTypeId;


    public BrdKwdType(Performance performance, Double pirateScore, Integer brdKwdTypeId) {
        this.brdKwdTypeId = brdKwdTypeId;
        this.basis = new HashMap<>();
        this.performance = performance;
        this.pirateScore = pirateScore;
    }

    public void addPerformance(Performance performance){
        this.performance.addPerformance(performance);
    }

    public Double getPirateScore() {
        return pirateScore;
    }

    public Map<String, Basis> getBasis() {
        return basis;
    }

    public Integer getBrdKwdTypeId() {
        return brdKwdTypeId;
    }

    public Performance getPerformance() {
        return performance;
    }
}
