package net.media.keywordlearning.beans;

/**
 * Created by autoopt/rohit.aga.
 */
public class Basis {
    private final Performance performance;
    private final Double pirateScore;
    private final String basis;

    public Basis(Performance performance, Double pirateScore, String basis) {
        this.performance = performance;
        this.pirateScore = pirateScore;
        this.basis = basis;
    }

    public void addPerformance(Performance performance){
        this.performance.addPerformance(performance);
    }

    public Double getPirateScore() {
        return pirateScore;
    }

    public String getBasis() {
        return basis;
    }
}
