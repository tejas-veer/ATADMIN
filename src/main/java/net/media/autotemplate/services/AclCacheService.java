package net.media.autotemplate.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.db.LoginDbUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AclCacheService {
    private static LoadingCache<Long, Set<String>> ADMIN_GROUP_DESC_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<Long, Set<String>>() {

                @Override
                public Set<String> load(Long adminId) throws Exception {
                    return new HashSet<>(LoginDbUtil.getadminGrpDesc(adminId));
                }
            });

    private static LoadingCache<Long, Set<Long>> ADMIN_CUST_GRP_ID = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<Long, Set<Long>>() {

                @Override
                public Set<Long> load(Long adminId) throws Exception {
                    return new HashSet<>(LoginDbUtil.getCustomerGrpIdForAdmin(adminId));
                }
            });

    private static LoadingCache<Long, Set<Long>> ADTAG_CUSTOMER_GROUP_ID_MAP = CacheBuilder.newBuilder()
            .maximumSize(500000)
            .expireAfterAccess(60, TimeUnit.MINUTES)
            .build(new CacheLoader<Long, Set<Long>>() {

                @Override
                public Set<Long> load(Long entityId) throws Exception {
                    return new HashSet<Long>(LoginDbUtil.getCustomerGrpId(String.valueOf(entityId)).stream().map(x -> x.get("customerGrpId").getAsLong()).collect(Collectors.toList()));
                }
            });

    public static Set<String> getAdminToDesc(Long id) throws ExecutionException {
        int retryCount = 0;
        Set<String> descSet = new HashSet<>();
        while (retryCount < ConfigConstants.MAX_RETRY) {
            try {
                descSet = ADMIN_GROUP_DESC_CACHE.get(id);
                break;
            } catch (ExecutionException e) {
                retryCount += 1;
                if (retryCount == ConfigConstants.MAX_RETRY) {
                    throw e;
                }
            }
        }
        return descSet;
    }

    public static Set<Long> getAdminToCustomerGrpIds(Long id) throws ExecutionException {
        int retryCount = 0;
        Set<Long> customerGroupIds = new HashSet<>();
        while (retryCount < ConfigConstants.MAX_RETRY) {
            try {
                customerGroupIds  = ADMIN_CUST_GRP_ID.get(id);
                break;
            } catch (ExecutionException e) {
                retryCount += 1;
                if (retryCount == ConfigConstants.MAX_RETRY) {
                    throw e;
                }
            }
        }
        return customerGroupIds;
    }

    public static Set<Long> getAdTagCustomerGrpIds(Long id) throws ExecutionException {
        int retryCount = 0;
        Set<Long> customerGroupIds = new HashSet<>();
        while (retryCount < ConfigConstants.MAX_RETRY) {
            try {
                customerGroupIds  = ADTAG_CUSTOMER_GROUP_ID_MAP.get(id);
                break;
            } catch (ExecutionException e) {
                retryCount += 1;
                if (retryCount == ConfigConstants.MAX_RETRY) {
                    throw e;
                }
            }
        }
        return customerGroupIds;
    }


}
