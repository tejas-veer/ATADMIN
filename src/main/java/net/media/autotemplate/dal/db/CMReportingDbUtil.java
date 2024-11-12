package net.media.autotemplate.dal.db;

import com.google.gson.JsonObject;
import net.media.autotemplate.bean.Customer;
import net.media.autotemplate.bean.Pair;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.List;

/**
 * Created by shubham.ar
 * on 12/09/17.
 */

public class CMReportingDbUtil extends Database {

    private static final CMReportingDbUtil DB = new CMReportingDbUtil(DbConstants.ADMIN_DETAILS_CONFIG);

    public CMReportingDbUtil(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }


    public static String getPartnerIdForCustomer(String customerId) throws DatabaseException {
        StoredProcedureCall<String> sp = new StoredProcedureCall<>("AOPT_Get_Partner_From_Customer", rs -> rs.getString("partner_id"));
        sp.addParameter("customer_id", customerId, Types.VARCHAR);
        List<String> t = DB.executeQuery(sp);
        return t.size() > 0 ? t.get(0) : null;
    }

    @Deprecated
    public static Pair<String, String> getPartnerCustomerforAdtag(String adtagId) throws DatabaseException {
        StoredProcedureCall<Pair<String, String>> sp = new StoredProcedureCall<>("AOPT_Get_Partner_Customer_From_Adtag", rs -> new Pair<>(rs.getString("partner_id"), rs.getString("customer_id")));
        sp.addParameter("ad_tag_id", adtagId, Types.INTEGER);
        sp.addParameter("filtered", 0, Types.INTEGER);
        List<Pair<String, String>> t = DB.executeQuery(sp);
        return t.size() > 0 ? t.get(0) : null;
    }


    public static List<JsonObject> getCustomerGroups() throws DatabaseException {
        StoredProcedureCall<JsonObject> sp = new StoredProcedureCall<>("AOPT_Get_Customer_Wise_Customer_Groups", rs -> {
            JsonObject resp = new JsonObject();
            resp.addProperty("customer_id", rs.getString("customer_id"));
            resp.addProperty("group_ids", rs.getString("customer_groups"));
            return resp;
        });
        return DB.executeQuery(sp);
    }

    public static void main(String[] args) throws DatabaseException {
        System.out.println(getPartnerCustomerforAdtag("930017673"));
    }
}
