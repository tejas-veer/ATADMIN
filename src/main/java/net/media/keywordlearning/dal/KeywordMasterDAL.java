package net.media.keywordlearning.dal;

import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.util.Util;
import net.media.database.*;
import net.media.keywordlearning.Utils.Utils;
import net.media.keywordlearning.beans.*;
import net.media.keywordlearning.mapper.MapperFactory;
import org.apache.commons.lang3.ObjectUtils;

import java.security.Key;
import java.sql.PreparedStatement;
import java.util.*;


/**
 * Created by autoopt/rohit.aga.
 */
public class KeywordMasterDAL extends Database {
    private static final KeywordMasterDAL DB = new KeywordMasterDAL(new DatabaseConfig(
            "LEARNING",
            DatabaseTypeImpl.MYSQL,
            Utils.getProperty("userName"),
            Utils.getProperty("password"),
            Utils.getProperty("MySQLDriver"),
            Utils.getProperty("keywordMasterMySqlDbConnectUrl")));

    private KeywordMasterDAL(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static Map<Integer, KeywordType> getKeywordTypeConfig() throws DatabaseException {
        String query = "SELECT * FROM KEYWORD_MASTER.GBL_KEYWORD_TYPE_MASTER WHERE keyword_type_name = broad_keyword_type and keyword_type_id > 0 ORDER BY keyword_type_id";
        PSMaker psMaker = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query);
            return preparedStatement;
        };
        return Utils.entrySetToMap(DB.executeQuery(psMaker, MapperFactory.getRpcRow()));
    }

    public static List<PirateLearnerId> getPirateLearnerId() throws DatabaseException {
        String query = "SELECT * FROM LEARNING_MASTER.PIRATE_LEARNER_ID";
        PSMaker psMaker = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query);
            return preparedStatement;
        };
        return DB.executeQuery(psMaker, MapperFactory.getPirateId());
    }

    public static List<GlobalBucket> getBucketId() throws DatabaseException {
        String query = "SELECT * FROM C8_LEARNING_DB.GLOBAL_AB_TEST_CONTROLLER WHERE is_active = 1";
        PSMaker psMaker = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query);
            return preparedStatement;
        };
        return DB.executeQuery(psMaker, MapperFactory.getGlobalBucketMapping());
    }

    public static List<PirateDebugResult> getPirateDataForBestUrls(String siteName, Integer keywordType, String basis, Integer urlCount, String canonHash, int actualData, int learnerId, int bucketId, int adTagId) throws DatabaseException{
        bucketId = bucketId == -115 ? -1 : bucketId;
        String procName = "LEARNING.GET_PIRATE_DEBUG_STATS_APP";
        Map<String, String> map = new HashMap<>();
        map.put("domain_name", "'" + siteName + "'");
        map.put("keyword_type_id", String.valueOf(keywordType));
        if(!Utils.isEmpty(basis)){
            map.put("basis", basis);
        }
        if(!Utils.isEmpty(urlCount)){
            map.put("num_of_urls", String.valueOf(urlCount));
        }
        if(!Utils.isEmpty(actualData)){
            map.put("actual_data", "1");
        }
        if(!Utils.isEmpty(learnerId)){
            map.put("learner_id", String.valueOf(learnerId));
        }
        if(!Utils.isEmpty(bucketId)){
            map.put("bucket_id", String.valueOf(bucketId));
        }
        if(adTagId > 0){
            map.put("ad_tag_id", String.valueOf(adTagId));
        }

        String dbProcString = Utils.getMySqlDBProcString(procName, map);

        PSMaker psMaker = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    dbProcString);
            return preparedStatement;
        };

        List<PirateDebugResult> pirateDebugResults = DB.executeQuery(psMaker, MapperFactory.getPirateDebugMapping());
        return pirateDebugResults;
    }
}
