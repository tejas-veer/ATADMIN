package net.media.autotemplate.dal.mapping;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.SupplyDemandHierarchy;
import net.media.autotemplate.bean.mapping.MappingTemplate;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.DbConstants;
import net.media.autotemplate.util.XmlUtil;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.List;

/*
    Created by shubham-ar
    on 28/3/18 1:11 PM   
*/
public class TemplateMappingDal extends Database {
    private static TemplateMappingDal DB = new TemplateMappingDal(DbConstants.AUTO_TEMPLATE_ADC);

    private TemplateMappingDal(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static <T extends List<? extends MappingTemplate>> T getMappedTemplates(CreativeConstants.MappingType type, SupplyDemandHierarchy supplyDemandHierarchy, BusinessUnit businessUnit) throws DatabaseException {
        StoredProcedureCall<? extends MappingTemplate> sp = new StoredProcedureCall<>("GET_MAPPED_TEMPLATES_XML_V3", type.getRowMapper());
        sp.addParameter("set_id", type.getSetId(), Types.SMALLINT);
        sp.addParameter("entity_xml", XmlUtil.getSupplyDemandHierarchyXML(supplyDemandHierarchy.getHierarchyLevel(), supplyDemandHierarchy.getEntityValue(), businessUnit), Types.LONGVARCHAR);
        return (T) DB.executeQuery(sp);
    }

    public static void insertMapping(List<? extends MappingTemplate> updates, long adminId) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("INSERT_TEMPLATE_MAPPING_XML_V4");
        sp.addParameter("insert_xml", XmlUtil.getUpsertTemplateXML(updates), Types.LONGVARCHAR);
        sp.addParameter("admin_id", adminId, Types.SMALLINT);
        DB.executeUpdate(sp);
    }

    public static void unmappingTemplate(List<? extends MappingTemplate> updates, long adminId) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("UNMAP_TEMPLATE_MAPPING_XML");
        sp.addParameter("insert_xml", XmlUtil.getUpsertTemplateXML(updates), Types.LONGVARCHAR);
        sp.addParameter("admin_id", adminId, Types.SMALLINT);
        DB.executeUpdate(sp);
    }

    public static void deleteMapping(List<? extends MappingTemplate> updates, Admin admin) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("REMOVE_TEMPLATE_MAPPING_XML_V3");
        sp.addParameter("insert_xml", XmlUtil.getUpsertTemplateXML(updates), Types.LONGVARCHAR);
        sp.addParameter("admin_id", admin.getAdminId(), Types.SMALLINT);
        DB.executeUpdate(sp);
    }
}
