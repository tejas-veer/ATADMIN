package net.media.autotemplate.util;

import com.google.common.collect.Lists;
import net.media.autotemplate.bean.ATRequestDetail;
import net.media.autotemplate.bean.blocking.BlockingTemplate;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.dal.blocking.TemplateBlockingDal;
import net.media.autotemplate.dal.db.AutoTemplateDAL;
import net.media.autotemplate.dal.mapping.TemplateMappingDal;
import net.media.autotemplate.enums.ATRequestType;
import net.media.autotemplate.factory.BeanFactory;
import net.media.autotemplate.util.logging.Level;
import net.media.autotemplate.util.logging.LoggingService;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateBulkCronUtil {
    private static final Logger LOGGER = LogManager.getLogger(TemplateBulkCronUtil.class);
    private static final int WRITE_BATCH_SIZE = 250;

    public static void insertMapping(List<ATRequestDetail> atRequestDetailList) {
        try {
            if (!Util.empty(atRequestDetailList)) {
                long adminId = atRequestDetailList.get(0).getAdminId();
                List<MappingTemplate> mappingTemplateList = BeanFactory.getTemplates(atRequestDetailList, adminId, ATRequestType.TEMPLATE_MAPPING);
                if (!Util.empty(mappingTemplateList)) {
                    List<List<MappingTemplate>> batchesForInsertion = Lists.partition(mappingTemplateList, WRITE_BATCH_SIZE);
                    for (List<MappingTemplate> mappingTemplatePartition : batchesForInsertion) {
                        TemplateMappingDal.insertMapping(mappingTemplatePartition, adminId);
                    }
                }
            }
            AutoTemplateDAL.updateRequestDetails(atRequestDetailList);
        } catch (Exception e) {
            LoggingService.log(LOGGER, Level.error, "insertMappings", e);
        }
    }

    public static void insertUnmapping(List<ATRequestDetail> atRequestDetailList) {
        try {
            if (!Util.empty(atRequestDetailList)) {
                long adminId = atRequestDetailList.get(0).getAdminId();
                List<MappingTemplate> unmappingTemplateList = BeanFactory.getTemplates(atRequestDetailList, adminId, ATRequestType.TEMPLATE_UNMAPPING);
                if (!Util.empty(unmappingTemplateList)) {
                    List<List<MappingTemplate>> batchesForInsertion = Lists.partition(unmappingTemplateList, WRITE_BATCH_SIZE);
                    for (List<MappingTemplate> unmappingTemplatePartition : batchesForInsertion) {
                        TemplateMappingDal.unmappingTemplate(unmappingTemplatePartition, adminId);
                    }
                }
            }
            AutoTemplateDAL.updateRequestDetails(atRequestDetailList);
        } catch (Exception e) {
            LoggingService.log(LOGGER, Level.error, "insertUnmapping", e);
        }
    }

    public static void insertBlocking(List<ATRequestDetail> atRequestDetailList) {
        try {
            if (!Util.empty(atRequestDetailList)) {
                long adminId = atRequestDetailList.get(0).getAdminId();
                List<BlockingTemplate> blockingTemplateList = BeanFactory.getBlockingTemplates(atRequestDetailList, adminId);
                if (!Util.empty(blockingTemplateList)) {
                    List<List<BlockingTemplate>> batchesForInsertion = Lists.partition(blockingTemplateList, WRITE_BATCH_SIZE);
                    for (List<BlockingTemplate> blockingTemplatePartition : batchesForInsertion) {
                        TemplateBlockingDal.insertEntityWisCreativeStatus(blockingTemplatePartition, adminId);
                    }
                }
            }
            AutoTemplateDAL.updateRequestDetails(atRequestDetailList);
        } catch (Exception e) {
            LoggingService.log(LOGGER, Level.error, "insertBlocking", e);
        }
    }
}
