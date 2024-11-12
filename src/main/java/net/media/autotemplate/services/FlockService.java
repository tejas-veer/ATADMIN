package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/*
    Created by shubham-ar
    on 30/1/18 3:30 PM
*/
public class FlockService {
    private static final String FLOCK_HOOK_URL = "https://api.flock.com/hooks/sendMessage/3870fa42-469a-485b-97cb-85717f7b2e0c";
    private static final int FLOCK_CONNECTION_TIMEOUT = 50 * 1000;
    private static final Logger LOG = LogManager.getLogger(FlockService.class);
    private static final RequestConfig FLOCK_REQUEST_CONFIG = RequestConfig.custom()
            .setConnectionRequestTimeout(FLOCK_CONNECTION_TIMEOUT)
            .setConnectTimeout(FLOCK_CONNECTION_TIMEOUT)
            .setSocketTimeout(FLOCK_CONNECTION_TIMEOUT).build();

    public static void postFlockMessage(String message, String hookUrl) throws IOException {
        HttpClient client = null;
        HttpResponse response = null;
        try {
            client = HttpClientBuilder.create().setDefaultRequestConfig(FLOCK_REQUEST_CONFIG).build();
            HttpPost post = new HttpPost(hookUrl);
            post.setHeader("Content-Type", "application/json");
            JsonObject messageJson = new JsonObject();
            messageJson.addProperty("text", message);
            StringEntity params = new StringEntity(new Gson().toJson(messageJson));
            post.setEntity(params);
            response = client.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(client);
        }
    }

    public static void postHTMLWellMessage(String text, String title, String body) {
        HttpClient client = null;
        HttpResponse response = null;
        try {
            client = HttpClientBuilder.create().setDefaultRequestConfig(FLOCK_REQUEST_CONFIG).build();
            HttpPost post = new HttpPost(FLOCK_HOOK_URL);
            post.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(new Gson().toJson(getAttachment(text, title, getHtmlAttach(body))));
            post.setEntity(params);
            response = client.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(client);
        }
    }

    public static void postSupportMessage(String url, String adminName, String base64image, String desc) {
        HttpClient client = null;
        HttpResponse response = null;
        try {
            client = HttpClientBuilder.create().setDefaultRequestConfig(FLOCK_REQUEST_CONFIG).build();
            HttpPost post = new HttpPost(FLOCK_HOOK_URL);
            post.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(new Gson().toJson(getAttachment("*Support Request:*\n" + url, "Support for " + adminName, desc, getImageAttach(base64image))));
            post.setEntity(params);
            response = client.execute(post);
            LoggingService.log(LOG, Level.info, "FlockMessage", post);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(client);
        }
    }

    public static void postFlockException(String message, Throwable t) {
        try {
            postHTMLWellMessage(message, "STACKTRACE", ExceptionUtils.getStackTrace(t));
        }catch(Exception ignored){
        }
    }

    private static JsonObject getAttachment(String text, String title, Pair<String, JsonObject> attach) {
        return getAttachment(text, title, null, attach);
    }

    private static JsonObject getAttachment(String text, String title, String desc, Pair<String, JsonObject> attach) {
        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("text", text);
        JsonArray array = new JsonArray();
        messageJson.add("attachments", array);
        JsonObject attachment = new JsonObject();
        array.add(attachment);
        attachment.addProperty("title", title);
        if (Util.isStringSet(desc))
            attachment.addProperty("description", desc);

        JsonObject views = new JsonObject();
        attachment.add("views", views);
        views.add(attach.first, attach.second);

        LoggingService.log(LOG, Level.info, messageJson.toString());
        return messageJson;
    }

    private static Pair<String, JsonObject> getImageAttach(String base64image) {
        JsonObject imageAttach = new JsonObject();
        JsonObject orignal = new JsonObject();
        imageAttach.add("original", orignal);
        orignal.addProperty("src", base64image);
        return new Pair<>("image", imageAttach);
    }

    private static Pair<String, JsonObject> getHtmlAttach(String body) {
        JsonObject html = new JsonObject();
        html.addProperty("inline", "<pre>" + body + "</pre>");
        html.addProperty("height", (StringUtils.countMatches(body, "\n") * 18 + 50) + "");
        return new Pair<>("html", html);
    }
}
