package net.media.keywordlearning.beans;

/**
 * Created by autoopt/rohit.aga.
 */
public class PirateDebugResult {
    private final String canonicalHash;
    private final int kwdTypeId;
    private final double impression;
    private final double conversion;
    private final double revenue;
    private final String basis;
    private final double scaledImpression;
    private final double pirateScore;
    private final String url;
    private double scaledConversion;

    public PirateDebugResult(String canonicalHash, int kwdTypeId, double impression, double conversion, double revenue, String basis, double scaledImpression, double pirateScore, String url) {
        this.canonicalHash = canonicalHash;
        this.kwdTypeId = kwdTypeId;
        this.impression = impression;
        this.conversion = conversion;
        this.revenue = revenue;
        this.basis = basis;
        this.scaledImpression = scaledImpression;
        this.pirateScore = pirateScore;
        this.url = url;
    }

    public PirateDebugResult(PirateDebugResult pirateDebugResult) {
        this.canonicalHash = pirateDebugResult.getCanonicalHash();
        this.kwdTypeId = pirateDebugResult.getKwdTypeId();
        this.impression = pirateDebugResult.getImpression();
        this.conversion = pirateDebugResult.getConversion();
        this.revenue = pirateDebugResult.getRevenue();
        this.basis = pirateDebugResult.getBasis();
        this.scaledImpression = pirateDebugResult.getScaledImpression();
        this.pirateScore = pirateDebugResult.getPirateScore();
        this.url = pirateDebugResult.getUrl();
    }

    public String getCanonicalHash() {
        return canonicalHash;
    }

    public int getKwdTypeId() {
        return kwdTypeId;
    }

    public double getImpression() {
        return impression;
    }

    public double getConversion() {
        return conversion;
    }

    public double getRevenue() {
        return revenue;
    }

    public String getBasis() {
        return basis;
    }

    public double getScaledImpression() {
        return scaledImpression;
    }

    public double getPirateScore() {
        return pirateScore;
    }

    public String getUrl() {
        return url;
    }

    public double getScaledConversion() {
        return scaledConversion;
    }

    public void setScaledConversion(double scaledConversion) {
        this.scaledConversion = scaledConversion;
    }

    public double scaledRPM(){
        return this.scaledConversion / this.scaledImpression;
    }
}
