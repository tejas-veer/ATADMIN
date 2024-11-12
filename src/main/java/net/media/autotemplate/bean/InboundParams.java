package net.media.autotemplate.bean;

import net.media.autotemplate.util.Util;
import spark.Request;

/*
    Created by shubham-ar
    on 3/1/18 3:35 PM   
*/
public class InboundParams {

    private String useproxy;
    private String nocache;
    private String supply;
    private String supplyId;
    private String demand;
    private String demandID;
    private String mappingType;
    private String buSelected;




    private InboundParams() {
        Request request = RequestGlobal.getRequest();
        parseRequest(request);
    }

    public static InboundParams getInstance() {
        if (RequestGlobal.getInboundParams() == null) {
            RequestGlobal.setInboundParams(new InboundParams());
        }
        return RequestGlobal.getInboundParams();
    }

    private void parseRequest(Request request) {
        this.useproxy = request.queryParamOrDefault("useproxy", "1");
        this.nocache = request.queryParamOrDefault("nocache", "false");
        this.nocache = Util.isStringSet(nocache) && nocache.equals("true") ? "1" : "0";

        this.supply = request.queryParamOrDefault("supply", "");
        this.supplyId = request.queryParamOrDefault("supplyId", "");
        this.demand = request.queryParamOrDefault("demand", "");
        this.demandID = request.queryParamOrDefault("demandId", "");

        this.buSelected = request.queryParamOrDefault("buSelected", "");
        this.mappingType = request.queryParamOrDefault("mappingType", "");

    }

    public String getUseproxy() {
        return useproxy;
    }

    public String getNocache() {
        return nocache;
    }

    public String getSupply() {
        return supply;
    }

    public String getBuSelected() {
        return buSelected;
    }

    public String getSupplyId() {
        return supplyId;
    }

    public String getDemand() {
        return demand;
    }

    public String getDemandID() {
        return demandID;
    }

    public String getMappingType() {
        return mappingType;
    }

}
