package net.media.autotemplate.bean;

import net.media.autotemplate.dal.druid.bean.Dimension;

import java.util.*;

// TODO: 03/05/24 need to refactored - every time we add new dimension we have to add entry inthis enum which has to be stopped
public enum ExpandedDimension {
    ALL_ASSETS("All Assets", null, Arrays.asList(Dimension.TEMPLATE, Dimension.CREATIVE_SIZE, Dimension.AD, Dimension.DISPLAY_TOPIC, Dimension.AUTO_ASSET_TITLE_ID, Dimension.IMAGE_ID, Dimension.C2A_ID)),
    TITLE("Title", Dimension.AUTO_ASSET_TITLE_ID, Arrays.asList(Dimension.DISPLAY_TOPIC)),
    TEMPLATE("Template", Dimension.TEMPLATE, Arrays.asList(Dimension.CREATIVE_SIZE)),

    BIDDER("Bidder", Dimension.BIDDER),
    IMAGE("Image", Dimension.IMAGE_ID),
    C2A("C2A", Dimension.C2A_ID),
    PROVIDER_TOPIC("Provider Topic", Dimension.PROVIDER_TOPIC),
    DISPLAY_TOPIC("Display Topic", Dimension.DISPLAY_TOPIC),
    DEMAND_BASIS("Demand basis", Dimension.DEMAND_BASIS),
    DOMAIN("Domain", Dimension.PUBLISHER_DOMAIN),
    SELLER_TAG("Seller Tag Id", Dimension.SELLER_TAG),
    ADVERTISER("Advertiser", Dimension.ADVERTISER),
    CAMPAIGN("Campaign", Dimension.CAMPAIGN),
    ADGROUP("AdGroup", Dimension.ADGROUP),
    KEYWORD_CATEGORY("Sprig Keyword Category",Dimension.SPRIG_KEYWORD_CATEGORY),
    KEYWORD_CLUSTER("Serp Cluster ID",Dimension.KEYWORD_CLUSTER),
    AD("Ad Id", Dimension.AD),
    TEMPLATE_TAC("TEMPLATE_TAC", Dimension.TEMPLATE_TAC),
    CAMPAIGN_OBJECTIVE("Campaign Objective", Dimension.CAMPAIGN_OBJECTIVE),

    AUTO_ASSET_TEST("AUTO_ASSET_TEST", Dimension.AUTO_ASSET_TEST),
    AUTO_ASSET_IMAGE_TEST("AUTO ASSET IMAGE TEST", Dimension.AUTO_ASSET_IMAGE_TEST),
    AUTO_ASSET_TITLE_TEST("AUTO ASSET TITLE TEST", Dimension.AUTO_ASSET_TITLE_TEST),
    AUTO_ASSET_C2A_TEST("AUTO ASSET C2A TEST", Dimension.AUTO_ASSET_C2A_TEST),

    AUTO_ASSET_IMAGE_SET_ID("AUTO ASSET IMAGE SETID", Dimension.AUTO_ASSET_IMAGE_SET_ID),
    AUTO_ASSET_TITLE_SET_ID("AUTO ASSET TITLE SETID", Dimension.AUTO_ASSET_TITLE_SET_ID),

    AUTO_ASSET_IMAGE_SOURCE_TYPE("AUTO ASSET IMAGE SOURCE TYPE", Dimension.AUTO_ASSET_IMAGE_SOURCE_TYPE),
    AUTO_ASSET_TITLE_SOURCE_TYPE("AUTO ASSET TITLE SOURCE TYPE", Dimension.AUTO_ASSET_TITLE_SOURCE_TYPE),
    AUTO_ASSET_C2A_SOURCE_TYPE("AUTO ASSET C2A SOURCE TYPE", Dimension.AUTO_ASSET_C2A_SOURCE_TYPE),

    TEMPLATE_TYPE("Template Type", Dimension.TEMPLATE_TYPE),
    SUB_TEMPLATE_TYPE("Sub Template Type", Dimension.SUB_TEMPLATE_TYPE);;

    private final String frontendName;
    private final Dimension primaryDimension;
    private List<Dimension> expandedDimensionList;

    ExpandedDimension(String frontendName, Dimension primaryDimension, List<Dimension> expandedDimensionList) {
        this.frontendName = frontendName;
        this.primaryDimension = primaryDimension;
        this.expandedDimensionList = expandedDimensionList;
    }

    ExpandedDimension(String frontendName, Dimension primaryDimension) {
        this(frontendName, primaryDimension, new ArrayList<>());
    }


    public String getFrontendName() {
        return frontendName;
    }

    public Dimension getPrimaryDimension() {
        return primaryDimension;
    }

    public List<Dimension> getExpandedDimensionList() {
        return expandedDimensionList;
    }

    private static final Map<String, ExpandedDimension> frontendNameToExpandedDimensionMap = new HashMap<String, ExpandedDimension>() {{
        for (ExpandedDimension dimension : ExpandedDimension.values()) {
            this.put(dimension.getFrontendName(), dimension);
        }
    }};

    public static ExpandedDimension getExpandedDimension(String frontendName) {
        return frontendNameToExpandedDimensionMap.get(frontendName);
    }
}
