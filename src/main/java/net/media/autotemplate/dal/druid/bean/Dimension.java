package net.media.autotemplate.dal.druid.bean;

import java.util.HashMap;
import java.util.Map;

// TODO: 03/05/24 revisit -> can we maintain this in DB?
// make frontend names similar to that of analytics name
public enum Dimension {
    TEMPLATE("Template", "Template"),
    DOMAIN("Domain", "Domain"),
    ADTAG("Ad Tag", "AdTag"),
    GLOBAL_ADTAG("Ad Tag", "Global_AdTag"),
    AT_STATE("Auto Template State", "Auto Template State"),
    CUSTOMER("Customer", "Customer"),
    GLOBAL_CUSTOMER("Customer", "Global_Customer"),
    PARTNER("Partner", "Partner"),
    URL("URL", "URL"),
    FRAMEWORK("Framework", "Framework"),
    CREATIVE_SIZE("Template Size", "Template Size"),
    PORTFOLIO("Portfolio", "Portfolio"),
    GLOBAL_PORTFOLIO("Portfolio", "Global_Portfolio"),

    ADVERTISER("Bdata - Advertiser", "Advertiser"),
    ADDOMAIN("Madmax Advertiser Domain", "AdDomain"),
    CAMPAIGN("Campaign", "Campaign"),
    ADGROUP("AdGroup", "AdGroup"),
    AD("Ad Id", "Ad"),
    ITYPE("Itype", "IType"),
    SELLER_TAG("Seller Tag Id", "Seller_Tag"),
    SIZE("Template Size", "Template_Size"),
    IMAGE_ID("AUTO ASSET IMAGE ID", "Image_ID"),
    C2A_ID("AUTO ASSET C2A ID", "C2A_ID"),
    PROVIDER_TOPIC("Provider Topic", "Provider_Topic"),
    DISPLAY_TOPIC("Display Topic", "Display_Topic"),
    DEMAND_BASIS("Demand basis", "Demand_Basis"),
    MULTI_KWD_STRATEGY("Multi-Kwd Strategy", "Multi-Kwd_Strategy"),
    BIDDER("Bidder", "Bidder"),
    CAMPAIGN_TYPE("Campaign Type", "Campaign_type"),
    PUBLISHER_DOMAIN("Publisher Domain", "Publisher_Domain"),
    CUSTOMER_HB("Customer (HB)", "Customer_HB"),
    MAX_INTEGRATION_TYPE("Max Integration Type", "Max_Integration_Type"),
    AUTO_ASSET_TITLE_ID("AUTO ASSET TITLE ID", "Auto_Asset_Title_Id"),
    KEYWORD_CLUSTER("Serp Cluster ID", "Serp Cluster ID"),
    SPRIG_KEYWORD_CATEGORY("Sprig Keyword Category", "Sprig Keyword Category"),
    TEMPLATE_TAC("TEMPLATE_TAC", "TEMPLATE_TAC"),
    CAMPAIGN_OBJECTIVE("Campaign Objective", "CAMPAIGN_OBJECTIVE"),

    AUTO_ASSET_TEST("AUTO_ASSET_TEST", "AUTO_ASSET_TEST"),
    AUTO_ASSET_IMAGE_TEST("AUTO ASSET IMAGE TEST", "AUTO_ASSET_IMAGE_TEST"),
    AUTO_ASSET_TITLE_TEST("AUTO ASSET TITLE TEST", "AUTO_ASSET_TITLE_TEST"),
    AUTO_ASSET_C2A_TEST("AUTO ASSET C2A TEST", "AUTO_ASSET_C2A_TEST"),
    AUTO_ASSET_IMAGE_SET_ID("AUTO ASSET IMAGE SETID", "AUTO_ASSET_IMAGE_SET_ID"),
    AUTO_ASSET_TITLE_SET_ID("AUTO ASSET TITLE SETID", "AUTO_ASSET_TITLE_SET_ID"),
    AUTO_ASSET_IMAGE_SOURCE_TYPE("AUTO ASSET IMAGE SOURCE TYPE", "AUTO_ASSET_IMAGE_SOURCE_TYPE"),
    AUTO_ASSET_TITLE_SOURCE_TYPE("AUTO ASSET TITLE SOURCE TYPE", "AUTO_ASSET_TITLE_SOURCE_TYPE"),
    AUTO_ASSET_C2A_SOURCE_TYPE("AUTO ASSET C2A SOURCE TYPE", "AUTO_ASSET_C2A_SOURCE_TYPE"),
    TEMPLATE_TYPE("Template Type", "TEMPLATE_TYPE"),
    SUB_TEMPLATE_TYPE("Sub Template Type", "SUB_TEMPLATE_TYPE");

    private String analyticsName;
    private String frontendName;

    Dimension(String analyticsName, String frontendName) {
        this.analyticsName = analyticsName;
        this.frontendName = frontendName;
    }

    public String getAnalyticsName() {
        return analyticsName;
    }

    public String getFrontendName() {
        return frontendName;
    }

    private static Map<String, Dimension> analyticsNameToDimensionMap = new HashMap<String, Dimension>(){{
        for (Dimension dimension : Dimension.values()) {
            this.put(dimension.getAnalyticsName(), dimension);
        }
    }};

    public static Dimension getDimensionFromAnalyticsName(String analyticsName) {
        Dimension dimension = analyticsNameToDimensionMap.get(analyticsName);
        return dimension ;
    }

    public static Dimension getDimensionFromName(String dimension) {
        return valueOf(dimension.toUpperCase());
    }
}
