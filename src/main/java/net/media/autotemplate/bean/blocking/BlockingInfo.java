package net.media.autotemplate.bean.blocking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.media.autotemplate.bean.SupplyDemandHierarchy;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.util.Util;

/*
    Created by shubham-ar
    on 9/2/18 5:20 PM   
*/
public class BlockingInfo {

    @Expose
    @SerializedName("creativeId")
    private final String creative;

    @Expose
    @SerializedName("size")
    private final String size;

    @Expose
    @SerializedName("type")
    private final CreativeConstants.Type type;

    @Expose
    @SerializedName("status")
    private final CreativeConstants.Status status;

    private final SupplyDemandHierarchy supplyDemandHierarchy;

    private String creationDate;
    private String adminName;

    public BlockingInfo(String creative, CreativeConstants.Type type, String size, CreativeConstants.Status status, String level, String entity, String creationDate, String adminName) {
        this.creative = creative;
        this.size = size;
        this.type = type;
        this.status = status;
        this.supplyDemandHierarchy = new SupplyDemandHierarchy(level, entity);
        this.creationDate = creationDate;
        this.adminName = adminName;
    }

    public BlockingInfo(String creative, CreativeConstants.Type type, String size, CreativeConstants.Status status, String level, String entity) {
        this(creative, type, size, status, level, entity, "", "");
    }

    public BlockingInfo(String entity, String creative, CreativeConstants.Type type, String size, CreativeConstants.Status status, CreativeConstants.Level level) {
        this(creative, type, size, status, level.name(), entity);
    }

    public SupplyDemandHierarchy getSupplyDemandHierarchy() {
        return this.supplyDemandHierarchy;
    }

    public String getCreative() {
        return creative;
    }

    public CreativeConstants.Type getType() {
        return type;
    }

    public CreativeConstants.Status getStatus() {
        return status;
    }

    public String getLevel() {
        return this.supplyDemandHierarchy.getHierarchyLevel();
    }

    public String getEntity() {
        return this.supplyDemandHierarchy.getEntityValue();
    }

    public String getSize() {
        return size;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getMessage() {
        if (status.equals(CreativeConstants.Status.B)) {
            return type.name() + " blocked for " + getLevel();
        }
        // what's the use of this
        return type.name() + " unblocked for " + getLevel();
    }

    @Override
    public String toString() {
        return Util.getGson().toJson(this);
    }
}
