package net.media.autotemplate.bean;

import com.google.gson.annotations.SerializedName;

public class BulkData {
    @SerializedName("supply")
    private String supply;
    @SerializedName("supplyId")
    private String supplyId;
    @SerializedName("demand")
    private String demand;
    @SerializedName("demandId")
    private String demandId;
    @SerializedName("templateIds")
    private String templateIds;
    @SerializedName("templateSizes")
    private String templateSizes;
    @SerializedName("systemPageType")
    private String systemPageType;
    @SerializedName("type")
    private String creativeType;
    @SerializedName("mappingType")
    private String mappingType;


    public BulkData(String supply, String supplyId, String demand, String demandId, String templateIds, String templateSizes, String systemPageType, String creativeType, String mappingType) {
        this.supply = supply;
        this.supplyId = supplyId;
        this.demand = demand;
        this.demandId = demandId;
        this.templateIds = templateIds;
        this.templateSizes = templateSizes;
        this.systemPageType = systemPageType;
        this.creativeType = creativeType;
        this.mappingType = mappingType;
    }
    public String getSupply() { return supply.toUpperCase(); }

    public String getSupplyId() {
        return supplyId;
    }

    public String getDemand() {
        return demand.toUpperCase();
    }

    public String getDemandId() {
        return demandId;
    }

    public String getTemplateIds() {
        return templateIds;
    }

    public String getTemplateSizes() {
        return templateSizes;
    }

    public String getSystemPageType() {
        return systemPageType;
    }

    public void setSupplyId(String supplyId) {
        this.supplyId = supplyId;
    }
    public String getCreativeType() {
        return creativeType;
    }

    public String getMappingType() {
        return mappingType;
    }

    public String getMappingInputString(){
        return "ROW = [ "
                + getSupply() + ", "
                + getSupplyId() + ", "
                + getDemand() + ", "
                + getDemandId() + ", "
                + getTemplateIds() + ", "
                + getMappingType() + ", "
                + getTemplateSizes() + ", "
                + getSystemPageType() + " ]";
    }

    public String getBlockingInputString() {
        return "ROW = [ "
                + getSupply() + ", "
                + getSupplyId() + ", "
                + getDemand() + ", "
                + getDemandId() + ", "
                + getTemplateIds() + ", "
                + getCreativeType() + ", "
                + getTemplateSizes() + ", "
                + getSystemPageType() + " ]";
    }
}
