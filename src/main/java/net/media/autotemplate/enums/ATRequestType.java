package net.media.autotemplate.enums;

import net.media.autotemplate.bean.ATRequestDetail;
import net.media.autotemplate.util.TemplateBulkCronUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public enum ATRequestType {
    STABLE_DIFFUSION("STABLE_DIFFUSION"),
    ASSET_MAPPING("ASSET_MAPPING"),
    TITLE_GENERATION("TITLE_GENERATION"),
    TEMPLATE_MAPPING("MAPPING", TemplateBulkCronUtil::insertMapping),
    TEMPLATE_UNMAPPING("TEMPLATE_UNMAPPING", TemplateBulkCronUtil::insertUnmapping),
    TEMPLATE_BLOCKING("BLOCKING", TemplateBulkCronUtil::insertBlocking);

    private String dbName;
    private Consumer<List<ATRequestDetail>> atRequestDetailConsumer;

    ATRequestType(String dbName) {
        this.dbName = dbName;
    }

    ATRequestType(String dbName, Consumer<List<ATRequestDetail>> atRequestDetailConsumer) {
        this.dbName = dbName;
        this.atRequestDetailConsumer = atRequestDetailConsumer;
    }

    public String getDbName() {
        return dbName;
    }

    public Consumer<List<ATRequestDetail>> getAtRequestDetailConsumer() {
        return atRequestDetailConsumer;
    }

    private static final Map<String, ATRequestType> DB_NAME_MAP = new HashMap<String, ATRequestType>() {{
        for (ATRequestType type : ATRequestType.values()) {
            this.put(type.getDbName(), type);
        }
    }};

    public static ATRequestType getATRequestTypeFromDbName(String dbName) {
        return DB_NAME_MAP.get(dbName);
    }
}