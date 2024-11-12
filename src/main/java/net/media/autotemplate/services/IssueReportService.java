package net.media.autotemplate.services;

import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/*
    Created by shubham-ar
    on 26/6/18 3:09 PM   
*/
public class IssueReportService {
    private static final Logger LOG = LogManager.getLogger(IssueReportService.class);
    private static final String ISSUE_HOOK = "https://api.flock.com/hooks/sendMessage/62072e0a-e19b-429b-9f08-dd4a9ca2d244";

    public static boolean reportIssue(String message) {
        try {
            FlockService.postFlockMessage(message, ISSUE_HOOK);
            return true;
        } catch (IOException e) {
            LoggingService.log(LOG, Level.error, "Error in sending issue", e);
        }
        return false;
    }
}
