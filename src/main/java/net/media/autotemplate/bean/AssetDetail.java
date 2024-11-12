package net.media.autotemplate.bean;

import com.google.gson.JsonObject;
import net.media.autotemplate.util.Util;

public class AssetDetail {
    private long id;
    private final String entityName;
    private final String entityValue;
    private final String keyValue;
    private String keyHash;
    private final String assetType;
    private long assetId;
    private final Long extAssetId;
    private final String assetValue;
    private final String basis;
    private final int setId;
    private String prompt;
    private final String size;
    private final float score;
    private final int isActive;
    private String updationDate;

    private long adminId;
    private String adminName;

    public AssetDetail(
            long id,
            String entityName,
            String entityValue,
            String keyValue,
            String keyHash,
            String assetType,
            long assetId,
            Long extAssetId,
            String assetValue,
            String basis,
            int setId,
            String size,
            float score,
            int isActive,
            long adminId
    ) {
        this.id = id;
        this.entityName = entityName;
        this.entityValue = entityValue;
        this.keyValue = keyValue;
        this.keyHash = keyHash;
        this.assetType = assetType;
        this.assetId = assetId;
        this.extAssetId = extAssetId;
        this.assetValue = assetValue;
        this.basis = basis;
        this.setId = setId;
        this.size = size;
        this.score = score;
        this.isActive = isActive;
        this.adminId = adminId;
    }

    public AssetDetail(
            String entityName,
            String entityValue,
            String keyValue,
            String assetType,
            Long extAssetId,
            String assetValue,
            String basis,
            int setId,
            String size,
            float score,
            int isActive
    ) {
        this.entityName = entityName;
        this.entityValue = entityValue;
        this.keyValue = keyValue;
        this.assetType = assetType;
        this.extAssetId = extAssetId;
        this.assetValue = assetValue;
        this.basis = basis;
        this.setId = setId;
        this.size = size;
        this.score = score;
        this.isActive = isActive;
    }

    public AssetDetail(
            long id,
            String entityName,
            String entityValue,
            String keyValue,
            String keyHash,
            String assetType,
            Long extAssetId,
            String assetValue,
            String basis,
            int setId,
            String size,
            float score,
            int isActive,
            long adminId,
            String adminName,
            String updationDate
    ) {
        this.id = id;
        this.entityName = entityName;
        this.entityValue = entityValue;
        this.keyValue = keyValue;
        this.keyHash = keyHash;
        this.assetType = assetType;
        this.extAssetId = extAssetId;
        this.assetValue = assetValue;
        this.basis = basis;
        this.setId = setId;
        this.size = size;
        this.score = score;
        this.isActive = isActive;
        this.adminId = adminId;
        this.adminName = adminName;
        this.updationDate = updationDate;
    }

    public long getId() {
        return id;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public String getAssetType() {
        return assetType;
    }

    public Long getExtAssetId() {
        return extAssetId;
    }

    public String getAssetValue() {
        return assetValue;
    }

    public String getBasis() {
        return basis;
    }

    public int getSetId() {
        return setId;
    }

    public String getSize() {
        return size;
    }

    public float getScore() {
        return score;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getUpdationDate() {
        return updationDate;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public JsonObject getAsJson() {
        JsonObject jb = new JsonObject();
        jb.addProperty("id", id);
        jb.addProperty("entityName", entityName);
        jb.addProperty("entityValue", entityValue);
        jb.addProperty("keyValue", keyValue);
        jb.addProperty("keyHash", keyHash);
        jb.addProperty("assetType", assetType);
        jb.addProperty("assetId", assetId);
        jb.addProperty("extAssetId", Util.isSet(extAssetId) ? extAssetId : null);
        jb.addProperty("assetValue", assetValue);
        jb.addProperty("basis", basis);
        jb.addProperty("setId", setId);
        jb.addProperty("size", size);
        jb.addProperty("score", score);
        jb.addProperty("isActive", isActive);
        jb.addProperty("adminId", adminId);
        jb.addProperty("adminName", adminName);
        jb.addProperty("updationDate", updationDate);
        return jb;
    }


}
