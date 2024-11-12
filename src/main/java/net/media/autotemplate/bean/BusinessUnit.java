package net.media.autotemplate.bean;

import net.media.autotemplate.dal.druid.bean.AnalyticsType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jatin Warade
 * on 04-July-2022
 * at 20:52
 */
public enum BusinessUnit {
    CM("CM", 1, SystemPageType.KEYWORD_ONLY, AnalyticsType.CM),
    MAX("MAX", 2, SystemPageType.CM_NATIVE, AnalyticsType.MAX);

    private final String name;
    private final int id;
    private final SystemPageType defaultSystemPage;
    private final AnalyticsType analyticsType;

    BusinessUnit(String name, int id, SystemPageType defaultSystemPage, AnalyticsType analyticsType) {
        this.name = name;
        this.id = id;
        this.defaultSystemPage = defaultSystemPage;
        this.analyticsType = analyticsType;
    }

    private static Map<String, BusinessUnit> nameToBUMap = new HashMap() {{
        for (BusinessUnit businessUnit : BusinessUnit.values()) {
            this.put(businessUnit.getName(), businessUnit);
        }
    }};

    private static Map<String, BusinessUnit> idToBUMap = new HashMap() {{
        for (BusinessUnit businessUnit : BusinessUnit.values()) {
            this.put(businessUnit.getId(), businessUnit);
        }
    }};

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public SystemPageType getDefaultSystemPage() {
        return defaultSystemPage;
    }

    public static BusinessUnit getBUFromName(String name) {
        return nameToBUMap.get(name.toUpperCase());
    }

    public static BusinessUnit getBUFromId(int id) {
        return idToBUMap.get(id);
    }

    public AnalyticsType getAnalyticsType() {
        return analyticsType;
    }
}
