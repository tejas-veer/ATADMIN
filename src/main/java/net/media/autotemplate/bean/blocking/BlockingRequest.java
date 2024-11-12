package net.media.autotemplate.bean.blocking;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlockingRequest {

    @SerializedName("payload")
    private final List<BlockingInfo> payloads;
    @SerializedName("buSelected")
    private final String buSelected;

    public BlockingRequest(List<BlockingInfo> payloads, String buSelected) {
        this.payloads = payloads;
        this.buSelected = buSelected;
    }

    public List<BlockingInfo> getPayloads() {
        return payloads;
    }

    public String getBuSelected() {
        return buSelected;
    }
}