package net.media.autotemplate.factory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.DateRange;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.dal.druid.DruidConstants;
import net.media.autotemplate.dal.druid.DruidDAL;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidFilterBean;
import net.media.autotemplate.util.parallelizer.Paralize;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    Created by shubham-ar
    on 8/8/18 7:02 PM   
*/
public class ParallelizeFactory {
    private static final Logger LOG = LogManager.getLogger(ParallelizeFactory.class);

    public static Paralize<String, JsonObject, JsonObject> getAnalyticsParallelQuery(Entity entity, DateRange dateRange) {
        return new Paralize<String, JsonObject, JsonObject>() {
            @Override
            protected List<String> getKeys() throws Exception {
                return new ArrayList<String>() {{
                    add(DruidConstants.ATState.BASE.getVal());
                    add(DruidConstants.ATState.ENABLED.getVal());
                }};
            }

            @Override
            protected JsonObject consumeKey(String keyObj) throws Exception {
                JsonObject stateWiseTemplates = DruidDAL.getDruidResponseSkeleton();
                stateWiseTemplates = DruidDAL.getAllTemplates(entity, dateRange, new DruidFilterBean(Dimension.AT_STATE, keyObj, DruidConstants.Filter.EQUAL));
                JsonArray templatesArray = stateWiseTemplates.get("data").getAsJsonArray();
                templatesArray.forEach(item -> {
                    JsonObject template = item.getAsJsonObject();
                    template.addProperty(DruidConstants.ATState.getDimensionName(), keyObj);
                });
                return stateWiseTemplates;
            }

            @Override
            protected JsonObject merge(List<String> strings, Map<String, JsonObject> items) {
                JsonObject analyticsResponse = DruidDAL.getDruidResponseSkeleton();
                JsonArray jsonArray = analyticsResponse.get("data").getAsJsonArray();
                JsonArray analyticsUrls = new JsonArray();
                strings.forEach(state -> {
                    JsonObject druidResponse = items.get(state);
                    JsonArray data = druidResponse.get("data").getAsJsonArray();
                    data.forEach(jsonArray::add);
                    if (data.size() > 0)
                        analyticsResponse.add("meta", druidResponse.get("meta"));
                    analyticsUrls.add(druidResponse.get("reportUrl"));
                });
                analyticsResponse.addProperty("reportUrl", DruidDAL.getTemplateDataUrl(entity, dateRange));
                return analyticsResponse;
            }
        };
    }
}
