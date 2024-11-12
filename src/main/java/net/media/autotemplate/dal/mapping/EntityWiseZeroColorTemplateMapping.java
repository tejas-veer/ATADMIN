package net.media.autotemplate.dal.mapping;

import net.media.autotemplate.dal.db.DbConstants;
import net.media.database.Database;
import net.media.database.DatabaseConfig;

/*
    Created by shubham-ar
    on 28/3/18 1:11 PM   
*/
@Deprecated
public class EntityWiseZeroColorTemplateMapping extends Database {

    private EntityWiseZeroColorTemplateMapping(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

//    public static List<MappingTemplate> getMappedTemplates(CreativeConstants.MappingType type, AdtagInfo adtagInfo, CreativeConstants.Level level) throws DatabaseException {
//        StoredProcedureCall<MappingTemplate> sp = new StoredProcedureCall<>("GET_ZERO_COLOR_TEMPLATES_XML", type.getRowMapper());
//        sp.addParameter("entity_xml", XmlUtil.getAdtagInfoXml(adtagInfo), Types.LONGVARCHAR);
//        return DB.executeQuery(sp);
//    }

//    public static void deleteMapping(List<? extends MappingTemplate> updates, Admin admin) throws DatabaseException {
//        StoredProcedureCall sp = new StoredProcedureCall("REMOVE_ZERO_COLOR_TEMPLATE_MAPPING_XML");
//        sp.addParameter("insert_xml", XmlUtil.getMappingInsertXML(updates, businessUnit), Types.LONGVARCHAR);
//        sp.addParameter("admin_id", admin.getAdminId(), Types.SMALLINT);
//        DB.executeUpdate(sp);
//    }
}
