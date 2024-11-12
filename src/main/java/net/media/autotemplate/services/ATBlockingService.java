package net.media.autotemplate.services;

/*
    Created by shubham-ar
    on 13/2/18 9:48 PM   
*/

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.blocking.BlockingInfo;
import net.media.autotemplate.bean.blocking.BlockingRequest;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.ATMappingUtil;
import net.media.autotemplate.dal.blocking.TemplateBlockingDal;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.util.Util;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ATBlockingService {
    private static final Logger LOG = LogManager.getLogger(ATBlockingService.class);
    private static final Gson GSON = Util.getGson();
    private final CreativeConstants.Type type;
    private final Admin admin;
    private final SupplyDemandHierarchy supplyDemandHierarchy;
    private final BusinessUnit businessUnit;
    private final AclStatus aclStatus;

    public ATBlockingService(SupplyDemandHierarchy supplyDemandHierarchy, CreativeConstants.Type type, String buSelected) throws ExecutionException, DatabaseException {
        this.type = type;
        this.supplyDemandHierarchy = supplyDemandHierarchy;
        this.admin = RequestGlobal.getAdmin();
        this.businessUnit = BusinessUnit.getBUFromName(buSelected);
        this.aclStatus = AclService.validateAccess(supplyDemandHierarchy, RequestGlobal.getAdmin(), this.businessUnit);
    }

    public CreativeConstants.Type getType() {
        return type;
    }

    public AclStatus getAclStatus() {
        return aclStatus;
    }


    public JsonObject getTableData() throws Exception {
        JsonObject analyticsResponse = DruidDAL.getDruidResponseSkeleton();
        Map<String, BlockingInfo> ruleMap = ATMappingUtil.getBlockingData(this.supplyDemandHierarchy.getEntityValue(), this.supplyDemandHierarchy.getHierarchyLevel(), type, this.businessUnit);
        ATMappingUtil.mapData(analyticsResponse, ruleMap, type.getDimensionName());
        analyticsResponse.addProperty("aclStatus", getAclStatus().getStatusCode());
        analyticsResponse.addProperty("aclErrMessage", getAclStatus().getErrMessage());
        return analyticsResponse;

    }

    public String updateEntity(String updateJson) throws DatabaseException {
        BlockingRequest blockingRequest = GSON.fromJson(updateJson, BlockingRequest.class);
        List<BlockingInfo> blockingInfoList = blockingRequest.getPayloads();
        BusinessUnit businessUnit = BusinessUnit.getBUFromName(blockingRequest.getBuSelected());
        TemplateBlockingDal.insertEntityWisCreativeStatus(this.supplyDemandHierarchy.getEntityValue(), this.supplyDemandHierarchy.getHierarchyLevel(), blockingInfoList, admin, businessUnit);
        return "" + blockingInfoList.size();
    }

}
