package net.media.autotemplate.dal.blocking;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.BusinessUnit;
import net.media.autotemplate.bean.blocking.BlockingInfo;
import net.media.autotemplate.bean.blocking.BlockingTemplate;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.dal.db.DbConstants;
import net.media.autotemplate.util.XmlUtil;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

/*
    Created by shubham-ar
    on 13/2/18 9:57 PM   
*/
public class TemplateBlockingDal extends Database {
    private static final TemplateBlockingDal DB = new TemplateBlockingDal(DbConstants.AUTO_TEMPLATE_ADC);


    public TemplateBlockingDal(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static void insertEntityWisCreativeStatus(String entity, String level, List<BlockingInfo> blockingInfoList, Admin admin, BusinessUnit businessUnit) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("INSERT_ENTITY_WISE_FRAMEWORK_STATUS_V3");
        sp.addParameter("entity", entity, Types.VARCHAR);
        sp.addParameter("hierarchy", level, Types.VARCHAR);
        sp.addParameter("admin_id", admin.getAdminId(), Types.INTEGER);
        sp.addParameter("status_xml", XmlUtil.getBlockingInfoXML(blockingInfoList, businessUnit), Types.LONGVARCHAR);
        DB.executeUpdate(sp);
    }

    public static void insertEntityWisCreativeStatus(List<BlockingTemplate> blockingTemplates, long adminId) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall("INSERT_ENTITY_WISE_FRAMEWORK_STATUS_XML");
        sp.addParameter("admin_id", adminId, Types.INTEGER);
        sp.addParameter("status_xml", XmlUtil.getUpsertTemplateXML(blockingTemplates), Types.LONGVARCHAR);
        DB.executeUpdate(sp);
    }

    public static List<BlockingInfo> getEntityWiseCreativeStatus(String entity, String level, BusinessUnit businessUnit) throws DatabaseException {
        StoredProcedureCall<BlockingInfo> sp = new StoredProcedureCall<>("GET_ENTITY_WISE_FRAMEWORK_STATUS_V3", rs ->
                new BlockingInfo(rs.getString("framework_id"),
                        CreativeConstants.Type.getTypeEnumFromName(rs.getString("type")),
                        rs.getString("framework_size"),
                        CreativeConstants.Status.getStatusEnumFromName(rs.getString("status")),
                        level,
                        rs.getString("entity"),
                        rs.getString("creation_date"),
                        rs.getString("admin_name")));

        sp.addParameter("entity", entity, Types.VARCHAR);
        sp.addParameter("hierarchy", level, Types.VARCHAR);
        sp.addParameter("bunit", businessUnit.getId(), Types.INTEGER);
        List<BlockingInfo> blockingInfos = DB.executeQuery(sp);
        return blockingInfos.stream().filter(b -> !(b.getType().equals(CreativeConstants.Type.NULL)) && !(b.getStatus().equals(CreativeConstants.Status.NA))).collect(Collectors.toList());
    }
}
