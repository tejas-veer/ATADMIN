package net.media.autotemplate.factory;

import net.media.autotemplate.bean.RequestGlobal;
import net.media.autotemplate.util.Util;
import spark.Request;

/*
    Created by shubham-ar
    on 26/12/17 7:08 PM   
*/
public class RequestFactory {
    public static void getAdvancedParameter(Request request) {
        String adv = request.queryParams("adv");
        if (Util.isSet(adv)) {
            try {
                boolean advBoolean = Boolean.valueOf(adv);
                RequestGlobal.setAdvanced(advBoolean);
            } catch (Exception e) {
                RequestGlobal.setAdvanced(false);
            }
        }
    }
}
