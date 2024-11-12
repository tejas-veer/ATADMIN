package net.media.autotemplate.services;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.autoasset.SDTaskDetail;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.network.NetworkUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.Part;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.media.autotemplate.constants.AutoAssetConstants.OZIL_HOSTED_URL_SEPARATOR;

/**
 * Created by Jatin Warade
 * on 19/01/24
 */
public class OzilApiService {
    public static final int OZIL_URL_UPLOAD_BATCH_SIZE = 10;
    private static final int OZIL_STABLE_DIFFUSION_SEED = 5;
    public static final int OZIL_IMAGE_UPLOAD_MAX_RETRY = 3;
    private static final String OZIL_IMAGE_UPLOAD_ENDPOINT = "http://ozil.g-use1d-kbb-k8s.internal.media.net/autotargeting/aaimageupload";
    private static final String STABLE_DIFFUSION_IMAGE_ENDPOINT = "http://ozil.g-use1d-kbb-k8s.internal.media.net/autotargeting/stablediffusionimages";
    private static final String GENERATE_TITLE_OZIL_ENDPOINT = "http://ozil.g-use1d-kbb-k8s.internal.media.net/autotargeting/generatetitle";

    private static JsonObject convertToJsonObject(HttpResponse response) throws IOException {
        HttpEntity responseEntity = response.getEntity();

        if (Util.isSet(responseEntity)) {
            String responseString = EntityUtils.toString(responseEntity);
            return new Gson().fromJson(responseString, JsonObject.class);
        }
        return null;
    }

    public static JsonObject uploadHostedImages(String urlList, String dirPath) throws Exception {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        HttpClient client = clientBuilder.build();

        HttpPost post = new HttpPost(OZIL_IMAGE_UPLOAD_ENDPOINT);
        String boundary = "boundary_" + System.currentTimeMillis();
        post.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setBoundary(boundary);
        entityBuilder.addPart("url_list", new StringBody(urlList, ContentType.TEXT_PLAIN));
        entityBuilder.addPart("dir_path", new StringBody(dirPath, ContentType.TEXT_PLAIN));
        post.setEntity(entityBuilder.build());

        HttpResponse response = client.execute(post);

        return convertToJsonObject(response);
    }

    public static JsonObject uploadLocalImages(Part part, String dirPath) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", part.getInputStream(), ContentType.APPLICATION_OCTET_STREAM, getFileName(part));
        builder.addPart("dir_path", new StringBody(dirPath, ContentType.TEXT_PLAIN));
        HttpEntity formData = builder.build();

        HttpPost postRequest = new HttpPost(OZIL_IMAGE_UPLOAD_ENDPOINT);
        postRequest.setEntity(formData);

        HttpResponse httpResponse = httpClient.execute(postRequest);

        return convertToJsonObject(httpResponse);
    }

    public static String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public static Map<String, String> getOzilImageUrlForHostedImages(List<String> imageUrls, String dirPath) throws Exception {
        Map<String, String> originalToUploadedUrlMap = new HashMap();
        for (List<String> batchUrls : Lists.partition(imageUrls, OZIL_URL_UPLOAD_BATCH_SIZE)) {
            String urlList = String.join(OZIL_HOSTED_URL_SEPARATOR, batchUrls);
            JsonObject jsonObject = uploadHostedImages(urlList, dirPath);
            JsonArray imageUrlList = jsonObject.getAsJsonArray("ImageUrlList");

            for (int j = 0; j < imageUrlList.size(); j++) {
                JsonObject imageObject = imageUrlList.get(j).getAsJsonObject();
                String orgUrl = imageObject.get("org_url").getAsString();
                String uplUrl = imageObject.get("upl_url").getAsString();
                originalToUploadedUrlMap.put(orgUrl, uplUrl);
            }
        }
        return originalToUploadedUrlMap;
    }

    public static Map<String, String> getOzilImageUrlsForLocalImages(List<Part> imagePartList, String dirPath) throws Exception {
        Map<String, String> originalToUploadedUrlMap = new HashMap<>();

        for (Part part : imagePartList) {
            JsonObject jsonObject = uploadLocalImages(part, dirPath);
            JsonArray imageUrlList = jsonObject.getAsJsonArray("ImageUrlList");
            JsonObject urlObject = imageUrlList.get(0).getAsJsonObject();
            String fileName = getFileName(part);
            String uplUrl = urlObject.get("upl_url").getAsString();
            originalToUploadedUrlMap.put(fileName, uplUrl);
        }
        return originalToUploadedUrlMap;
    }

    public static JsonObject getOzilStableDiffusionImages(SDTaskDetail sdTaskDetail) throws IOException {
        String keyword = sdTaskDetail.getKeyword();
        String prompt = sdTaskDetail.getPrompt();
        String negativePrompt = sdTaskDetail.getNegativePrompt();

        if (Util.isStringSet(keyword) && Util.isStringSet(prompt)) {
            prompt = prompt.replace("{{kwd}}", keyword);
        }

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("kwd", keyword);
        requestBody.addProperty("prompt", prompt);
        requestBody.addProperty("seed", OZIL_STABLE_DIFFUSION_SEED);
        if (Util.isStringSet(negativePrompt)) {
            requestBody.addProperty("negative_prompt", negativePrompt);
        }
        HttpResponse response = NetworkUtil.postRequest(STABLE_DIFFUSION_IMAGE_ENDPOINT, requestBody);
        return convertToJsonObject(response);
    }

    public static JsonObject getOzilTitles(String demandKwd, String publisherPageTitle, String url, String prompt) throws IOException {
        JsonObject requestBody = new JsonObject();

        requestBody.addProperty("demand_kwd", demandKwd);
        requestBody.addProperty("publisher_page_title", publisherPageTitle);
        requestBody.addProperty("url", url);
        requestBody.addProperty("publisher_page_description", "");
        requestBody.add("publisher_additional_info", new JsonArray());
        requestBody.addProperty("demand_description", "");
        if (Util.isStringSet(prompt))
            requestBody.addProperty("prompt", prompt);
        requestBody.addProperty("page_type", "publisher");
        requestBody.addProperty("model", "");
        requestBody.addProperty("frequency_penalty", "0.0");
        requestBody.addProperty("presence_penalty", "0.0");
        requestBody.addProperty("temperature", "1");
        requestBody.addProperty("top_p", "");

        HttpResponse response = NetworkUtil.postRequest(GENERATE_TITLE_OZIL_ENDPOINT, requestBody);
        return convertToJsonObject(response);
    }

    public static JsonObject getOzilStableDiffusionImagesForImageFetchId(long imageFetchID) throws IOException, URISyntaxException {
        HttpClient httpClient = HttpClients.createDefault();
        String endpointUrl = STABLE_DIFFUSION_IMAGE_ENDPOINT;
        URI uri = new URIBuilder(endpointUrl)
                .setParameter("img_fetch_id", String.valueOf(imageFetchID))
                .build();
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = httpClient.execute(httpGet);
        return convertToJsonObject(response);
    }
}
