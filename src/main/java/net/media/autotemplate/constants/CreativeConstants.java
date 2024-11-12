package net.media.autotemplate.constants;

import net.media.autotemplate.bean.AdtagInfo;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.bean.mapping.SeasonalTemplate;
import net.media.autotemplate.bean.mapping.ZeroColorTemplate;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.database.RowMapper;

import java.util.*;
import java.util.function.Function;

/*
    Created by shubham-ar
    on 5/12/17 4:48 PM   
*/
public class CreativeConstants {
    /*
                Created by shubham-ar
                on 5/12/17 4:31 PM
            */
    public static final String GLOBAL_ENTITY = "GLOBAL";

    public enum EntityType {
        SUPPLY,
        DEMAND,
        GLOBAL,
        ;
    }

    public enum Level {
        GLOBAL(EntityType.GLOBAL, e -> CreativeConstants.GLOBAL_ENTITY, e -> CreativeConstants.GLOBAL_ENTITY, null, null),

        PARTNER(EntityType.SUPPLY, Entity::getPartnerId, AdtagInfo::getPartnerId, Dimension.PARTNER, "partner_id"),
        CUSTOMER(EntityType.SUPPLY, Entity::getCustomerId, AdtagInfo::getCustomerId, Dimension.CUSTOMER, "customer_id"),
        GLOBAL_CUSTOMER(EntityType.SUPPLY, Entity::getCustomerId, AdtagInfo::getCustomerId, Dimension.GLOBAL_CUSTOMER, "customer_id"),
        DOMAIN(EntityType.SUPPLY, Entity::getDomain, AdtagInfo::getDomain, Dimension.DOMAIN, "domain"),
        ADTAG(EntityType.SUPPLY, Entity::getAdtagId, e -> Objects.nonNull(e.getAdtagId()) ? String.valueOf(e.getAdtagId()) : null, Dimension.ADTAG, "adtag_id"),
        GLOBAL_ADTAG(EntityType.SUPPLY, Entity::getAdtagId, e -> Objects.nonNull(e.getAdtagId()) ? String.valueOf(e.getAdtagId()) : null, Dimension.GLOBAL_ADTAG, "adtag_id"),
        ENTITY(EntityType.SUPPLY, e -> String.valueOf(e.getEntityId()), e -> null, null, "id"),
        PORTFOLIO(EntityType.SUPPLY, null, null, Dimension.PORTFOLIO, null),
        GLOBAL_PORTFOLIO(EntityType.SUPPLY, null, null, Dimension.GLOBAL_PORTFOLIO, null),
        ITYPE(EntityType.SUPPLY, null, null, Dimension.ITYPE, null),

        CSID$DM$AT(EntityType.SUPPLY, null, null, null, null),
        IT$DM$ST$AD(EntityType.SUPPLY, null, null, null, null),

        //Todo 05/12/2023 : EntityType should be Demand but if demand then hierarchy in DB will be GLOBAL$IT$AD but we want to map on IT$AD. [improve]
        IT$AD(EntityType.SUPPLY, null, null, null, null),

        AD(EntityType.DEMAND, null, null, Dimension.AD, null),
        CAMPAIGN(EntityType.DEMAND, null, null, Dimension.CAMPAIGN, null),
        ADGROUP(EntityType.DEMAND, null, null, Dimension.ADGROUP, null),
        ADVERTISER(EntityType.DEMAND, null, null, Dimension.ADVERTISER, null),
        ADDOMAIN(EntityType.DEMAND, null, null, Dimension.ADDOMAIN, null);

        private final Function<Entity, String> getter;
        private final Function<AdtagInfo, String> adtaginfoGetter;
        private final Dimension dimension;
        private final String databaseColumn;
        private final EntityType entityType;

        private static final EnumMap<EntityType, HashSet<String>> entityTypeToLevel = new EnumMap<EntityType, HashSet<String>>(EntityType.class) {{
            for (Level level : Level.values()) {
                this.computeIfPresent(level.entityType, (key, value) -> new HashSet<String>(value) {{
                    add(level.name());
                }});
                this.putIfAbsent(level.entityType, new HashSet<>(Collections.singletonList(level.name())));
            }
        }};

        Level(EntityType entityType, Function<Entity, String> getter, Function<AdtagInfo, String> adtaginfoGetter, Dimension dimension, String databaseColumn) {
            this.getter = getter;
            this.adtaginfoGetter = adtaginfoGetter;
            this.dimension = dimension;
            this.databaseColumn = databaseColumn;
            this.entityType = entityType;
        }

