//  Copyright (C) 2017 Media.net Advertising FZ-LLC All Rights Reserved

package net.media.autotemplate.dal.db;

import net.media.autotemplate.bean.FrameworkSize;
import net.media.autotemplate.util.Util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sumeet
 * on 5/7/17.
 */
public class FrameworkSizeService {

    private static long UPDATION_TIME = System.currentTimeMillis();
    private static final long REFRESH_INTERVAL = 1000 * 60 * 60 * 24;
    private static final Map<Integer, FrameworkSize> FRAMEWORK_SIZE_MAP = new ConcurrentHashMap<>(2000);

    public static boolean isValidSize(long frameworkId, String adtagSize) throws Exception {
        Set<Integer> sizeIds = CMTemplateDbUtil.getFrameworkPageSizeIds(frameworkId);
        for (int size : sizeIds) {
            FrameworkSize frameworkSize = getFrameworkSize(size);
            if (adtagSize.equalsIgnoreCase(frameworkSize.getWidthByHeight())) {
                return true;
            }
        }
        return false;
    }

    public static FrameworkSize getFrameworkSize(Integer sizeId) throws Exception {
        return getFrameworkSizeMap().get(sizeId);
    }

    private static Map<Integer, FrameworkSize> getFrameworkSizeMap() throws Exception {

       if (isOutDated() || Util.empty(FRAMEWORK_SIZE_MAP)) {
            synchronized (FrameworkSizeService.class) {
                if (isOutDated() || Util.empty(FRAMEWORK_SIZE_MAP)) {
                    List<FrameworkSize> frameworkSizes = CMTemplateDbUtil.getAllFrameworkSize();
                    for (FrameworkSize frameworkSize : frameworkSizes) {
                        FRAMEWORK_SIZE_MAP.put(frameworkSize.getSizeId(), frameworkSize);
                    }
                    UPDATION_TIME = System.currentTimeMillis();
                }
            }
        }
        return FRAMEWORK_SIZE_MAP;
    }

    private static boolean isOutDated() {
        return (System.currentTimeMillis() - UPDATION_TIME) > REFRESH_INTERVAL;
    }
}
