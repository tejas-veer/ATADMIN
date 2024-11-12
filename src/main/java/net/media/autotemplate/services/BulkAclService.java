package net.media.autotemplate.services;

import net.media.autotemplate.bean.*;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.util.SysProperties;
import net.media.autotemplate.util.Util;
import net.media.database.DatabaseException;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class BulkAclService {

    public static List<Pair<String, String>> getFailedAclList(BulkRequest bulkRequest, Admin admin) throws DatabaseException, ExecutionException {

        if (AclService.isSuperAdmin(admin.getAdminName()) || AclService.checkSuperGroupAccess(admin) || SysProperties.getPropertyAsBoolean("LOCAL"))
            return new ArrayList<>();

        List<BulkData> bulkDataList = bulkRequest.getBulkDataList();
        List<Pair<String, String>> failedAclList = new ArrayList<>();
        if (bulkRequest.getBuSelected().equalsIgnoreCase(BusinessUnit.MAX.name())) {
            failedAclList = getFailedAclListForMax(admin, bulkDataList);
        }
        return failedAclList;
    }

    private static List<Pair<String, String>> getFailedAclListForMax(Admin admin, List<BulkData> bulkDataList) throws DatabaseException {
        List<Pair<String, String>> failedAclList = new ArrayList<>();
        Set<String> entityHierarchySet = new HashSet<>();
        for (BulkData bulkData : bulkDataList) {
            if (!bulkData.getDemandId().isEmpty() && !bulkData.getDemand().isEmpty()) {
                entityHierarchySet.add(Util.getKey(bulkData.getDemandId().trim(), bulkData.getDemand().trim().toUpperCase()));
            }
        }

        Map<String, EntityParentsData> entityParentData = Util.getParentData(entityHierarchySet);
        List<String> keys = Util.getDistinctEntityKeys(entityParentData);
        Map<String, List<String>> entityAdminMap = AutoTemplateDAL.getEntityKeyAdminMap(keys);

        for (String entry: entityHierarchySet) {
            if (CreativeConstants.Level.valueOf(Util.getHierarchy(entry)).equals(CreativeConstants.Level.ADVERTISER) && !AclService.hasAdvertiserAccess(entry, admin, entityAdminMap))
                failedAclList.add(new Pair<>(Util.getEntityId(entry), Util.getHierarchy(entry)));
            else if (CreativeConstants.Level.valueOf(Util.getHierarchy(entry)).equals(CreativeConstants.Level.ADDOMAIN) && !AclService.hasAdDomainAccess(entry, admin, entityAdminMap, entityParentData))
                failedAclList.add(new Pair<>(Util.getEntityId(entry), Util.getHierarchy(entry)));
            else if (CreativeConstants.Level.valueOf(Util.getHierarchy(entry)).equals(CreativeConstants.Level.CAMPAIGN) && !AclService.hasCampaignAccess(entry, admin, entityAdminMap, entityParentData))
                failedAclList.add(new Pair<>(Util.getEntityId(entry), Util.getHierarchy(entry)));
            else if (CreativeConstants.Level.valueOf(Util.getHierarchy(entry)).equals(CreativeConstants.Level.ADGROUP) && !AclService.hasAdGroupAccess(entry, admin, entityAdminMap, entityParentData)) {
                failedAclList.add(new Pair<>(Util.getEntityId(entry), Util.getHierarchy(entry)));
            }
        }
        return failedAclList;
    }
}
