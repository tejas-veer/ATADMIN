package net.media.autotemplate.dal.db;

import net.media.autotemplate.bean.FrameworkSize;
import net.media.autotemplate.bean.TemplateMetaInfo;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vaibhav.si
 * on 10/07/17.
 */
public class CMTemplateDbUtil extends Database {

    private static final CMTemplateDbUtil DB = new CMTemplateDbUtil(DbConstants.CM_GLOBAL_MASTER);

    public CMTemplateDbUtil(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static Set<Integer> getFrameworkPageSizeIds(long frameworkId) throws Exception {
        StoredProcedureCall<Integer> sp1 = new StoredProcedureCall<>("Get_HTMLFramework_Page_Details_Test_Admin", rs -> rs.getInt("size_id"));
        sp1.addParameter("html_framework_id", frameworkId, Types.BIGINT);
        sp1.addParameter("page_name", "keywords-only", Types.VARCHAR);
        List<Integer> sizeIdList = DB.executeQuery(sp1);
        return new HashSet<>(sizeIdList);
    }

    public static List<FrameworkSize> getAllFrameworkSize() throws Exception {
        StoredProcedureCall<FrameworkSize> sp = new StoredProcedureCall<FrameworkSize>("CM_Get_Creative_Size", rs -> new FrameworkSize(rs.getInt("size_id"), rs.getInt("width"), rs.getInt("height"), rs.getString("width_by_height")));
        return DB.executeQuery(sp);
    }

    public static TemplateMetaInfo getTemplateMetaInfo(String templateId) throws DatabaseException {
        StoredProcedureCall<TemplateMetaInfo> sp1 = new StoredProcedureCall<>("CM_Get_Template_List", RowMappers.TEMPLATE_META_INFO_MAPPER_V2());
        sp1.addParameter("template_id", templateId, Types.BIGINT);
        return DB.executeQuery(sp1).get(0);
    }

    public static String getFrameworkName(String frameworkId) throws DatabaseException {
        StoredProcedureCall<String> sp1 = new StoredProcedureCall<>("Get_HTMLFramework", rs -> rs.getString("html_framework_name"));
        sp1.addParameter("html_framework_id", frameworkId, Types.BIGINT);
        return DB.executeQuery(sp1).get(0);
    }
}
