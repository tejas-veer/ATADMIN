package net.media.autotemplate.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
    Created by shubham-ar
    on 24/9/18 6:40 PM   
*/
public class MailingService {
    private static final MailingService INSTANCE = new MailingService();
    private static final Logger LOG = LogManager.getLogger(MailingService.class);

    private MailingService() {
    }

    public static MailingService getInstance() {
        return INSTANCE;
    }
}
