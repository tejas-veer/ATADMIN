package net.media.autotemplate.services;

import net.media.autotemplate.bean.*;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.util.SysProperties;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Authentication;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
    Created by shubham-ar
    on 26/10/17 7:14 PM
*/
public class AclService {
    private static final Logger LOG = LogManager.getLogger(AclService.class);
    private static final Set<String> superGroups = new HashSet<>(Arrays.asList("Developer", "Super Admin", "Quality Analyst", "cm-dev", "Accounts", "CM-Products"));
    private static String[] superAdmins;
    private static boolean isSuperAdminRefresherLoaded = false;
    private static final int MAX_RETRY = 3;


    public static void initRefreshers() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                AclService.refreshSuperAdmins();
            } catch (Exception e) {
                LoggingService.log(LOG, Level.error, "Error in SuperAdmins Refresher", e);
            }
        }, 0, 5, TimeUnit.MINUTES);
    }


    public static void refreshSuperAdmins() {
        try {
            QueryTuple super_admins = AutoTemplateDAL.getAutoTemplateConfigMaster(ConfigConstants.GLOBAL_CONFIG, "SUPER_ADMIN");
            String super_admin[] = super_admins.getValue().split(",");
            Arrays.sort(super_admin);
            superAdmins = super_admin;
            isSuperAdminRefresherLoaded = true;
        } catch (Exception e) {
            isSuperAdminRefresherLoaded = false;
        }

    }


    public static boolean isSuperAdmin(String adminName) {
        if (Util.empty(superAdmins)) {
            refreshSuperAdmins();
        }
        return Arrays.binarySearch(superAdmins, adminName) > -1;
    }

    public static boolean isSuperAdmin(Admin admin) {
        return isSuperAdmin(admin.getAdminName());
    }

    public static boolean checkSuperGroupAccess(long admin_id) throws ExecutionException {
        Set<String> adminGrpDescList = AclCacheService.getAdminToDesc(admin_id);
        return Util.doSetsIntersect(adminGrpDescList, superGroups);
    }

    public static boolean checkSuperGroupAccess(Admin admin) throws ExecutionException {
        return checkSuperGroupAccess(admin.getAdminId());
    }


    public static boolean hasAdvertiserAccess(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap) {
        if (!entityAdminMap.containsKey(entityKey)) {
            return true;
        } else {
            return entityAdminMap.get(entityKey).contains(String.valueOf(admin.getAdminId()));
        }
    }

    public static boolean hasCampaignAccess(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap, String advertiserKey) {
        if (entityAdminMap.containsKey(entityKey) && entityAdminMap.get(entityKey).contains(String.valueOf(admin.getAdminId())))
            return true;
        else
            return hasAdvertiserAccess(advertiserKey, admin, entityAdminMap);
    }

    public static boolean hasAdDomainAccess(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap, Map<String, EntityParentsData> entityParentsData) {
        List<String> advertiserKeys = entityParentsData.get(entityKey).getAdvertiserKeys();
        for (String advertiserKey : advertiserKeys) {
            if (hasAdvertiserAccess(advertiserKey, admin, entityAdminMap))
                return true;
        }
        return false;
    }

    public static boolean hasCampaignAccess(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap, Map<String, EntityParentsData> entityParentsData) {
        String advertiserKey = entityParentsData.get(entityKey).getAdvertiserKey();
        return hasCampaignAccess(entityKey, admin, entityAdminMap, advertiserKey);
    }

    public static boolean hasAdGroupAccess(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap, Map<String, EntityParentsData> entityParentsData) {
        String campaignKey = entityParentsData.get(entityKey).getCampaignKey();
        String advertiserKey = entityParentsData.get(entityKey).getAdvertiserKey();
        return hasCampaignAccess(campaignKey, admin, entityAdminMap, advertiserKey);
    }

    public static AclStatus checkCustomerAccess(String customerId, Admin admin) {
        try {
            Set<Long> customerGrpIdListForAdmin = AclCacheService.getAdminToCustomerGrpIds(admin.getAdminId());
            CustomerGrpMapRefresher.initOnce();
            if (!CustomerGrpMapRefresher.isIsRefresherLoaded()) {
                return new AclStatus(HttpStatus.SC_EXPECTATION_FAILED, "CustomerId to CustomerGrpId Map refreshable failed!!");
            }
            Collection<Long> groupIds = CustomerGrpMapRefresher.getCustGrpIdMap().get(customerId);
            if (Util.doSetsIntersect(new HashSet<>(groupIds), customerGrpIdListForAdmin)) {
                return new AclStatus(HttpStatus.SC_OK, "");
            }
            return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have write access to given CUSTOMER " + customerId);
        } catch (Exception e) {
            return new AclStatus(HttpStatus.SC_EXPECTATION_FAILED, "Unable to fetch customer group Id for given admin.");
        }
    }

    public static AclStatus validateAccess(SupplyDemandHierarchy supplyDemandHierarchy, Admin admin, BusinessUnit businessUnit) throws DatabaseException {
        String entity;
        CreativeConstants.Level level;
        if (businessUnit == BusinessUnit.MAX) {
            entity = supplyDemandHierarchy.getDemandValue();
            level = supplyDemandHierarchy.getDemandLevel();
        } else {
            entity = supplyDemandHierarchy.getSupplyValue();
            level = supplyDemandHierarchy.getSupplyLevel();
        }
        return checkAccess(entity, level, admin, businessUnit);
    }


    public static AclStatus checkAccess(String entity, CreativeConstants.Level level, Admin admin, BusinessUnit businessUnit) throws DatabaseException {
        admin = FakeAdminService.transformIfFaked(admin);

        AclStatus superAccessStatus = checkSuperAccess(admin);
        if (superAccessStatus.getStatusCode() != HttpStatus.SC_FORBIDDEN)
            return superAccessStatus;

        if (businessUnit == BusinessUnit.MAX) {
            return checkMaxAccess(entity, level, admin);
        } else {
            return checkCmAccess(entity, level, admin);
        }
    }

    public static AclStatus checkSuperAccess(Admin admin)  {

        AclStatus superAdminStatus = superAdminCheck(admin);
        if (superAdminStatus.getStatusCode() != HttpStatus.SC_FORBIDDEN)
            return superAdminStatus;

        AclStatus superGrpStatus = superGroupCheck(admin);
        if (superGrpStatus.getStatusCode() != HttpStatus.SC_FORBIDDEN)
            return superGrpStatus;

        return checkLocalSysProperty();
    }

    public static AclStatus superAdminCheck(Admin admin) {
        refreshSuperAdminList();
        if (!isSuperAdminRefresherLoaded) {
            return new AclStatus(HttpStatus.SC_EXPECTATION_FAILED, "Super Admin Refreshable failed!!");
        } else if (isSuperAdmin(admin.getAdminName())) {
            return new AclStatus(HttpStatus.SC_OK, "");
        }
        return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have Super Admin Access.");
    }

    public static AclStatus superGroupCheck(Admin admin) {
        try {
            AclCacheService.getAdminToDesc(admin.getAdminId());
            if (checkSuperGroupAccess(admin)) {
                return new AclStatus(HttpStatus.SC_OK, "");
            }
            return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have Super Group Access");
        } catch (ExecutionException e) {
            return new AclStatus(HttpStatus.SC_EXPECTATION_FAILED, "Admin Group Description Cache loading failed!!");
        }
    }

    public static AclStatus checkLocalSysProperty() {
        if (SysProperties.getPropertyAsBoolean("LOCAL")) {
            return new AclStatus(HttpStatus.SC_OK, "");
        }
        return new AclStatus(HttpStatus.SC_FORBIDDEN, "");
    }

    public static void refreshSuperAdminList() {
        int retryCount = 0;
        while (!isSuperAdminRefresherLoaded && retryCount < ConfigConstants.MAX_RETRY) {
            AclService.refreshSuperAdmins();
            retryCount++;
        }
    }


    public static AclStatus checkMaxAccess(String entity, CreativeConstants.Level level, Admin admin) throws DatabaseException {
        if (level.equals(CreativeConstants.Level.GLOBAL))
            return new AclStatus(HttpStatus.SC_OK, "");

        Map<String, EntityParentsData> entityParentsData = Util.getParentData(entity, level);
        List<String> keys = Util.getDistinctEntityKeys(entityParentsData);
        Map<String, List<String>> entityAdminMap = AutoTemplateDAL.getEntityKeyAdminMap(keys);
        String entityKey = Util.getKey(entity, level.name());
        switch (level) {
            case ADVERTISER:
                return checkAdvertiserAccessStatus(entityKey, admin, entityAdminMap);
            case ADDOMAIN:
                return checkAdDomainAccessStatus(entityKey, admin, entityAdminMap, entityParentsData);
            case CAMPAIGN:
                return checkCampaignAccessStatus(entityKey, admin, entityAdminMap, entityParentsData);
            case ADGROUP:
                return checkAdGroupAccessStatus(entityKey, admin, entityAdminMap, entityParentsData);
            default:
                return new AclStatus(HttpStatus.SC_FORBIDDEN, "");
        }
    }

    public static AclStatus checkAdvertiserAccessStatus(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap) {
        if (hasAdvertiserAccess(entityKey, admin, entityAdminMap))
            return new AclStatus(HttpStatus.SC_OK, "");
        return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have write access to this ADVERTISER " + entityKey.split(ConfigConstants.UNDERSCORE_SEPARATOR)[0]);
    }

    public static AclStatus checkAdDomainAccessStatus(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap, Map<String, EntityParentsData> entityParentsData) {
        if (hasAdDomainAccess(entityKey, admin, entityAdminMap, entityParentsData))
            return new AclStatus(HttpStatus.SC_OK, "");
        return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have write access to this AD_DOMAIN " + entityKey.split(ConfigConstants.UNDERSCORE_SEPARATOR)[0]);
    }

    public static AclStatus checkCampaignAccessStatus(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap, Map<String, EntityParentsData> entityParentsData) {
        if (hasCampaignAccess(entityKey, admin, entityAdminMap, entityParentsData))
            return new AclStatus(HttpStatus.SC_OK, "");
        return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have write access to this CAMPAIGN " + entityKey.split(ConfigConstants.UNDERSCORE_SEPARATOR)[0]);
    }

    public static AclStatus checkAdGroupAccessStatus(String entityKey, Admin admin, Map<String, List<String>> entityAdminMap, Map<String, EntityParentsData> entityParentsData) {
        if (hasAdGroupAccess(entityKey, admin, entityAdminMap, entityParentsData))
            return new AclStatus(HttpStatus.SC_OK, "");
        return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have write access to this ADGROUP " + entityKey.split(ConfigConstants.UNDERSCORE_SEPARATOR)[0]);
    }

    public static AclStatus checkCmAccess(String entity, CreativeConstants.Level level, Admin admin) {
        if (level.equals(CreativeConstants.Level.GLOBAL) || level.equals(CreativeConstants.Level.ITYPE) || level.equals(CreativeConstants.Level.PORTFOLIO) || level.equals(CreativeConstants.Level.DOMAIN) || level.equals(CreativeConstants.Level.ADTAG))
            return new AclStatus(HttpStatus.SC_OK, "");

        else if (level.equals(CreativeConstants.Level.PARTNER)) {
            //If user is not superAdmin then no access for partner.
            return Util.aclStatusForPartner(entity);
        } else if (level.equals(CreativeConstants.Level.CUSTOMER)) {
            return checkCustomerAccess(entity, admin);
        } else if (level.equals(CreativeConstants.Level.ENTITY)) {
            return checkEntityAccess(entity, admin);
        }
        return new AclStatus(HttpStatus.SC_FORBIDDEN, "");

    }

    public static AclStatus checkEntityAccess(String entity, Admin admin){
        int retry = 0;
        Entity templateEntity = null;

        while (!Util.isSet(templateEntity) && retry < MAX_RETRY) {
            try {
                templateEntity = AutoTemplateDAL.getEntityInfo(Long.parseLong(entity));
            } catch (DatabaseException e) {
                retry++;
            }
        }

        if (!Util.isSet(templateEntity))
            return new AclStatus(HttpStatus.SC_EXPECTATION_FAILED, "Failed to Fetch entity info for given entity Id " + entity);

        return checkAccess(templateEntity, admin);
    }


    private static AclStatus checkAccess(Entity entity, Admin admin){
        admin = FakeAdminService.transformIfFaked(admin);
        try {
            Set<Long> customerGrpIdListForAdmin = AclCacheService.getAdminToCustomerGrpIds(admin.getAdminId());
            long adTagId = entity.getAdtagIdAsLong();
            try {
                Set<Long> adTagCustomerGrpIdCache = AclCacheService.getAdTagCustomerGrpIds(adTagId);
                if (Util.empty(adTagCustomerGrpIdCache) || Util.doSetsIntersect(adTagCustomerGrpIdCache, customerGrpIdListForAdmin))
                    return new AclStatus(HttpStatus.SC_OK, "");
                return new AclStatus(HttpStatus.SC_FORBIDDEN, "You don't have write access to given ENTITY " + entity.getEntityId());
            }catch (Exception e){
                return new AclStatus(HttpStatus.SC_EXPECTATION_FAILED, "AdTag to customer Grp id Cache failure for entity id " + entity.getEntityId());
            }
        } catch (Exception e) {
            return new AclStatus(HttpStatus.SC_EXPECTATION_FAILED, "Admin to customer Grp id Cache failure!!");
        }
    }

    public static AclStatus hasAccess(Entity entity, Admin admin, BusinessUnit businessUnit){
        if (isSuperAdmin(admin))
            admin = FakeAdminService.transformIfFaked(admin);
        if (SysProperties.getPropertyAsBoolean("LOCAL"))
            return new AclStatus(HttpStatus.SC_OK, "");

        AclStatus superGrpStatus = superGroupCheck(admin);
        if (superGrpStatus.getStatusCode() != HttpStatus.SC_FORBIDDEN)
            return superGrpStatus;

        if (businessUnit == BusinessUnit.CM)
            return checkAccess(entity, admin);

        return new AclStatus(HttpStatus.SC_OK, "");
    }

    public static AclStatus validate(Entity entity, Admin admin, BusinessUnit businessUnit) throws ExecutionException, Authentication.Failed {
        AclStatus aclStatus = hasAccess(entity, admin, businessUnit);
        if (aclStatus.getStatusCode() != HttpStatus.SC_OK)
            throw new Authentication.Failed("ACL failed,You may not have access to this page");
        return aclStatus;
    }
}
