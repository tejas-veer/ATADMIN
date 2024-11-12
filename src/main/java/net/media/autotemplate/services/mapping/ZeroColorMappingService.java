//package net.media.autotemplate.services.mapping;
//
//import net.media.autotemplate.bean.Admin;
//import net.media.autotemplate.bean.AdtagInfo;
//import net.media.autotemplate.bean.mapping.MappingTemplate;
//import net.media.autotemplate.bean.mapping.MappingUpdate;
//import net.media.autotemplate.constants.CreativeConstants;
//import net.media.autotemplate.dal.mapping.EntityWiseZeroColorTemplateMapping;
//import net.media.autotemplate.factory.BeanFactory;
//import net.media.autotemplate.routes.util.IllegalActionApiException;
//import net.media.autotemplate.util.logging.UserActionLogging;
//import net.media.database.DatabaseException;
//import org.eclipse.jetty.server.Authentication;
//
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//
///*
//    Created by shubham-ar
//    on 17/4/18 5:30 PM
//*/
//
//@Deprecated
//public class ZeroColorMappingService extends MappingService {
//
//    public ZeroColorMappingService(CreativeConstants.Level level, String entity, Admin admin, AdtagInfo adtagInfo, CreativeConstants.MappingType mappingType) throws ExecutionException, DatabaseException, Authentication.Failed {
//        super(level, entity, admin, adtagInfo, mappingType);
//    }
//
//    @Override
//    public List<MappingTemplate> getTemplates() throws Exception {
//        return EntityWiseZeroColorTemplateMapping.getMappedTemplates(mappingType, adtagInfo, level);
//    }
//
//    @Override
//    public MappingUpdate insertMappings(String updateJson) throws Exception {
//        throw new IllegalActionApiException("Zero color insertion is done through generator", this);
//    }
//
//    @Override
//    public MappingUpdate deleteMappings(String updateJson) throws Exception {
//        MappingUpdate<? extends MappingTemplate> mappingUpdate = BeanFactory.makeMappingUpdate(mappingType, updateJson);
//        EntityWiseZeroColorTemplateMapping.deleteMapping(mappingUpdate.getUpdates(), this.admin);
//        UserActionLogging.log(admin, "DELETED_ZERO_COLOR_MAPPINGS", GSON.toJson(updateJson));
//        return mappingUpdate;
//    }
//}
