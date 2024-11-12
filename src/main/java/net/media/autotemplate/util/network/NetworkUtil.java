package net.media.autotemplate.util.network;


import com.google.gson.JsonObject;
import net.media.autotemplate.dal.druid.DruidConstants;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*
    Created by shubham-ar
    on 3/10/17 9:54 PM   
*/
public class NetworkUtil {
    private static final Logger log = LogManager.getLogger(NetworkUtil.class);

    public static final RequestConfig DRUID_REQUEST_CONFIG = RequestConfig.custom()
            .setConnectionRequestTimeout(DruidConstants.ANALYTICS_API_DRUID_CONNECTION_TIMEOUT_MS)
            .setConnectTimeout(DruidConstants.ANALYTICS_API_DRUID_CONNECTION_TIMEOUT_MS)
            .setSocketTimeout(DruidConstants.ANALYTICS_API_DRUID_CONNECTION_TIMEOUT_MS).build();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public static NetworkResponse formPostRequest(String apiUrl, String body) throws Exception {
        HttpClient httpClient = null;
        HttpResponse response = null;
        try {
            HttpPost httpRequest = new HttpPost(apiUrl);
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
            httpRequest.setEntity(entity);

            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(DRUID_REQUEST_CONFIG).build();
            response = httpClient.execute(httpRequest);

            return new NetworkResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            throw e;
        } finally {
            HttpClientUtils.closeQuietly(httpClient);
            HttpClientUtils.closeQuietly(response);
        }
    }

    public static NetworkResponse postRequest(String apiUrl, String requestBody) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DruidConstants.ANALYTICS_API_DRUID_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(DruidConstants.ANALYTICS_API_DRUID_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS).build();
        RequestBody postBody = RequestBody.create(JSON, requestBody);
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(postBody).addHeader(DruidConstants.HEADER_AUTH, DruidConstants.HEADER_AUTH_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        NetworkResponse networkResponse = new NetworkResponse(response.code(), response.body().string());
        response.body().close();
        return networkResponse;
    }

    public static NetworkResponse getRequest(String apiUrl) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DruidConstants.ANALYTICS_API_DRUID_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .readTimeout(DruidConstants.ANALYTICS_API_DRUID_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS).build();


        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader(DruidConstants.HEADER_AUTH, DruidConstants.HEADER_AUTH_VALUE)
                .build();

        Response response = client.newCall(request).execute();
        NetworkResponse networkResponse = new NetworkResponse(response.code(), response.body().string());
        response.body().close();
        return networkResponse;

    }

    public static HttpResponse postRequest(String apiUrl, JsonObject requestBody) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        String endpointUrl = apiUrl;
        HttpPost httpPost = new HttpPost(endpointUrl);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity requestEntity = new StringEntity(requestBody.toString());
        httpPost.setEntity(requestEntity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        return httpResponse;
    }
}
