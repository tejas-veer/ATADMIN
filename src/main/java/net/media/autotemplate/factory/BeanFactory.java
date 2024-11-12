package net.media.autotemplate.factory;

import com.google.gson.Gson;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.blocking.BlockingTemplate;
import net.media.autotemplate.bean.mapping.MappingRequest;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.bean.mapping.MappingUpdate;
import net.media.autotemplate.bean.mapping.SeasonalTemplate;
import net.media.autotemplate.constants.ConfigConstants;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.db.CMReportingDbUtil;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.util.BulkUtil;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
    Created by shubham-ar
    on 7/2/18 6:13 PM   
*/
public class BeanFactory {
    private static final Logger LOG = LogManager.getLogger(BeanFactory.class);
    public static final Gson GSON = Util.getGson();
    public static final String ENTITY = "ENTITY";

    public static AdtagInfo makeAdtagInfo(String entity, CreativeConstants.Level level) throws DatabaseException {

        String customerId = null, partnerId = null;
        Long entId = null, adtagId = null;
        String domain = null;
        try {
            switch (level) {
                case GLOBAL:
                    break;
                case PARTNER:
                    partnerId = entity;
                    break;
                case CUSTOMER:
                    customerId = entity;
                    partnerId = CMReportingDbUtil.getPartnerIdForCustomer(entity);
                    break;
                case DOMAIN:
                    domain = entity;
                    break;
                case ADTAG:
                    Pair<String, String> partnerCustomer = CMReportingDbUtil.getPartnerCustomerforAdtag(entity);
                    adtagId = Long.parseLong(entity);
                    assert partnerCustomer != null;
                    partnerId = partnerCustomer.first;
                    customerId = partnerCustomer.second;
                    break;

                case ENTITY:
                    entId = Long.parseLong(entity);
                    Entity atEntity = AutoTemplateDAL.getEntityInfo(entId);
                    adtagId = atEntity.getAdtagIdAsLong();
                    domain = atEntity.getDomain();
                    customerId = atEntity.getCustomerId();
                    partnerId = atEntity.getPartnerId();
                    break;
                case AD:
                    return new AdtagInfo().setAdId(Long.parseLong(entity));
                case CAMPAIGN:
                    return new AdtagInfo().setCampaignId(Long.parseLong(entity));
                case ADGROUP:
                    return new AdtagInfo().setAdgroupId(Long.parseLong(entity));
                case ADVERTISER:
                    return new AdtagInfo().setAdvertiserId(Long.parseLong(entity));
                case PORTFOLIO:
                    return new AdtagInfo().setPortfolioId(entity);
                case ADDOMAIN:
                    return new AdtagInfo().setAdDomain(entity);
            }
        } catch (Exception e) {
            LoggingService.log(LOG, Level.error, "Could not make AdtagInfo", e);
        }

        return new AdtagInfo(entId, adtagId, customerId, partnerId, domain);
    }

    public static AdtagInfo makeAdtagInfo(Entity entity) {
        return new AdtagInfo(entity.getEntityId(), entity.getAdtagIdAsLong(), entity.getCustomerId(), entity.getPartnerId(), entity.getDomain());
    }

    public static MappingUpdate<? extends MappingTemplate> makeMappingUpdate(CreativeConstants.MappingType mappingType, String json, SupplyDemandHierarchy supplyDemandHierarchy) {
        MappingUpdate<? extends MappingTemplate> mappingUpdate = null;
        MappingRequest mappingRequest = GSON.fromJson(json, MappingRequest.class);
        switch (mappingType) {
            case SEASONAL:
                List<SeasonalTemplate> seasonalTemplates = new ArrayList<>();
                for (TemplateData templateData : mappingRequest.getTemplateDataList()) {
                    String buSelected = mappingRequest.getBuSelected();
                    seasonalTemplates.add(new SeasonalTemplate(templateData, mappingRequest.getMappingType(), supplyDemandHierarchy, buSelected, BusinessUnit.getBUFromName(buSelected).getDefaultSystemPage().getName()));
                }
                mappingUpdate = new MappingUpdate<>(seasonalTemplates);
                break;
            case MANUAL:
                List<MappingTemplate> mappingTemplates = new ArrayList<>();
                for (TemplateData templateData : mappingRequest.getTemplateDataList()) {
                    String buSelected = mappingRequest.getBuSelected();
                    mappingTemplates.add(new MappingTemplate(templateData, mappingRequest.getMappingType(), supplyDemandHierarchy, buSelected, BusinessUnit.getBUFromName(buSelected).getDefaultSystemPage().getName()));
                }
                mappingUpdate = new MappingUpdate<>(mappingTemplates);
                break;
        }
        return mappingUpdate;
    }

