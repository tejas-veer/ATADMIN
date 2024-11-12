package net.media.autotemplate.services.mapping;

import com.google.gson.Gson;
import net.media.autotemplate.bean.*;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.bean.mapping.MappingUpdate;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.mapping.TemplateMappingDal;
import net.media.autotemplate.factory.BeanFactory;
import net.media.autotemplate.services.AclService;
import net.media.autotemplate.util.Util;
import net.media.autotemplate.util.logging.UserActionLogging;
import net.media.database.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/*
    Created by shubham-ar
    on 27/3/18 2:58 PM   
*/
public class MappingService {
    protected static final Logger LOG = LogManager.getLogger(MappingService.class);
    protected static final Gson GSON = Util.getGson();
    protected final Admin admin;
    protected final CreativeConstants.MappingType mappingType;
    private final SupplyDemandHierarchy supplyDemandHierarchy;
    private final BusinessUnit businessUnit;
    private final AclStatus aclStatus;

    public MappingService(SupplyDemandHierarchy supplyDemandHierarchy, CreativeConstants.MappingType mappingType, String buSelected) throws DatabaseException {
        this.supplyDemandHierarchy = supplyDemandHierarchy;
        this.mappingType = mappingType;
        this.admin = RequestGlobal.getAdmin();
        this.businessUnit = BusinessUnit.getBUFromName(buSelected);
        this.aclStatus = AclService.validateAccess(supplyDemandHierarchy, RequestGlobal.getAdmin(), this.businessUnit);
    }

    public CreativeConstants.MappingType getMappingType() {
        return mappingType;
    }

    public Admin getAdmin() {
        return admin;
    }

    public List<MappingTemplate> getTemplates() throws Exception {
        return TemplateMappingDal.getMappedTemplates(mappingType, supplyDemandHierarchy, businessUnit);
    }

    public AclStatus getAclStatus() {
        return aclStatus;
    }

    public MappingUpdate<? extends MappingTemplate> insertMappings(String updateJson) throws Exception {
        MappingUpdate<? extends MappingTemplate> mappingUpdate = BeanFactory.makeMappingUpdate(mappingType, updateJson, supplyDemandHierarchy);
        return insertMappings(mappingUpdate);
    }

    public MappingUpdate<? extends MappingTemplate> insertMappings(MappingUpdate<? extends MappingTemplate> mappingUpdate) throws Exception {
        TemplateMappingDal.insertMapping(mappingUpdate.getUpdates(), this.admin.getAdminId());
        UserActionLogging.log(admin, "INSERTED_MANUAL_TEMPLATE_MAPPING", GSON.toJson(mappingUpdate));
        return mappingUpdate;
    }

    public MappingUpdate<? extends MappingTemplate> deleteMappings(String updateJson) throws Exception {
        MappingUpdate<? extends MappingTemplate> mappingUpdate = BeanFactory.makeMappingUpdate(mappingType, updateJson, supplyDemandHierarchy);
        TemplateMappingDal.deleteMapping(mappingUpdate.getUpdates(), this.admin);
        UserActionLogging.log(admin, "DELETED_MANUAL_TEMPLATE_MAPPINGS", GSON.toJson(updateJson));
        return mappingUpdate;
    }

}
