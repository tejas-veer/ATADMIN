package net.media.keywordlearning.dal;

import net.media.database.*;
import net.media.keywordlearning.Utils.Utils;
import net.media.keywordlearning.beans.DomainAdTag;
import net.media.keywordlearning.mapper.MapperFactory;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * Created by autoopt/rohit.aga.
 */
public class KeywordMaster55DAL extends Database {
    private static final KeywordMaster55DAL DB = new KeywordMaster55DAL(new DatabaseConfig(
            "KEYWORD_MASTER_UNPOOLED",
            DatabaseTypeImpl.MSSQL,
            Utils.getProperty("userName"),
            Utils.getProperty("password"),
            Utils.getProperty("MSSQLDriver"),
            Utils.getProperty("keywordMasterSql55DbConnectUrl")));

    private KeywordMaster55DAL(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static List<DomainAdTag> getDomainAdTag() throws DatabaseException {
        String query = "SELECT * FROM KEYWORD_MASTER..PIRATE_DOMAIN_AD_TAG WITH(NOLOCK)";
        PSMaker psMaker = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query);
            return preparedStatement;
        };
        return DB.executeQuery(psMaker, MapperFactory.getDomainAdTagMapping());
    }
}