        @Override
        public String toString() {
            return this.name();
        }

        public Dimension getDimension() {
            return dimension;
        }

        public Function<Entity, String> getter() {
            return getter;
        }

        public Function<AdtagInfo, String> AdtagInfoGetter() {
            return adtaginfoGetter;
        }

        public String getDatabaseColumn() {
            return databaseColumn;
        }

        public static boolean isLevelExistsOnEntityType(EntityType entityType, String level){
            return entityTypeToLevel.containsKey(entityType) && entityTypeToLevel.get(entityType).contains(level);
        }

        public static Level getLevelFromEntityType(EntityType entityType, String level) {
            level = level.toUpperCase();
            if (entityTypeToLevel.containsKey(entityType)) {
                if (entityTypeToLevel.get(entityType).contains(level)) {
                    return Level.valueOf(level);
                }
                return GLOBAL;
            }
            return GLOBAL;
        }
    }

    /*
            Created by shubham-ar
            on 4/12/17 8:07 PM
        */
    public enum Status {
        B, W, NA;

        private static final HashMap<String, Status> nameToStatusEnumMap = new HashMap<String, Status>() {{
            for (Status value : Status.values()) {
                this.put(value.name(), value);
            }
        }};

        public static Status getStatusEnumFromName(String status) {
            return nameToStatusEnumMap.getOrDefault(status, NA);
        }
    }

    /*
            Created by shubham-ar
            on 4/12/17 3:29 PM
        */
    public enum Type {
        TEMPLATE(Dimension.TEMPLATE, "Template"),
        FRAMEWORK(Dimension.FRAMEWORK, "Framework"),
        ALL(null, null),
        NULL(null, null);

        private final Dimension dimension;
        private final String dimensionName;

        Type(Dimension dimension, String dimensionName) {
            this.dimension = dimension;
            this.dimensionName = dimensionName;
        }

        private static final HashMap<String, Type> nameToTypeEnumMap = new HashMap<String, Type>() {{
            for (Type value : Type.values()) {
                this.put(value.name(), value);
            }
        }};

        public Dimension getDimension() {
            return this.dimension;
        }

        public String getDimensionName() {
            return dimensionName;
        }

        public static Type getTypeEnumFromName(String type) {

            return nameToTypeEnumMap.getOrDefault(type, NULL);
        }
    }


    public enum MappingType {
        NULL(-1, null),
        SEASONAL(3, rs -> new SeasonalTemplate(rs.getString("entity"), rs.getString("hierarchy_level"), rs.getString("template_id"), rs.getString("template_size"), rs.getString("start_date"), rs.getString("expiry_date"), rs.getInt("business_unit_id"), rs.getInt("system_page_type_id"), rs.getString("updation_date"), rs.getString("admin_name"))),
        MANUAL(1, rs -> new MappingTemplate(rs.getString("entity"), rs.getString("hierarchy_level"), rs.getString("template_id"), rs.getString("template_size"), rs.getInt("business_unit_id"), rs.getInt("system_page_type_id"), rs.getString("updation_date"), rs.getString("admin_name"))),
        ZERO_COLOR(1, rs -> new ZeroColorTemplate(rs.getString("entity"), rs.getString("hierarchy_level"), rs.getString("template_id"), rs.getString("framework_id"), rs.getString("template_size"), rs.getInt("business_unit_id"), rs.getInt("system_page_type_id"))),
        ADWISER(4, null);

        private final Integer setId;
        private final RowMapper<MappingTemplate> rowMapper;

        MappingType(Integer setId, RowMapper<MappingTemplate> rowMapper) {
            this.setId = setId;
            this.rowMapper = rowMapper;
        }

        private static final HashMap<String, MappingType> nameToMappingTypeEnumMap = new HashMap<String, MappingType>() {{
            for (MappingType value : MappingType.values()) {
                this.put(value.name(), value);
            }
        }};

        public Integer getSetId() {
            return setId;
        }

        public RowMapper<MappingTemplate> getRowMapper() {
            return rowMapper;
        }

        public static MappingType getMappingTypeEnumFromName(String mappingType) {
            return nameToMappingTypeEnumMap.getOrDefault(mappingType, NULL);
        }
    }

    public enum ServingStatus {
        ENABLED,
        DISABLED,
        BLOCKED,
        NA
    }



}
