package net.media.autotemplate.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.AdAssetDetail;
import net.media.autotemplate.bean.TemplateRenderingInboundParams;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import org.jetbrains.annotations.Nullable;


/**
 * Created by Jatin Warade
 * on 11-September-2023
 * at 16:57
 */
public class TemplateRenderingUtil {
    public static String getNativeAsset(TemplateRenderingInboundParams templateRenderingParams){
        JsonArray nativeAsset = new JsonArray();
        JsonObject assetJson = getAssetJson(templateRenderingParams);
        for(int i = 0; i < templateRenderingParams.getAdCountFromParam(); i++){
            nativeAsset.add(assetJson);
        }
        return new Gson().toJson(nativeAsset);
    }

    private static JsonObject getAssetJson(TemplateRenderingInboundParams templateRenderingParams) {
        AdAssetDetail adAssetDetail = getAdAssetDetail(templateRenderingParams);

        String finalTitle = "[TITLE]";
        String finalDescription = "[DESCRIPTION]";
        String finalSponsored = "[Adwiser.com]";
        String finalCTA = "[CTA]";
        String finalImage = "";

        if (Util.isStringSet(templateRenderingParams.getAdId()) && !Util.isSet(adAssetDetail)) {
            finalTitle = "Error while getting AdAssets";
        } else if (Util.isSet(adAssetDetail)) {
            finalTitle = Util.isStringSet(adAssetDetail.getTitle()) ? adAssetDetail.getTitle() : finalTitle;
            finalDescription = Util.isStringSet(adAssetDetail.getDescription()) ? adAssetDetail.getDescription() : finalDescription;
            finalSponsored = Util.isStringSet(adAssetDetail.getSponsoredBy()) ? adAssetDetail.getSponsoredBy() : finalSponsored;
            finalCTA = Util.isStringSet(adAssetDetail.getCallToAction()) ? adAssetDetail.getCallToAction() : finalCTA;
            finalImage = Util.isStringSet(adAssetDetail.getImageUrl()) ? adAssetDetail.getImageUrl() : finalImage;
        }

        if (templateRenderingParams.isOverride()) {
            if (Util.isStringSet(templateRenderingParams.getTitleFromParam()))
                finalTitle = templateRenderingParams.getTitleFromParam();
            if (Util.isStringSet(templateRenderingParams.getDescriptionFromParam()))
                finalDescription = templateRenderingParams.getDescriptionFromParam();
            if (Util.isStringSet(templateRenderingParams.getImageUrlFromParam()))
                finalImage = templateRenderingParams.getImageUrlFromParam();
            if (Util.isStringSet(templateRenderingParams.getC2aTextFromParam()))
                finalCTA = templateRenderingParams.getC2aTextFromParam();
            if (Util.isStringSet(templateRenderingParams.getSponsoredByFromParam()))
                finalSponsored = templateRenderingParams.getSponsoredByFromParam();
        }

        finalTitle = updateAssetForQueryKeyword(finalTitle, templateRenderingParams.getTitleKeywordFromParam());
        finalDescription = updateAssetForQueryKeyword(finalDescription, templateRenderingParams.getTitleKeywordFromParam());

        JsonObject finalJson = new JsonObject();
        finalJson.add("title", getTextElement(finalTitle));
        finalJson.add("img", getUrlElement(finalImage));
        finalJson.add("desc", getValueElement(finalDescription));
        finalJson.add("cta", getValueElement(finalCTA));
        finalJson.add("spons", getValueElement(finalSponsored));
        return finalJson;
    }

    @Nullable
    private static AdAssetDetail getAdAssetDetail(TemplateRenderingInboundParams templateRenderingParams) {
        AdAssetDetail adAssetDetail = null;
        if (Util.isStringSet(templateRenderingParams.getAdId())) {
            try {
                adAssetDetail = AutoTemplateDAL.getAssetsFromAdId(templateRenderingParams.getAdId());
            } catch (Exception e) {
            }
        }
        return adAssetDetail;
    }

    private static String updateAssetForQueryKeyword(String asset, String titleKeywordFromParam) {
        if (Util.isStringSet(titleKeywordFromParam)) {
            asset = asset.replace("{queryKeyword_Sentence}", titleKeywordFromParam);
            asset = asset.replace("{queryKeyword_Title}", titleKeywordFromParam);
        }
        return asset;
    }

    public static JsonObject getUrlElement(String url) {
        JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("url", url);
        return jsonElement;
    }

    public static JsonObject getTextElement(String text) {
        JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("text", text);
        return jsonElement;
    }

    public static JsonObject getValueElement(String value) {
        JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("value", value);
        return jsonElement;
    }
}
