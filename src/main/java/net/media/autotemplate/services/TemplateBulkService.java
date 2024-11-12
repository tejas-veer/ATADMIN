package net.media.autotemplate.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.util.BulkUtil;
import net.media.autotemplate.util.Util;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TemplateBulkService {
    private static final Gson GSON = Util.getGson();
    protected final Admin admin;

    public TemplateBulkService() {
        this.admin = RequestGlobal.getAdmin();
    }

    public Admin getAdmin() {
        return admin;
    }

    private String getFailedAclList(List<Pair<String, String>> failedAclList) {
        StringBuilder builder = new StringBuilder();
        for (Pair<String, String> pair : failedAclList) {
            builder.append(pair.first)
                    .append(ConfigConstants.UNDERSCORE_SEPARATOR)
                    .append(pair.second)
                    .append(ConfigConstants.COMMA_SEPARATED);
        }
        if (!failedAclList.isEmpty()) {
            return builder.substring(0, builder.lastIndexOf(ConfigConstants.COMMA_SEPARATED));
        } else
            return "";

    }

    private JsonObject insertRequestDetails(String inputPayload, ATRequestType requestType) throws Exception {
        JsonObject response = new JsonObject();
        List<Pair<String, String>> failedAclList = new ArrayList<>();
        Long requestId = -1L;

        BulkRequest bulkRequest = BulkUtil.getModifiedBulkRequest(GSON.fromJson(inputPayload, BulkRequest.class));
        Pair<Integer, String> bulkRequestValidity;
        if (ATRequestType.TEMPLATE_BLOCKING.equals(requestType)) {
            bulkRequestValidity = getBulkBlockingRequestValidity(bulkRequest);
        } else {
            bulkRequestValidity = getBulkMappingRequestValidity(bulkRequest);
        }

        int status = bulkRequestValidity.first;
        String errorMsg = bulkRequestValidity.second;

        if (status == HttpStatus.SC_OK) {
            failedAclList = BulkAclService.getFailedAclList(bulkRequest, this.admin);

            if (!failedAclList.isEmpty()) {
                status = HttpStatus.SC_NOT_ACCEPTABLE;
                errorMsg = "ACL FAILED";
            } else {
                requestId = AutoTemplateDAL.insertATRequest(bulkRequest, requestType.getDbName(), admin.getAdminId());
            }
        }

        response.addProperty("requestId", requestId);
        response.addProperty("statusCode", status);
        response.addProperty("errorMessage", errorMsg);
        response.addProperty("failedAcl", getFailedAclList(failedAclList));

        return response;
    }

    public JsonObject insertMappingRequestDetails(String inputPayload) throws Exception {
        return insertRequestDetails(inputPayload, ATRequestType.TEMPLATE_MAPPING);
    }

    public JsonObject insertUnmappingRequestDetails(String inputPayload) throws Exception {
        return insertRequestDetails(inputPayload, ATRequestType.TEMPLATE_UNMAPPING);
    }

    public JsonObject insertBlockingRequestDetails(String inputPayload) throws Exception {
        return insertRequestDetails(inputPayload, ATRequestType.TEMPLATE_BLOCKING);
    }

    private Pair<Integer, String> getBulkRequestValidity(BulkRequest bulkRequest, Function<BulkData, Pair<Integer, String>> rowValidityChecker) {
        for (BulkData bulkData : bulkRequest.getBulkDataList()) {
            Pair<Integer, String> rowValidity = rowValidityChecker.apply(bulkData);
            if (rowValidity.first != HttpStatus.SC_OK) {
                return rowValidity;
            }
        }
        return new Pair<>(HttpStatus.SC_OK, "");
    }

    private Pair<Integer, String> getBulkMappingRequestValidity(BulkRequest bulkRequest) {
        return getBulkRequestValidity(bulkRequest, BulkUtil::getMappingRowValidity);
    }

    private Pair<Integer, String> getBulkBlockingRequestValidity(BulkRequest bulkRequest) {
        return getBulkRequestValidity(bulkRequest, BulkUtil::getBlockingRowValidity);
    }


}
