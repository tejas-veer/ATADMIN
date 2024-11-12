package net.media.autotemplate.bean;

import com.google.gson.JsonObject;
import net.media.autotemplate.enums.ATRequestType;

/**
 * Created by Jatin Warade
 * on 08-March-2023
 * at 18:49
 */

public class ATRequest {
    private final long requestId;
    private final ATRequestType requestType;
    private long adminId;
    private final String adminName;
    private final int rowsProcessed;
    private final int rowsGenerated;
    private final String hash;
    private final String creationDate;
    private final int isActive;
    private final double progressPercentage;
    private final int totalCount;


    public ATRequest(long requestId, ATRequestType requestType, long adminId, String adminName, int isActive, int rowsProcessed, int rowsGenerated, String hash, String creationDate, int totalCount) {
        this.requestId = requestId;
        this.requestType = requestType;
        this.adminId = adminId;
        this.adminName = adminName;
        this.rowsProcessed = rowsProcessed;
        this.rowsGenerated = rowsGenerated;
        this.hash = hash;
        this.creationDate = creationDate;
        this.isActive = isActive;
        this.progressPercentage = rowsProcessed == rowsGenerated ? 100 : Math.floor((rowsProcessed * 100.00) / Math.max(rowsGenerated, 1));
        this.totalCount = totalCount;
    }

    public long getRequestId() {
        return requestId;
    }

    public ATRequestType getRequestType() {
        return requestType;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int isActive() {
        return isActive;
    }


    public JsonObject getAsJson() {
        JsonObject jb = new JsonObject();
        jb.addProperty("requestId", requestId);
        jb.addProperty("requestType", requestType.getDbName());
        jb.addProperty("adminId", adminId);
        jb.addProperty("adminName", adminName);
        jb.addProperty("rowsGenerated", rowsGenerated);
        jb.addProperty("rowsProcessed", rowsProcessed);
        jb.addProperty("hash", hash);
        jb.addProperty("creationDate", creationDate);
        jb.addProperty("isActive", isActive);
        jb.addProperty("progressPercentage", progressPercentage);
        jb.addProperty("totalCount", totalCount);
        return jb;
    }
}
