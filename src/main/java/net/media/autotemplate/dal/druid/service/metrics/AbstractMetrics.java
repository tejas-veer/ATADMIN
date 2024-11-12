package net.media.autotemplate.dal.druid.service.metrics;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.media.autotemplate.bean.QueryTuple;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.druid.bean.DruidMeta;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static java.lang.String.format;

/**
 * @author Prateek Agrawal
 */

public abstract class AbstractMetrics implements Runnable {
    public static final Gson GSON = Util.getGson();
    private static final Logger LOG = LogManager.getLogger(AbstractMetrics.class);

    public static void runAll() {
        new CmMetricService().run();
        new MaxMetricService().run();
    }

    @Override
    public void run() {
        LoggingService.log(LOG, Level.info, "ANALYTICS_METRICS_STARTED: %s", getClassName());
        try {
            final String ANALYTICS_ATTRIBUTES_URL = format("https://analytics.mn/rest/model/getModelAttributes?modelId=%s", getModelId());

            List<String> metrics = getMetricsConfig();
            NetworkResponse modelAttributions = NetworkUtil.getRequest(ANALYTICS_ATTRIBUTES_URL);

            if (modelAttributions.isOK()) {
                updateMetrics(metrics, modelAttributions);
            } else {
                LoggingService.log(LOG, Level.error, format("%s | ANALYTICS_METRICS_RESPONSE_STATUS_CODE_NOT_OKAY | %s",
                        getClassName(), modelAttributions.getStatus()));
            }
        } catch (IOException e) {
            LoggingService.log(LOG, Level.error, format("ANALYTICS_METRICS_NETWORK_EXCEPTION | %s", getClassName()), e);
        } catch (DatabaseException e) {
            LoggingService.log(LOG, Level.error, format("ANALYTICS_METRICS_DATABASE_EXCEPTION | %s", getClassName()), e);
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, format("UNEXPECTED_EXCEPTION | %s", getClassName()), e);
        }
    }

    private void updateMetrics(List<String> metrics, NetworkResponse modelAttributions) {
        Map<String, DruidMeta> metricsMap = makeHashMapFromResponse(modelAttributions);

        setMetaMap(metricsMap);
        setMetrics(metrics.toArray(new String[0]));
        setImpression(metrics.get(0));
        LoggingService.log(LOG, Level.info, "ANALYTICS_METRICS_COMPLETED -> " + metrics);
    }

    @NotNull
    private Map<String, DruidMeta> makeHashMapFromResponse(NetworkResponse response) {
        Map<String, DruidMeta> metricsMap = new HashMap<>();
        JsonObject attributesObject = GSON.fromJson(response.getBody(), JsonObject.class);
        JsonArray attributes = attributesObject.getAsJsonArray("dimensions");
        attributes.addAll(attributesObject.getAsJsonArray("metrics"));

        attributes.forEach(item -> {
            JsonObject attribute = item.getAsJsonObject();
            if (!attribute.has("id") || !(attribute.has("name")))
                return;
            String attributeName = attribute.get("name").getAsString();
            if (metricsMap.containsKey(attributeName)) {
                return;
            }
            metricsMap.put(attributeName, new DruidMeta(attribute));
        });
        return metricsMap;
    }

    @NotNull
    private List<String> getMetricsConfig() throws DatabaseException {
        QueryTuple tuple = AutoTemplateDAL.getAutoTemplateConfigMaster(getEntity(), getProperty());
        if (Objects.isNull(tuple)) {
            LoggingService.log(LOG, Level.error, format("METRICS_CONFIG_IS_NULL: %s %s", getEntity(), getProperty()));
            return Collections.emptyList();
        }
        return Util.getGson().fromJson(tuple.getValue(), new TypeToken<List<String>>() {
        }.getType());
    }

    abstract String getProperty();

    abstract String getEntity();

    abstract String getModelId();

    abstract String getClassName();

    abstract void setMetaMap(Map<String, DruidMeta> refreshedMetaMap);

    abstract void setMetrics(String[] metrics);

    abstract void setImpression(String impression);
}
