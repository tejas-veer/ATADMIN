package net.media.autotemplate.util.generator;

import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.util.GeneratorUtil;
import net.media.autotemplate.util.Util;

import java.util.List;

/*
    Created by shubham-ar
    on 14/11/17 8:34 PM   
*/
public class EntityUrlCache {

    public static List<String> get(Entity entity) throws Exception {
        List<String> urls = AutoTemplateDAL.getCachedUrlsForEntity(entity);
        if (Util.empty(urls))
            return fetch(entity);
        return urls;
    }

    private static List<String> fetch(Entity entity) throws Exception {
        return GeneratorUtil.getUrlsFromDruid(entity);
    }
}
