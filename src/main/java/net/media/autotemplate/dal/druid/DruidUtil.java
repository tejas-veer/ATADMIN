package net.media.autotemplate.dal.druid;

import com.google.gson.*;
import net.media.autotemplate.dal.druid.bean.Dimension;
import net.media.autotemplate.dal.druid.bean.DruidFilterBean;
import net.media.autotemplate.dal.druid.bean.DruidMeta;
import net.media.autotemplate.dal.druid.exception.DruidException;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;

import java.util.ArrayList;
import java.util.List;

import static net.media.autotemplate.dal.druid.DruidConstants.getCmMetaMap;

/*
    Created by shubham-ar
    on 3/10/17 9:53 PM   
*/
public class DruidUtil {
    private static final Logger LOG = LogManager.getLogger(DruidUtil.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static JsonArray executeQuery(String query) throws Exception {
        LoggingService.log(LOG, Level.info, query);
        NetworkResponse response;
        response = NetworkUtil.postRequest(DruidConstants.ANALYTICS_API_CONTAINS_URL, query);
        if (response.getStatus() == 420) {
            LoggingService.log(LOG, Level.error, response.getBody());
            throw new DruidException("NO_DATA", response.getBody());
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Request cancelled by user");
        }
        String responseStr = response.getBody();
        if (!Util.isStringSet(responseStr))
            return new JsonArray();
        JsonObject jsonObject = new Gson().fromJson(responseStr, JsonObject.class);
        JsonArray dataArray = jsonObject.getAsJsonArray("data");
        return dataArray;
    }

    public static JsonObject submitQuery(String query) throws Exception {
        LoggingService.log(LOG, Level.info, query);
        NetworkResponse response;
        response = NetworkUtil.postRequest(DruidConstants.ANALYTICS_API_DRUID_URL, query);
        if (response.getStatus() == 420) {
            throw new DruidException("NO_DATA", response.getBody());
        }
        String responseStr = response.getBody();
        LoggingService.log(LOG, Level.info, responseStr + "|" + response.getStatus());
        JsonArray APIResponse = GSON.fromJson(responseStr, JsonArray.class);
        return makeData(APIResponse);
    }

    private static JsonObject makeData(JsonArray APIResponse) {
        JsonObject formatted = new JsonObject();
        JsonArray dataArray = new JsonArray(), metaArray = new JsonArray();
        String[] meta_names = new String[0];
        boolean first = true;
        for (JsonElement tupleEle : APIResponse) {
            JsonObject row = tupleEle.getAsJsonObject();
            JsonObject item = new JsonObject();
            boolean err = false;
            if (first) {
                //Initializing Meta names
                meta_names = row.keySet().toArray(new String[0]);
                metaArray = getMeta(meta_names);
                first = false;
            }

            for (String meta : meta_names) {
                try {
                    if (isGranularity(meta)) {
                        item.addProperty(meta, row.get(meta).getAsString());
                        continue;
                    }
                    DruidMeta druidMeta = getCmMetaMap().get(meta);
                    switch (druidMeta.getType()) {
                        case "calculatedmetric":
                            Double data = formatDouble(row.get(meta).getAsDouble());
                            if (data.isNaN() || data.isInfinite())
                                item.addProperty(meta, "-");
                            else
                                item.addProperty(meta, data);
                            break;
                        case "filteredmetric":
                        case "metric":
                            item.addProperty(meta, row.get(meta).getAsInt());
                            break;
                        default:
                            String val = row.get(meta).getAsString();
                            err = val.equals("null");
                            item.addProperty(meta, val);
                    }
                } catch (Exception e) {
                    LOG.error(meta + " ->" + row.get(meta).toString(), e);
                    throw e;
                }
            }
            if (!err)
                dataArray.add(item);
        }

        formatted.add("data", dataArray);
        formatted.add("meta", metaArray);
        return formatted;
    }

    private static boolean isGranularity(String meta) {
        DruidConstants.Granularity[] granularities = DruidConstants.Granularity.values();
        for (DruidConstants.Granularity granularity : granularities) {
            if (granularity.getVal().equals(meta))
                return true;
        }
        return false;
    }

    private static double formatDouble(Double value) {
        if (value.isInfinite() || value.isNaN())
            return value;
        return Double.parseDouble(String.format("%.4f", value));
    }

    private static JsonArray getMeta(String[] names) {
        JsonArray meta_data = new JsonArray();
        for (String name : names) {
            meta_data.add(GSON.toJsonTree(getCmMetaMap().get(name)));
        }
        return meta_data;
    }


    public static List<DruidFilterBean> makeFilterList(Request request, Dimension dimension) {
        List<DruidFilterBean> filterList = new ArrayList<>();
        for (String parameter : DruidConstants.SEARCH_PARAMS) {
            String value = request.queryParams(parameter);
            if (Util.isStringSet(value)) {
                Dimension filterDimension = Dimension.getDimensionFromName(parameter);
                if (dimension.equals(filterDimension))
                    continue;
                filterList.add(new DruidFilterBean(filterDimension, value));
            }
        }
        return filterList;
    }

    public static void removeMeta(JsonArray druidMeta, String metaName) {
        for (int i = 0; i < druidMeta.size(); i++) {
            JsonObject mdata = druidMeta.get(i).getAsJsonObject();
            if (mdata.get("name").getAsString().equals(metaName)) {
                druidMeta.remove(i);
                break;
            }
        }
    }

    public static void addMeta(JsonArray druidMeta, String name, String type) {
        JsonObject meta_status = new JsonObject();
        meta_status.addProperty("name", name);
        meta_status.addProperty("type", type);
        druidMeta.add(meta_status);
    }
}

