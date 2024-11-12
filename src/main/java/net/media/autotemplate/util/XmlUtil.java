package net.media.autotemplate.util;

import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.autoasset.AATaskDetail;
import net.media.autotemplate.bean.autoasset.SitePropertyDetail;
import net.media.autotemplate.bean.blocking.BlockingInfo;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.enums.ATRequestState;
import net.media.autotemplate.enums.ATRequestType;
import net.media.database.DatabaseException;

import java.util.List;
import java.util.Set;

/*
    Created by shubham-ar
    on 26/10/17 3:40 PM   
*/
public class XmlUtil {
    public static String getEntityBlockingXML(Entity entity, CreativeConstants.Level[] hierarchy) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<rt>");
        for (int i = 0; i < hierarchy.length; i++) {
            CreativeConstants.Level level = hierarchy[i];
            String value = level.getter().apply(entity);
            if (Util.isStringSet(value)) {
                xmlBuilder.append("<r>");
                xmlBuilder.append("<e>").append(value).append("</e>");
                xmlBuilder.append("<ht>").append(level.name()).append("</ht>");
                xmlBuilder.append("</r>");
            }
        }
        xmlBuilder.append("</rt>");
        return xmlBuilder.toString();
    }

    public static String getEntityUpdateXML(List<String> enabled, List<String> disabled) {
        StringBuilder builder = new StringBuilder();
        builder.append("<rt>");
        for (String templateId : enabled) {
            builder.append("<r>")
                    .append("<t_id>").append(templateId).append("</t_id>")
                    .append("<opr>e</opr>")
                    .append("</r>");
        }

        for (String templateId : disabled) {
            builder.append("<r>")
                    .append("<t_id>").append(templateId).append("</t_id>")
                    .append("<opr>d</opr>")
                    .append("</r>");
        }
        builder.append("</rt>");

        return builder.toString();
    }

    public static String getEntityXml(List<Pair<String, String>> domainAdTagList) {
        StringBuilder builder = new StringBuilder();
        builder.append("<rt>");
        for (Pair<String, String> pair : domainAdTagList) {
            builder.append("<r>")
                    .append("<d>").append(pair.first).append("</d>")
                    .append("<at>").append(pair.second).append("</at>")
                    .append("</r>");
        }
        builder.append("</rt>");
        return builder.toString();
    }

    public static String getEntityHierarchyXml(Set<String> entityHierarchySet) {
        StringBuilder builder = new StringBuilder();
        builder.append("<rt>");
        for (String entry : entityHierarchySet) {
            builder.append("<r>")
                    .append("<e>").append(Util.getEntityId(entry)).append("</e>")
                    .append("<h>").append(Util.getHierarchy(entry)).append("</h>")
                    .append("</r>");
        }
        builder.append("</rt>");
        return builder.toString();
    }

    public static String getEntityPropertyXml(List<Pair<String, String>> entityPropertyPair) {
        StringBuilder xml = new StringBuilder("<rt>");
        for (Pair<String, String> pair : entityPropertyPair) {
            xml.append("<r>");
            xml.append("<e>").append(pair.first).append("</e>");
            xml.append("<p>").append(pair.second).append("</p>");
            xml.append("</r>");
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getEntityInsertionXml(List<Pair<String, String>> domainAdTagList) {
        StringBuilder builder = new StringBuilder();
        builder.append("<rt>");
        for (Pair<String, String> pair : domainAdTagList) {
            builder.append("<r>")
                    .append("<d>").append(pair.first).append("</d>")
                    .append("<at>").append(pair.second).append("</at>")
                    .append("<ats>").append("DUMMY").append("</ats>")
                    .append("<cid>").append("DUMMY").append("</cid>")
                    .append("<pid>").append("DUMMY").append("</pid>")
                    .append("<dt>").append("DUMMY").append("</dt>")
                    .append("</r>");
        }
        builder.append("</rt>");
        return builder.toString();
    }

    public static String getBlockingInfoXML(List<BlockingInfo> blockingInfoList, BusinessUnit businessUnit) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (BlockingInfo blockingInfo : blockingInfoList) {
            xml.append("<r><f_id>")
                    .append(blockingInfo.getCreative())
                    .append("</f_id><f_size>")
                    .append(blockingInfo.getSize())
                    .append("</f_size><f_status>")
                    .append(blockingInfo.getStatus().name())
                    .append("</f_status><f_type>")
                    .append(blockingInfo.getType().name())
                    .append("</f_type><spt>")
                    .append(businessUnit.getDefaultSystemPage().getId())
                    .append("</spt><bu>")
                    .append(businessUnit.getId())
                    .append("</bu></r>");
        }
        xml.append("</rt>");
        return xml.toString();
    }

    private static String templateUpdateListToXML(List<String> list, String update, CreativeConstants.Type type, String size) {
        StringBuilder XMLRows = new StringBuilder();
        for (String item : list) {
            XMLRows.append("<r><f_id>").append(item).append("</f_id><f_size>").append(size).append("</f_size><f_status>").append(update).append("</f_status><f_type>").append(type.name()).append("</f_type></r>");
        }
        return XMLRows.toString();
    }

    public static String getEntityListXML(List<String> entities) {
        StringBuilder sb = new StringBuilder();
        sb.append("<rt>");
        entities.forEach(item -> sb.append("<r><e>").append(item).append("</e></r>"));
        sb.append("</rt>");
        return sb.toString();
    }

    public static String getUpsertTemplateXML(List<? extends MappingTemplate> updates) {
        StringBuilder sb = new StringBuilder().append("<rt>");
        updates.forEach(row -> {
            StringBuilder rowSb = new StringBuilder();
            try {
                rowSb.append("<r>")
                        .append(row.getXML())
                        .append("</r>");
                sb.append(rowSb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return sb.append("</rt>").toString();
    }

    public static String getSupplyDemandHierarchyXML(String hierarchy, String value, BusinessUnit businessUnit) {
        return "<rt>" +
                "<r>" + "<e>" + value + "</e>" + "<hl>" + hierarchy + "</hl>" + "<bu>" + businessUnit.getId() + "</bu>" + "</r>" +
                "</rt>";
    }


    public static String getATRequestXML(BulkRequest bulkRequest) {
        BusinessUnit buFromName = BusinessUnit.getBUFromName(bulkRequest.getBuSelected());
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (BulkData bulkData : bulkRequest.getBulkDataList()) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<shl>");
            xmlRow.append(bulkData.getSupply());
            xmlRow.append("</shl>");
            xmlRow.append("<sev>");
            xmlRow.append(bulkData.getSupplyId());
            xmlRow.append("</sev>");
            xmlRow.append("<dhl>");
            xmlRow.append(bulkData.getDemand());
            xmlRow.append("</dhl>");
            xmlRow.append("<dev>");
            xmlRow.append(bulkData.getDemandId());
            xmlRow.append("</dev>");
            xmlRow.append("<tfids>");
            xmlRow.append(bulkData.getTemplateIds());
            xmlRow.append("</tfids>");
            xmlRow.append("<ts>");
            xmlRow.append(bulkData.getTemplateSizes());
            xmlRow.append("</ts>");
            xmlRow.append("<ct>");
            xmlRow.append(bulkData.getCreativeType());
            xmlRow.append("</ct>");
            xmlRow.append("<mt>");
            xmlRow.append(bulkData.getMappingType());
            xmlRow.append("</mt>");
            xmlRow.append("<spt>");
            xmlRow.append(SystemPageType.getSPTFromName(bulkData.getSystemPageType()).getId());
            xmlRow.append("</spt>");
            xmlRow.append("<bu>");
            xmlRow.append(buFromName.getId());
            xmlRow.append("</bu>");
            xmlRow.append("</r>");
            xml.append(xmlRow.toString());
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getATRequestDetailsXML(List<ATRequestDetail> requestDetails) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (ATRequestDetail requestDetail : requestDetails) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<taskId>");
            xmlRow.append(requestDetail.getTaskId());
            xmlRow.append("</taskId>");
            xmlRow.append("</r>");
            xml.append(xmlRow.toString());
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getAdIdXml(String adId) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        StringBuilder xmlRow = new StringBuilder();
        xmlRow.append("<r>");
        xmlRow.append("<adid>");
        xmlRow.append(adId);
        xmlRow.append("</adid>");
        xmlRow.append("</r>");
        xml.append(xmlRow.toString());
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getUpdateAssetAssetIdCombinedXml(List<AssetDetail> assetList, String basis, String state) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");

        for (AssetDetail asset : assetList) {
            StringBuilder xmlRow = new StringBuilder();

            xmlRow.append("<r>");
            xmlRow.append("<ent_name>").append(asset.getEntityName()).append("</ent_name>");
            xmlRow.append("<ent_val>").append(asset.getEntityValue()).append("</ent_val>");
            xmlRow.append("<k_val>").append(asset.getKeyValue().toLowerCase()).append("</k_val>");
            xmlRow.append("<asset_type>").append(asset.getAssetType()).append("</asset_type>");
            if (Util.isSet(asset.getExtAssetId())) {
                xmlRow.append("<ext_asset_id>").append(asset.getExtAssetId()).append("</ext_asset_id>");
            }
            xmlRow.append("<asset_value>").append(asset.getAssetValue()).append("</asset_value>");
            if (Util.isStringSet(asset.getBasis()) && !asset.getBasis().equals("NULL")) {
                xmlRow.append("<basis>" + asset.getBasis() + "</basis>");
            }
            xmlRow.append("<set>").append(asset.getSetId()).append("</set>");
            xmlRow.append("<scr>").append(asset.getScore()).append("</scr>");
            xmlRow.append("<is_active>").append(asset.getIsActive()).append("</is_active>");
            xmlRow.append("<b>").append(basis).append("</b>");
            xmlRow.append("<s>").append(state).append("</s>");
            if (Util.isStringSet(asset.getSize()) && !asset.getSize().equals("NULL")) {
                xmlRow.append("<size>" + asset.getSize() + "</size>");
            }
            xmlRow.append("</r>");

            xml.append(xmlRow);
        }

        xml.append("</rt>");
        return xml.toString();
    }

    public static String getUpdateReviewAssetsXml(List<AssetDetail> assetList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");

        for (AssetDetail asset : assetList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<id>").append(asset.getId()).append("</id>");
            xmlRow.append("<review_status>").append(asset.getIsActive()).append("</review_status>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getMappedAssetXml(String assetType, String entityName, List<String> entityValueList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (String entityValue : entityValueList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<key_value>").append((assetType + "@@" + entityName + "@@" + entityValue).toLowerCase()).append("</key_value>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getMappedAssetForKeyValueXml(List<String> keyValueList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (String keyValue : keyValueList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<key_value>").append(keyValue).append("</key_value>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getAATaskDetailInsertXML(List<AATaskDetail> aaTaskDetailList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<tin>").append(aaTaskDetail.getTaskInputDetails()).append("</tin>");
            xmlRow.append("<version>").append("V1").append("</version>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }


    public static String getAATaskDetailsUpdateXML(List<AATaskDetail> aaTaskDetailList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (AATaskDetail aaTaskDetail : aaTaskDetailList) {
            ATRequestState state = aaTaskDetail.getState();
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<tid>").append(aaTaskDetail.getTaskId()).append("</tid>");
            xmlRow.append("<tin>").append(aaTaskDetail.getTaskInputDetails()).append("</tin>");
            xmlRow.append("<tout>").append(aaTaskDetail.getTaskOutputDetails()).append("</tout>");
            xmlRow.append("<st>").append(aaTaskDetail.getState().getDbName()).append("</st>");
            if (ATRequestState.ERROR.equals(state)) {
                xmlRow.append("<reason>").append(aaTaskDetail.getFailureReason()).append("</reason>");
            }
            xmlRow.append("<iact>").append(aaTaskDetail.getIsActive()).append("</iact>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getPromptDetailInsertXML(List<String> promptList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (String prompt : promptList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<prompt>").append(prompt).append("</prompt>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getPromptDetailGetXML(List<Long> promptIdList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (Long promptId : promptIdList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<prompt_id>").append(promptId).append("</prompt_id>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }


    public static String getRequestMasterGetXml(List<ATRequestType> atRequestTypeList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (ATRequestType atRequestType : atRequestTypeList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<req_type>").append(atRequestType.getDbName()).append("</req_type>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }


    public static String getCmSitePropertyMasterGetXml(List<Pair<String, String>> list) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (Pair item : list) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<site_name>").append(item.first).append("</site_name>");
            xmlRow.append("<property>").append(item.second).append("</property>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getCmSitePropertyMasterUpdateXml(List<SitePropertyDetail> sitePropertyDetailList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (SitePropertyDetail sitePropertyDetail : sitePropertyDetailList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<site_name>").append(sitePropertyDetail.getSiteName()).append("</site_name>");
            xmlRow.append("<environment></environment>");
            xmlRow.append("<property>").append(sitePropertyDetail.getProperty()).append("</property>");
            xmlRow.append("<value>").append(sitePropertyDetail.getPropertyValue()).append("</value>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }

    public static String getTemplateIdXML(List<String> templateIdList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<rt>");
        for (String id : templateIdList) {
            StringBuilder xmlRow = new StringBuilder();
            xmlRow.append("<r>");
            xmlRow.append("<template_id>").append(id).append("</template_id>");
            xmlRow.append("</r>");
            xml.append(xmlRow);
        }
        xml.append("</rt>");
        return xml.toString();
    }
}
