package net.media.autotemplate.bean;

import com.google.gson.annotations.SerializedName;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.util.Util;


public class SupplyDemandHierarchy {
    private static final String SEPARATOR = "$";

    //supply-side data
    @SerializedName("supply")
    private final CreativeConstants.Level supplyLevel;
    @SerializedName("supplyId")
    private final String supplyValue;

    //demand-side data
    @SerializedName("demand")
    private final CreativeConstants.Level demandLevel;
    @SerializedName("demandId")
    private final String demandValue;

    //for frontend
    public SupplyDemandHierarchy(String supplyLevel, String supplyValue, String demandLevel, String demandValue) {

        this.supplyLevel = CreativeConstants.Level.getLevelFromEntityType(CreativeConstants.EntityType.SUPPLY, supplyLevel);
        this.supplyValue = supplyValue;

        this.demandLevel = CreativeConstants.Level.getLevelFromEntityType(CreativeConstants.EntityType.DEMAND, demandLevel);
        this.demandValue = demandValue;
    }

    //for backend
    public SupplyDemandHierarchy(String combinedHierarchy, String combinedEntity) {
        Pair<CreativeConstants.Level, CreativeConstants.Level> supplyDemandLevels = getHierarchyLevels(combinedHierarchy);
        this.supplyLevel = supplyDemandLevels.first;
        this.demandLevel = supplyDemandLevels.second;
        Pair<String, String> supplyDemandValues = getHierarchyValues(combinedEntity);
        this.supplyValue = supplyDemandValues.first;
        this.demandValue = supplyDemandValues.second;
    }

    private Pair<CreativeConstants.Level, CreativeConstants.Level> getHierarchyLevels(String combinedHierarchy) {
        CreativeConstants.Level supplyLevel;
        CreativeConstants.Level demandLevel;
        String[] levels = combinedHierarchy.split("\\" + SEPARATOR);
        if(levels.length > 1) {
            // both supply and demand present in combinedHierarchy
            supplyLevel = CreativeConstants.Level.getLevelFromEntityType(CreativeConstants.EntityType.SUPPLY, levels[0]);
            demandLevel = CreativeConstants.Level.getLevelFromEntityType(CreativeConstants.EntityType.DEMAND, levels[1]);
        }
        else {
            // one of supply, demand present in combinedHierarchy
            CreativeConstants.Level supply = CreativeConstants.Level.getLevelFromEntityType(CreativeConstants.EntityType.SUPPLY, levels[0]);
            if(supply.equals(CreativeConstants.Level.GLOBAL)) {
                CreativeConstants.Level demand = CreativeConstants.Level.getLevelFromEntityType(CreativeConstants.EntityType.DEMAND, levels[0]);
                supplyLevel = supply;
                demandLevel = demand;
            }
            else {
                supplyLevel = supply;
                demandLevel = CreativeConstants.Level.GLOBAL;
            }
        }
        return new Pair<>(supplyLevel, demandLevel);
    }

    private Pair<String, String> getHierarchyValues(String combinedEntity) {
        String supplyValue;
        String demandValue;
        String[] entities = combinedEntity.split("\\" + SEPARATOR);
        if(entities.length > 1) {
            supplyValue = entities[0];
            demandValue = entities[1];
        }
        else {
            if(this.supplyLevel.equals(CreativeConstants.Level.GLOBAL)) {
                supplyValue = "";
                if(this.demandLevel.equals(CreativeConstants.Level.GLOBAL)) {
                    demandValue = "";
                }
                else {
                    demandValue = entities[0];
                }
            }
            else {
                supplyValue = entities[0];
                demandValue = "";
            }
        }
        return new Pair<>(supplyValue, demandValue);
    }

    public String getHierarchyLevel() {
        if(!isDemandValid()) {
            return this.supplyLevel.name();
        }

        if(!isSupplyValid()) {
            return CreativeConstants.Level.GLOBAL.name() + SEPARATOR + this.demandLevel.name();
        }

        return this.supplyLevel.name() + SEPARATOR + this.demandLevel.name();
    }

    public String getEntityValue() {
        if(!isDemandValid()) {
            return this.supplyValue;
        }

        if(!isSupplyValid()) {
            return "GLOBAL"  + SEPARATOR + this.demandValue;
        }

        return this.supplyValue + SEPARATOR + this.demandValue;
    }

    public boolean isSupplyValid() {
        return !this.supplyLevel.equals(CreativeConstants.Level.GLOBAL) && Util.isStringSet(this.supplyValue);
    }

    public boolean isDemandValid() {
        return !this.demandLevel.equals(CreativeConstants.Level.GLOBAL) && Util.isStringSet(this.demandValue);
    }

    public CreativeConstants.Level getSupplyLevel() {
        return this.supplyLevel;
    }

    public String getSupplyValue() {
        return this.supplyValue;
    }

    public CreativeConstants.Level getDemandLevel() {
        return this.demandLevel;
    }

    public String getDemandValue() {
        return this.demandValue;
    }
}
