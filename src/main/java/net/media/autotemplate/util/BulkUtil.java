package net.media.autotemplate.util;

import net.media.autotemplate.bean.BulkData;
import net.media.autotemplate.bean.BulkRequest;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.bean.SystemPageType;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.enums.ATRequestType;
import net.media.database.DatabaseException;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BulkUtil {
    public static final String ALL_SIZE = "ALL";
    public static final String ENTITY = "ENTITY";

    public static BulkRequest getModifiedBulkRequest(BulkRequest bulkRequest) {
        List<BulkData> bulkDataList = new ArrayList<>();
        for (BulkData bulkData : bulkRequest.getBulkDataList()) {
            BulkData modifiedBulkData = new BulkData(
                    Util.getTrimmedStringIfSet(bulkData.getSupply()),
                    Util.getTrimmedStringIfSet(bulkData.getSupplyId()),
                    Util.getTrimmedStringIfSet(bulkData.getDemand()),
                    Util.getTrimmedStringIfSet(bulkData.getDemandId()),
                    getModifiedTemplateIdSizes(bulkData.getTemplateIds()),
                    getModifiedTemplateIdSizes(bulkData.getTemplateSizes()),
                    Util.getTrimmedStringIfSet(bulkData.getSystemPageType()),
                    Util.getTrimmedStringIfSet(bulkData.getCreativeType()),
                    Util.getTrimmedStringIfSet(bulkData.getMappingType())
            );
            bulkDataList.add(modifiedBulkData);
        }
        return new BulkRequest(bulkRequest.getBuSelected().trim(), bulkDataList);
    }

    public static String getModifiedTemplateIdSizes(String templateSizes) {
        if (Util.isStringSet(templateSizes)) {
            List<String> modifiedSizeList = new ArrayList<>();
            List<String> sizes = Arrays.asList(templateSizes.trim().split(ConfigConstants.PIPE_SEPARATOR));
            for (String size : sizes) {
                modifiedSizeList.add(size.trim());
            }
            return String.join("|", modifiedSizeList);
        }
        return templateSizes;
    }


    public static List<String> validTemplateSizes(List<String> tSizes) {
        List<String> templateSizes = new ArrayList<>();
        for (String size : tSizes) {
            List<String> templateSize = Arrays.asList(size.split("x"));
            if (templateSize.size() == 1) {
                templateSize = Arrays.asList(size.split("X"));
            }
            templateSizes.add(String.join("x", templateSize.get(0).trim(), templateSize.get(1).trim()));
        }
        return templateSizes;
    }

    public static List<String> getTemplateSizeListFromString(String templateSizeString, ATRequestType atRequestType) throws DatabaseException {
        List<String> templateSizes;
        if (templateSizeString.equalsIgnoreCase(ALL_SIZE)) {
             templateSizes = Collections.singletonList(
                    atRequestType == ATRequestType.TEMPLATE_UNMAPPING ? "" : ALL_SIZE
            );
        } else {
            templateSizes = Arrays.asList(templateSizeString.split(ConfigConstants.PIPE_SEPARATOR));
            templateSizes = validTemplateSizes(templateSizes);
        }
        return templateSizes;
    }

    public static boolean isTemplateValid(String template) {
        return template.chars().allMatch(Character::isDigit);
    }

    public static boolean isTemplateIdsValid(String templateIds) {
        if (templateIds.isEmpty()) {
            return false;
        } else {
            List<String> ids = Arrays.asList(templateIds.split(ConfigConstants.PIPE_SEPARATOR));
            for (String id : ids) {
                if (!isTemplateValid(id.trim())) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isSizeValid(String size) {
        List<String> splitSize = Arrays.asList(size.split("x"));
        if (splitSize.size() == 1) {
            splitSize = Arrays.asList(size.split("X"));
        }
        if (splitSize.size() == 1) {
            return false;
        } else {
            if (!splitSize.get(0).trim().chars().allMatch(Character::isDigit) || !splitSize.get(1).trim().chars().allMatch(Character::isDigit)) {
                return false;
            }
            return true;
        }
    }

    public static boolean isValidTemplateSizes(String templateSizes) {
        if (templateSizes.equalsIgnoreCase(ALL_SIZE)) {
            return true;
        }
        if (templateSizes.isEmpty()) {
            return false;
        } else {
            List<String> sizes = Arrays.asList(templateSizes.split(ConfigConstants.PIPE_SEPARATOR));
            for (String size : sizes) {
                if (!isSizeValid(size)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isValidSystemPageType(String systemPageType) {
        SystemPageType spt = SystemPageType.getSPTFromName(systemPageType);
        if (Util.isSet(spt)) {
            return true;
        }
        return false;
    }

    public static boolean isDemandHierarchyLevelValid(BulkData bulkData) {
        if (!bulkData.getDemand().isEmpty()) {
            return CreativeConstants.Level.isLevelExistsOnEntityType(CreativeConstants.EntityType.DEMAND, bulkData.getDemand().toUpperCase());
        }
        return true;
    }

    public static boolean isSupplyHierarchyLevelValid(BulkData bulkData) {
        if (!bulkData.getSupply().isEmpty()) {
            return CreativeConstants.Level.isLevelExistsOnEntityType(CreativeConstants.EntityType.SUPPLY, bulkData.getSupply().toUpperCase());
        }
        return true;
    }

    public static boolean isCorrectHierarchyPair(BulkData bulkData) {
        if ((bulkData.getSupply().isEmpty() && !bulkData.getSupplyId().isEmpty()) ||
                (!bulkData.getSupply().isEmpty() && bulkData.getSupplyId().isEmpty()) ||
                (bulkData.getDemand().isEmpty() && !bulkData.getDemandId().isEmpty()) ||
                (!bulkData.getDemand().isEmpty() && bulkData.getDemandId().isEmpty())) {
            return false;
        }
        return true;
    }

    public static boolean isEntityValid(BulkData bulkData) {
        List<String> domainStag = Arrays.asList(bulkData.getSupplyId().split(ConfigConstants.PIPE_SEPARATOR));
        return domainStag.size() == 2;
    }

    public static boolean isMappingTypeValid(String mappingType) {
        CreativeConstants.MappingType mType = CreativeConstants.MappingType.getMappingTypeEnumFromName(mappingType);
        return mType.equals(CreativeConstants.MappingType.ADWISER) || mType.equals(CreativeConstants.MappingType.MANUAL);
    }

    public static boolean isTypeValid(String type) {
        CreativeConstants.Type cType = CreativeConstants.Type.getTypeEnumFromName(type);
        return cType.equals(CreativeConstants.Type.TEMPLATE) || cType.equals(CreativeConstants.Type.FRAMEWORK);
    }


    public static Pair<Integer, String> getRowParamsValidity(BulkData bulkData) {
        int status = HttpStatus.SC_OK;
        String errorMsg = "";
        if (!isCorrectHierarchyPair(bulkData)) {
            errorMsg = "Any one of missing from Hierarchy Level & Entity value . \n";
        } else if (!isDemandHierarchyLevelValid(bulkData)) {
            errorMsg = "Wrong Demand Hierarchy Level. \n";
        } else if (!isSupplyHierarchyLevelValid(bulkData)) {
            errorMsg = "Wrong Supply Hierarchy Level \n";
        } else if (bulkData.getSupply().equalsIgnoreCase(ENTITY) && !isEntityValid(bulkData)) {
            errorMsg = "Domain and AdTag both Required for ENTITY.\n";
        } else if (!isTemplateIdsValid(bulkData.getTemplateIds())) {
            errorMsg = "Template Ids are empty or not in correct format. \n";
        } else if (!isValidTemplateSizes(bulkData.getTemplateSizes())) {
            errorMsg = "Template sizes are empty or not in correct format. \n";
        } else if (!isValidSystemPageType(bulkData.getSystemPageType())) {
            errorMsg = "System Page Type is not correct. \n";
        }
        if (Util.isStringSet(errorMsg)) {
            status = HttpStatus.SC_NOT_ACCEPTABLE;
        }
        return new Pair<>(status, errorMsg);
    }

    public static Pair<Integer, String> getMappingParamsValidity(BulkData bulkData) {
        if (!isMappingTypeValid(bulkData.getMappingType().toUpperCase())) {
            return new Pair<>(HttpStatus.SC_NOT_ACCEPTABLE, "Mapping Type is not correct. \n");
        }
        return getRowParamsValidity(bulkData);
    }

    public static Pair<Integer, String> getBlockingParamsValidity(BulkData bulkData) {
        if (!isTypeValid(bulkData.getCreativeType().toUpperCase())) {
            return new Pair<>(HttpStatus.SC_NOT_ACCEPTABLE, "Creative Type is not correct. \n");
        }
        return getRowParamsValidity(bulkData);
    }

    public static Pair<Integer, String> getMappingRowValidity(BulkData bulkData) {
        Pair<Integer, String> mappingRowValidity = getMappingParamsValidity(bulkData);
        int status = mappingRowValidity.first;
        String errorMsg = "";
        if (status != HttpStatus.SC_OK)
            errorMsg = mappingRowValidity.second + bulkData.getMappingInputString();
        return new Pair<>(status, errorMsg);
    }


    public static Pair<Integer, String> getBlockingRowValidity(BulkData bulkData) {
        Pair<Integer, String> mappingRowValidity = getBlockingParamsValidity(bulkData);
        int status = mappingRowValidity.first;
        String errorMsg = "";
        if (status != HttpStatus.SC_OK)
            errorMsg = mappingRowValidity.second + bulkData.getBlockingInputString();
        return new Pair<>(status, errorMsg);
    }

}
