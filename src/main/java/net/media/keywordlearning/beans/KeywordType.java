package net.media.keywordlearning.beans;

/**
 * Created by autoopt/rohit.aga.
 */
public class KeywordType {
    private final int keywordTypeId;
    private final String keywordType;

    public KeywordType(int keywordTypeId, String keywordType) {
        this.keywordTypeId = keywordTypeId;
        this.keywordType = keywordType;
    }

    public int getKeywordTypeId() {
        return keywordTypeId;
    }

    public String getKeywordType() {
        return keywordType;
    }
}
