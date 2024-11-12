package net.media.autotemplate.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jatin Warade
 * on 04-July-2022
 * at 20:49
 */
public enum SystemPageType {
    KEYWORD_ONLY("keywords-only", 1),
    CM_NATIVE("cm-native", 2);

    String name;
    int id;

    SystemPageType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    private static Map<String, SystemPageType> nameToSPTMap = new HashMap() {{
        for (SystemPageType systemPageType : SystemPageType.values()) {
            this.put(systemPageType.getName(), systemPageType);
        }
    }};

    private static Map<String, SystemPageType> idToSPTMap = new HashMap() {{
        for (SystemPageType systemPageType : SystemPageType.values()) {
            this.put(systemPageType.getId(), systemPageType);
        }
    }};

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static SystemPageType getSPTFromName(String name) {
        return nameToSPTMap.get(name);
    }

    public static SystemPageType getSPTFromId(int id) {
        return idToSPTMap.get(id);
    }
}
