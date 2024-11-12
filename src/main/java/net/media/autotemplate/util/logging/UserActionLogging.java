package net.media.autotemplate.util.logging;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
    Created by shubham-ar
    on 9/11/17 8:23 PM   
*/
public class UserActionLogging {
    private static final Logger LOG = LogManager.getLogger(UserActionLogging.class);

    public static void log(Admin admin, String action, String comment) {
        try {
            LoggingService.log(LOG, Level.info, action + " -> " + comment);
            AutoTemplateDAL.logUserAction(admin, action, comment);
        } catch (Exception e) {
            LoggingService.log(LOG, Level.fatal, "failed in action logging", e);
        }
    }

}
