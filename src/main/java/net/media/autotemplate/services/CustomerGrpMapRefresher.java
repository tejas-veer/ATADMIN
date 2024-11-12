package net.media.autotemplate.services;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.db.CMReportingDbUtil;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
    Created by shubham-ar
    on 14/3/18 5:12 PM   
*/
public class CustomerGrpMapRefresher implements Runnable {

    private static final Logger LOG = LogManager.getLogger(CustomerGrpMapRefresher.class);
    private static boolean isRefresherLoaded = false;
    private static Multimap<String, Long> CustGrpIdMap = HashMultimap.create();

    public static Multimap<String, Long> getCustGrpIdMap() {
        return CustGrpIdMap;
    }

    public static void initRefresher() {
        LoggingService.log(LOG, Level.info, "CustomerGrp_Map_Refresher_Initialised");
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new CustomerGrpMapRefresher(), 0, 15, TimeUnit.MINUTES);
    }

    public static void initOnce() {
        int retryCount = 0;
        while (!isRefresherLoaded && retryCount < ConfigConstants.MAX_RETRY) {
            LoggingService.log(LOG, Level.info, "CustomerGrp_Map_Refresher_Started");
            loadRefresher();
            retryCount += 1;
        }
        LoggingService.log(LOG, Level.info, "CustomerGrp_Map_Refresher_Ended");
    }

    private static void loadRefresher() {
        try {
            Multimap<String, Long> updateMap = HashMultimap.create();
            List<JsonObject> customerList = CMReportingDbUtil.getCustomerGroups();
            customerList.forEach(customer -> {
                String customerId = customer.get("customer_id").getAsString();
                try {
                    String groupIds[] = customer.get("group_ids").getAsString().split("\\,");
                    for (String groupID : groupIds) {
                        updateMap.put(customerId, Long.parseLong(groupID.trim()));
                    }
                } catch (Throwable throwable) {
                    LoggingService.log(LOG, Level.error, "ERROR_IN_CUSTOMER_GROUPS|" + customerId, throwable);
                }
            });
            CustGrpIdMap = updateMap;
            LoggingService.log(LOG, Level.info, "CustGroupsMap size: " + CustGrpIdMap.size());
            isRefresherLoaded = true;
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "ERROR_IN_CUSTOMER_GROUPS_MAP_REFRESHER", e);
            isRefresherLoaded = false;
        }
    }

    @Override
    public void run() {
        loadRefresher();
    }

    public static boolean isIsRefresherLoaded() {
        return isRefresherLoaded;
    }

    public static void main(String[] args) {
        initRefresher();
    }
}