    public static Map<String, Long> getEntityIdFromDomainAdTag(List<ATRequestDetail> atRequestDetailList, long adminId) throws Exception {
        Map<String, Pair<String, String>> entityKeyToDomainAdTagMap = getDistinctDomainAdTag(atRequestDetailList);
        List<Pair<String, String>> domainAdTagPairList = new ArrayList<>(entityKeyToDomainAdTagMap.values());

        List<EntityInfo> entityInfoList = new ArrayList<>();
        if (!domainAdTagPairList.isEmpty()) {
            entityInfoList = AutoTemplateDAL.getEntityIds(domainAdTagPairList);
        }

        Map<String, Long> finalEntityData = entityInfoList.stream()
                .collect(Collectors.toMap(e -> e.getDomain().trim() + ConfigConstants.UNDERSCORE_SEPARATOR + e.getAdtagId().trim(),
                        e -> e.getEntityId(),
                        (x, y) -> x)
                );

        List<Pair<String, String>> remainingDomainAdTag = Util.getRemainingEntriesToInsert(entityKeyToDomainAdTagMap.keySet(), finalEntityData);
        if (!remainingDomainAdTag.isEmpty()) {
            AutoTemplateDAL.insertEntityHierarchyMaster(remainingDomainAdTag, adminId);
            List<EntityInfo> remainingEntityInfoList = AutoTemplateDAL.getEntityIds(remainingDomainAdTag);
            Map<String, Long> remainingEntityData = remainingEntityInfoList.stream()
                    .collect(Collectors.toMap(
                            e -> e.getDomain().trim() + ConfigConstants.UNDERSCORE_SEPARATOR + e.getAdtagId().trim(),
                            e -> e.getEntityId(),
                            (x, y) -> x));
            finalEntityData.putAll(remainingEntityData);
        }

        return finalEntityData;
    }

    private static Map<String, Pair<String, String>> getDistinctDomainAdTag(List<ATRequestDetail> atRequestDetailList) {
        Map<String, Pair<String, String>> entityKeyToDomainAdTagMap = new HashMap<>();
        for (ATRequestDetail atRequestDetail : atRequestDetailList) {
            if (atRequestDetail.getSupplyHierarchyLevel().equalsIgnoreCase(ENTITY)) {
                String[] split = atRequestDetail.getSupplyEntityValue().split(ConfigConstants.PIPE_SEPARATOR);
                String domain = split[0].trim();
                String adTag = split[1].trim();
                String entityKey = domain + ConfigConstants.UNDERSCORE_SEPARATOR + adTag;
                entityKeyToDomainAdTagMap.put(entityKey, new Pair<>(domain, adTag));
            }
        }
        return entityKeyToDomainAdTagMap;
    }


    public static List<MappingTemplate> getTemplates(List<ATRequestDetail> atRequestDetailList, long adminId, ATRequestType atRequestType) throws Exception {
        Map<String, MappingTemplate> keyToMappingTemplatesMapping = new HashMap<>();

        Map<String, Long> dATagToEntityIdMap = getEntityIdFromDomainAdTag(atRequestDetailList, adminId);
        for (ATRequestDetail atRequestDetail : atRequestDetailList) {
            BusinessUnit businessUnit = atRequestDetail.getBusinessUnit();
            String supplyEntityValue = getSupplyEntityValue(dATagToEntityIdMap, atRequestDetail);
            List<String> templateSizes = BulkUtil.getTemplateSizeListFromString(atRequestDetail.getTemplateSizes() , atRequestType);
            for (String tId : atRequestDetail.getTemplateIdList()) {
                for (String tSize : templateSizes) {
                    MappingTemplate mappingTemplate = createMappingTemplate(atRequestDetail, businessUnit, supplyEntityValue, tId, tSize);
                    keyToMappingTemplatesMapping.put(mappingTemplate.getKey(), mappingTemplate);
                }
            }
        }
        return new ArrayList<>(keyToMappingTemplatesMapping.values());
    }

