package net.media.autotemplate.bean;

import net.media.autotemplate.util.Util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Jatin Warade
 * on 11-September-2023
 * at 18:50
 */
public class TemplateRenderingInboundParams {
    private final String template;
    private final int adCountFromParam;
    private final String adId;
    private final String titleFromParam;
    private final String descriptionFromParam;
    private final String imageUrlFromParam;
    private final String c2aTextFromParam;
    private final String sponsoredByFromParam;
    private final String titleKeywordFromParam;
    private final String isOverride;

    public TemplateRenderingInboundParams(HttpServletRequest request) {
        this.template = request.getParameter("tid");
        this.adCountFromParam = Util.getAdCountFromString(request.getParameter("adCount"));
        this.adId = request.getParameter("adid");
        this.titleFromParam = request.getParameter("title");
        this.descriptionFromParam = request.getParameter("desc");
        this.imageUrlFromParam = request.getParameter("imgUrl");
        this.c2aTextFromParam = request.getParameter("c2a");
        this.sponsoredByFromParam = request.getParameter("spons");
        this.titleKeywordFromParam = Util.isSet(request.getParameter("it")) ? request.getParameter("it") : "Multi Keyword Title";
        this.isOverride = request.getParameter("ov");
    }

    public String getTemplate() {
        return template;
    }

    public int getAdCountFromParam() {
        return adCountFromParam;
    }

    public String getAdId() {
        return adId;
    }

    public String getTitleFromParam() {
        return titleFromParam;
    }

    public String getDescriptionFromParam() {
        return descriptionFromParam;
    }

    public String getImageUrlFromParam() {
        return imageUrlFromParam;
    }

    public String getC2aTextFromParam() {
        return c2aTextFromParam;
    }

    public String getSponsoredByFromParam() {
        return sponsoredByFromParam;
    }

    public String getTitleKeywordFromParam() {
        return titleKeywordFromParam;
    }

    public boolean isOverride() {
        return (Util.isStringSet(isOverride)) && isOverride.equals("1");
    }
}
