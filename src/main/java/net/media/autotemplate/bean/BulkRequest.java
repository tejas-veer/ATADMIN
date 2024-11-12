package net.media.autotemplate.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BulkRequest {
    @SerializedName("buSelected")
    private String buSelected;
    @SerializedName("inputPayload")
    private List<BulkData> bulkDataList;

    public BulkRequest(String buSelected, List<BulkData> bulkDataList) {
        this.buSelected = buSelected;
        this.bulkDataList = bulkDataList;
    }

    public String getBuSelected() {
        return buSelected;
    }

    public List<BulkData> getBulkDataList() {
        return bulkDataList;
    }
}
