package net.media.autotemplate.enums;

import java.util.HashMap;
import java.util.Map;

public enum ATRequestState {
    UNPROCESSED("UNPROCESSED"),
    PROCESSING("PROCESSING"),
    SUCCESS("SUCCESS"),
    ERROR("ERROR"),
    FAILED("FAILED");


    String dbName;

    ATRequestState(String dbName) {
        this.dbName = dbName;
    }

    private static Map<String, ATRequestState> DB_NAME_MAP = new HashMap<String, ATRequestState>(){{
        for (ATRequestState state : ATRequestState.values()) {
            this.put(state.getDbName(), state);
        }
    }};

    public String getDbName() {
        return dbName;
    }

    public static ATRequestState getATRequestStateFromDbName(String dbName) {
        return DB_NAME_MAP.get(dbName);
    }
}
