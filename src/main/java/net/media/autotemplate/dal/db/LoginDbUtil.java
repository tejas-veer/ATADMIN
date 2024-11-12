package net.media.autotemplate.dal.db;

import com.google.gson.JsonObject;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.List;

/**
 * Created by vaibhav.si on 28/06/17.
 */
public class LoginDbUtil extends Database {

    private static final LoginDbUtil DB = new LoginDbUtil(DbConstants.ADMIN_DETAILS_CONFIG);

    public LoginDbUtil(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static long getAdminId(String adminEmail) throws DatabaseException {
        StoredProcedureCall<Long> sp = new StoredProcedureCall<>("AOPT_Get_Admin_Id", rs -> rs.getLong("admin_id"));
        sp.addParameter("admin_email", adminEmail, Types.VARCHAR);
        List<Long> adminId = DB.executeQuery(sp);
        return adminId.isEmpty() ? -1 : adminId.get(0);
    }

    public static List<JsonObject> getCustomerGrpId(String adTagList) throws DatabaseException {

        StoredProcedureCall<JsonObject> sp = new StoredProcedureCall<>("AOPT_Get_Customer_Grp_Id_From_Adtag", rs -> {
            JsonObject resp = new JsonObject();
            resp.addProperty("adtagId", rs.getBigDecimal("ad_tag_id"));
            resp.addProperty("customerGrpId", rs.getLong("group_id"));
            return resp;
        });
        sp.addParameter("ad_tag_id", adTagList, Types.VARCHAR);
        return DB.executeQuery(sp);
    }

    public static List<String> getadminGrpDesc(Long adminId) throws DatabaseException {
        StoredProcedureCall<String> sp = new StoredProcedureCall<>("GET_ADMIN_GRP_DESC", rs -> rs.getString("admingroup_desc"));
        sp.addParameter("admin_id", adminId, Types.VARCHAR);
        return DB.executeQuery(sp);
    }

    public static List<Long> getCustomerGrpIdForAdmin(Long adminId) throws DatabaseException {
        StoredProcedureCall<Long> sp = new StoredProcedureCall<>("AOPT_Get_Customer_Grp_Id_From_Admin_Id", rs -> rs.getLong("group_id"));
        sp.addParameter("admin_id", adminId, Types.INTEGER);
        return DB.executeQuery(sp);
    }
}
