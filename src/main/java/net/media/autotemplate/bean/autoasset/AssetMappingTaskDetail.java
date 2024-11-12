package net.media.autotemplate.bean.autoasset;

import com.google.gson.annotations.SerializedName;

public class AssetMappingTaskDetail {
    @SerializedName("entity_name")
    private final String entityName;
    @SerializedName("entity_value")
    private final String entityValue;
    @SerializedName("asset_value")
    private final String assetValue;
    @SerializedName("set_id")
    private final int setId;

    public AssetMappingTaskDetail(String entityName, String entityValue, String assetValue, int setId) {
        this.entityName = entityName;
        this.entityValue = entityValue;
        this.assetValue = assetValue;
        this.setId = setId;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public String getAssetValue() {
        return assetValue;
    }

    public int getSetId() {
        return setId;
    }

}
