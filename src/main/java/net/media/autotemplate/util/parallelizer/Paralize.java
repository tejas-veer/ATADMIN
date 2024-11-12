package net.media.autotemplate.util.parallelizer;

import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
    Created by shubham-ar
    on 8/8/18 4:47 PM   
*/
public abstract class Paralize<Key, PartSolution, Output> {
    public final int THREAD_COUNT = 10;

    private static final Logger LOG = LogManager.getLogger(Paralize.class);

    protected abstract List<Key> getKeys() throws Exception;

    protected abstract PartSolution consumeKey(Key x) throws Exception;

    protected final PartSolution safeconsumeKey(Key x) {
        try {
            return consumeKey(x);
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "ERROR_IN_CONSUME_KEY|" + x.getClass() + "|" + x.toString(), e);
        }
        return null;
    }

    protected abstract Output merge(List<Key> keys, Map<Key, PartSolution> items);

    public final Output doTask() throws Exception {
        List<Key> keys = getKeys();
        Map<Key, PartSolution> consumeMap = new ConcurrentHashMap<>();
        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<PartSolution>> futureList = new ArrayList<>();
        keys.forEach(key ->
                futureList.add(es.submit(() -> safeconsumeKey(key)))
        );

        for (int i = 0; i < keys.size(); i++) {
            PartSolution partSolution = futureList.get(i).get();
            consumeMap.put(keys.get(i), partSolution);
        }
        es.shutdown();
        return merge(keys, consumeMap);
    }
}
