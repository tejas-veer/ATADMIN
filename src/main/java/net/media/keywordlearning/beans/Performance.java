package net.media.keywordlearning.beans;

/**
 * Created by autoopt/rohit.aga.
 */
public class Performance {
    private double impression;
    private double conversion;
    private double revenue;
    private double scaledImpression;

    public Performance(double impression, double conversion, double revenue, double scaledImpression) {
        this.impression = impression;
        this.conversion = conversion;
        this.revenue = revenue;
        this.scaledImpression = scaledImpression;
    }

    public Performance() {
    }

    public double getImpression() {
        return impression;
    }

    public void setImpression(double impression) {
        this.impression = impression;
    }

    public double getConversion() {
        return conversion;
    }

    public void setConversion(double conversion) {
        this.conversion = conversion;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void addPerformance(Performance performance){
        this.impression += performance.getImpression();
        this.conversion += performance.getConversion();
        this.revenue += performance.getRevenue();
        this.scaledImpression += performance.getScaledImpression();
    }

    public double getScaledImpression() {
        return scaledImpression;
    }
}
