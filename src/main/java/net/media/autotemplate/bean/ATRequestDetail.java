package net.media.autotemplate.bean;

import net.media.autotemplate.constants.ConfigConstants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jatin Warade
 * on 14-March-2023
 * at 17:03
 */
public class ATRequestDetail {
    private final long taskId;
    private final long requestId;
    private final long adminId;
    private final String requestType;
    private final String supplyHierarchyLevel;
    private final String supplyEntityValue;
    private final String demandHierarchyLevel;
    private final String demandEntityValue;
    private final String templateFrameworkIds;
    private final String templateSizes;
    private final String creativeType;
    private final String mappingType;
    private final SystemPageType systemPageType;
    private final BusinessUnit businessUnit;


    public ATRequestDetail(long taskId, long requestId, long adminId, String requestType, String supplyHierarchyLevel, String supplyEntityValue,
                           String demandHierarchyLevel, String demandEntityValue, String templateFrameworkIds, String templateSizes,
                           String creativeType, String mappingType, SystemPageType systemPageType, BusinessUnit businessUnit) {
        this.taskId = taskId;
        this.requestId = requestId;
        this.adminId = adminId;
        this.requestType = requestType;
        this.supplyHierarchyLevel = supplyHierarchyLevel;
        this.supplyEntityValue = supplyEntityValue;
        this.demandHierarchyLevel = demandHierarchyLevel;
        this.demandEntityValue = demandEntityValue;
        this.templateFrameworkIds = templateFrameworkIds;
        this.templateSizes = templateSizes;
        this.creativeType = creativeType;
        this.mappingType = mappingType;
        this.systemPageType = systemPageType;
        this.businessUnit = businessUnit;
    }

    public long getTaskId() {
        return taskId;
    }

    public long getRequestId() {
        return requestId;
    }

    public long getAdminId() {
        return adminId;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getSupplyHierarchyLevel() {
        return supplyHierarchyLevel;
    }

    public String getSupplyEntityValue() {
        return supplyEntityValue;
    }

    public String getDemandHierarchyLevel() {
        return demandHierarchyLevel;
    }

    public String getDemandEntityValue() {
        return demandEntityValue;
    }

    public String getTemplateFrameworkIds() {
        return templateFrameworkIds;
    }


    public List<String> getTemplateIdList() {
        return Arrays.asList(getTemplateFrameworkIds().split(ConfigConstants.PIPE_SEPARATOR));
    }

    public String getTemplateSizes() {
        return templateSizes;
    }

    public String getCreativeType() {
        return creativeType;
    }

    public String getMappingType() {
        return mappingType;
    }

    public SystemPageType getSystemPageType() {
        return systemPageType;
    }

    public BusinessUnit getBusinessUnit() {
        return businessUnit;
    }
}