    private static MappingTemplate createMappingTemplate(ATRequestDetail atRequestDetail, BusinessUnit businessUnit, String supplyEntityValue, String tId, String tSize) {
        return new MappingTemplate(
                new TemplateData(tId.trim(),
                        tSize.trim()
                ),
                CreativeConstants.MappingType.getMappingTypeEnumFromName(atRequestDetail.getMappingType().toUpperCase()),
                new SupplyDemandHierarchy(atRequestDetail.getSupplyHierarchyLevel(),
                        supplyEntityValue,
                        atRequestDetail.getDemandHierarchyLevel(),
                        atRequestDetail.getDemandEntityValue()
                ),
                businessUnit.getName(),
                atRequestDetail.getSystemPageType().getName()
        );
    }

    public static ArrayList<BlockingTemplate> getBlockingTemplates(List<ATRequestDetail> atRequestDetailList, long adminId) throws Exception {
        Map<String, BlockingTemplate> keyToBlockingTemplatesMap = new HashMap<>();

        Map<String, Long> dATagToEntityIdMap = getEntityIdFromDomainAdTag(atRequestDetailList, adminId);
        for (ATRequestDetail atRequestDetail : atRequestDetailList) {
            String supplyEntityValue = getSupplyEntityValue(dATagToEntityIdMap, atRequestDetail);
            List<String> templateSizes = BulkUtil.getTemplateSizeListFromString(atRequestDetail.getTemplateSizes(), ATRequestType.TEMPLATE_BLOCKING);

            for (String tId : atRequestDetail.getTemplateIdList()) {
                for (String tSize : templateSizes) {
                    BlockingTemplate blockingTemplate = createBlockingTemplate(atRequestDetail, atRequestDetail.getBusinessUnit(), supplyEntityValue, tId, tSize);
                    keyToBlockingTemplatesMap.put(blockingTemplate.getKey(), blockingTemplate);
                }
            }
        }
        return new ArrayList<>(keyToBlockingTemplatesMap.values());
    }

    private static BlockingTemplate createBlockingTemplate(ATRequestDetail atRequestDetail, BusinessUnit businessUnit, String supplyEntityValue, String tId, String tSize) {
        return new BlockingTemplate(
                new TemplateData(tId.trim(), tSize.trim()),
                new SupplyDemandHierarchy(
                        atRequestDetail.getSupplyHierarchyLevel(),
                        supplyEntityValue,
                        atRequestDetail.getDemandHierarchyLevel(),
                        atRequestDetail.getDemandEntityValue()
                ),
                businessUnit.getName(),
                atRequestDetail.getSystemPageType().getName(),
                CreativeConstants.Type.getTypeEnumFromName(atRequestDetail.getCreativeType())
        );
    }


    private static String getSupplyEntityValue(Map<String, Long> dATagToEntityIdMap, ATRequestDetail atRequestDetail) {
        String supplyEntityValue = atRequestDetail.getSupplyEntityValue();
        if (atRequestDetail.getSupplyHierarchyLevel().equalsIgnoreCase(ENTITY)) {
            String[] split = atRequestDetail.getSupplyEntityValue().split(ConfigConstants.PIPE_SEPARATOR);
            String domain = split[0].trim();
            String adTag = split[1].trim();
            String entityKey = domain + ConfigConstants.UNDERSCORE_SEPARATOR + adTag;
            supplyEntityValue = String.valueOf(dATagToEntityIdMap.get(entityKey));
        }
        return supplyEntityValue;
    }

    public static MappingUpdate<MappingTemplate> makeMappingUpdateForEntity(Entity entity, String templateStr, String buSelected) {
        BusinessUnit businessUnit = BusinessUnit.getBUFromName(buSelected);
        MappingUpdate<MappingTemplate> mappingUpdate = new MappingUpdate<>();
        List<MappingTemplate> list = new ArrayList<>();
        String[] templates = templateStr.split("\\s*,\\s*");
        for (int i = 0; i < templates.length; i++) {
            String template = templates[i];
            MappingTemplate mappingTemplate = new MappingTemplate(String.valueOf(entity.getEntityId()), CreativeConstants.Level.ENTITY.name(), template, entity.getSize(), businessUnit.getId(), businessUnit.getDefaultSystemPage().getId());
            list.add(mappingTemplate);
        }
        mappingUpdate.setUpdates(list);
        return mappingUpdate;
    }
}
