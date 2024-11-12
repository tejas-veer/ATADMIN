package net.media.autotemplate.dal.db;

import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.List;

/*
    Created by shubham-ar
    on 1/11/17 4:58 PM   
*/
public class C8LearningKeyMasterUtils extends Database {
    private static final C8LearningKeyMasterUtils DB = new C8LearningKeyMasterUtils(DbConstants.KEYWORD_MASTER_C8_LEARNING);

    public C8LearningKeyMasterUtils(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static Integer getSizeIdForAdtag(String adtagId) throws DatabaseException {
        StoredProcedureCall<Integer> sp = new StoredProcedureCall<>("GET_SIZE_FROM_ADTAG", rs -> {
            return Integer.parseInt(rs.getString("size_id"));
        });
        sp.addParameter("ad_tag_id", adtagId, Types.VARCHAR);
        List<Integer> results = DB.executeQuery(sp);
        if (results.isEmpty())
            return null;
        return results.get(0);
    }

    public static void main(String[] args) throws DatabaseException {
        System.out.println(getSizeIdForAdtag("201316168"));
    }
}
