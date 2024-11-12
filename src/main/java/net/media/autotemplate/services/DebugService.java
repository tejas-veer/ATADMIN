package net.media.autotemplate.services;

import com.google.gson.Gson;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.network.NetworkResponse;
import net.media.autotemplate.util.network.NetworkUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugService {
    private static final Gson GSON = Util.getGson();
    private static final Logger LOG = LogManager.getLogger(EntityService.class);

    public String getTemplateDebugData(String url) throws Exception{
        NetworkResponse res= NetworkUtil.getRequest(url);
        return res.getBody();
    }
}
