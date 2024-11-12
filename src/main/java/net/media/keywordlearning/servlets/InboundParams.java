package net.media.keywordlearning.servlets;

import net.media.keywordlearning.Utils.Utils;
import net.media.keywordlearning.beans.PirateDebugInput;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by autoopt/rohit.aga.
 */
public class InboundParams {

    private PirateDebugInput pirateInput;
    //get the admin id later on
    private Boolean isLocal;

    public InboundParams(HttpServletRequest request){
        setPirateDebugValues(request);

    }

    private void setPirateDebugValues(HttpServletRequest request) {
        pirateInput = new PirateDebugInput();
        pirateInput.setSiteName(Utils.getTrimmedString(request.getParameter("siteName")));
        pirateInput.setBasis(Utils.getTrimmedString(request.getParameter("basis")));
        pirateInput.setCanonHash(Utils.getTrimmedString(request.getParameter("canonHash")));

        if(!Utils.isEmpty(request.getParameter("keywordType"))){
            pirateInput.setKeywordType(Integer.valueOf(Utils.getTrimmedString(request.getParameter("keywordType"))));
        }
        if(!Utils.isEmpty(request.getParameter("urlCount"))){
            pirateInput.setUrlCount(Integer.valueOf(Utils.getTrimmedString(request.getParameter("urlCount"))));
        }
        if(!Utils.isEmpty(request.getParameter("learnerId"))){
            pirateInput.setLearnerId(Integer.valueOf(Utils.getTrimmedString(request.getParameter("learnerId"))));
        }
        if(!Utils.isEmpty(request.getParameter("bucketId"))){
            pirateInput.setBucketId(Integer.valueOf(Utils.getTrimmedString(request.getParameter("bucketId"))));
        }
        if(!Utils.isEmpty(request.getParameter("adTagId"))) {
            pirateInput.setAdTagId(Integer.valueOf(Utils.getTrimmedString(request.getParameter("adTagId"))));
        }
    }

    public PirateDebugInput getPirateInput(){
        return this.pirateInput;
    }

}
