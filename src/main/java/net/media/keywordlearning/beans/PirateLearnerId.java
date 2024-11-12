package net.media.keywordlearning.beans;

/**
 * Created by autoopt/rohit.aga.
 */
public class PirateLearnerId {
    private final Boolean isActive;
    private final String description;
    private final int learnerId;

    public PirateLearnerId(Boolean isActive, String description, int learnerId) {
        this.isActive = isActive;
        this.description = description;
        this.learnerId = learnerId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getDescription() {
        return description;
    }

    public int getLearnerId() {
        return learnerId;
    }
}
