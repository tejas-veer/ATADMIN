// Copyright (C) 2016 Media.net Advertising FZ-LLC All Rights Reserved 
package net.media.autotemplate.bean;


import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.LoggingService;
import spark.Request;

/**
 * Created by sumeet
 * on 20/4/16.
 */
public class RequestGlobal {
    private static final ThreadLocal<RequestGlobal> globalThreadLocal = ThreadLocal.withInitial(RequestGlobal::new);
    private String requestId = Util.concatenate("", "requestId|", Thread.currentThread().getId(), System.currentTimeMillis());

    private RequestGlobal() {
    }

    private long entityId;
    private String domain;
    private Long adTag;
    private Admin admin;
    private InboundParams inboundParams;
    private LoggingService loggingService;
    private boolean advanced;
    private Request request;

    public static void setAdvanced(boolean advanced) {
        get().advanced = advanced;
    }

    public static boolean isAdvanced() {
        return get().advanced;
    }

    private static RequestGlobal get() {
        return globalThreadLocal.get();
    }

    public long getEntityId() {
        return get().entityId;
    }

    public void setEntityId(long entityId) {
        get().entityId = entityId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        get().domain = domain;
    }

    public static String getRequestId() {
        return get().requestId;
    }

    public static Long getAdTag() {
        return get().adTag;
    }

    public static void setAdTag(Long adTag) {
        get().adTag = adTag;
    }

    public static Admin getAdmin() {
        return get().admin;
    }

    public static void setAdmin(Admin admin) {
        get().admin = admin;
    }

    public static LoggingService getLoggingService() {
        return get().loggingService;
    }

    public static void setLoggingService(LoggingService loggingService) {
        get().loggingService = loggingService;
    }

    public static void destroy() {
        globalThreadLocal.remove();
    }

    public static void setInboundParams(InboundParams inboundParams) {
        get().inboundParams = inboundParams;
    }

    public static InboundParams getInboundParams() {
        return get().inboundParams;
    }

    public static void setRequest(Request request) {
        get().request = request;
    }

    public static Request getRequest() {
        return get().request;
    }
}
