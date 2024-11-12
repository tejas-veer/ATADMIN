package net.media.autotemplate.services;

import com.google.gson.JsonArray;
import net.media.autotemplate.util.Util;

import java.util.Map;
import java.util.concurrent.*;

public class RequestManagerService {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Map<String, Future<JsonArray>> activeRequests = new ConcurrentHashMap<>();

    public static void addActiveRequest(String requestId, Future<JsonArray> future) {
        if (Util.isStringSet(requestId))
            activeRequests.put(requestId, future);
    }

    public static void removeActiveRequest(String requestId) {
        if (Util.isStringSet(requestId))
            activeRequests.remove(requestId);
    }

    public static void cancelRequest(String requestId) {
        Future<JsonArray> future = activeRequests.get(requestId);
        if (Util.isSet(future)) {
            future.cancel(true);
            removeActiveRequest(requestId);
        }
    }

    public static Future<JsonArray> submitRequest(Callable<JsonArray> task) {
        return executor.submit(task);
    }
}
