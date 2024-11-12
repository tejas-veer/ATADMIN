package net.media.keywordlearning.mapper;

import net.media.database.RowMapper;
import net.media.keywordlearning.Utils.Utils;
import net.media.keywordlearning.beans.*;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Created by autoopt/rohit.aga.
 */
public class MapperFactory {
    public static RowMapper<Map.Entry<Integer, KeywordType>> getRpcRow() {
        return row -> new AbstractMap.SimpleEntry<>(
                row.getInt("keyword_type_id"),
                new KeywordType(row.getInt("keyword_type_id"), row.getString("keyword_type"))
        );
    }

    public static RowMapper<PirateLearnerId> getPirateId() {
        return row -> new PirateLearnerId(
                row.getBoolean("is_selected"),
                row.getString("description"),
                row.getInt("learner_id")
        );
    }


    public static RowMapper<DomainAdTag> getDomainAdTagMapping() {
        return row -> new DomainAdTag(
                row.getString("domain_name"),
                Utils.getListFromString(row.getString("ad_tag_id"))
        );
    }

    public static RowMapper<GlobalBucket> getGlobalBucketMapping() {
        return row -> new GlobalBucket(
                row.getInt("BUCKET_ID_NEW"),
                row.getString("bucket_name"));
    }

    public static RowMapper<PirateDebugResult> getPirateDebugMapping() {
        return row -> new PirateDebugResult(
                row.getString("canonical_hash"),
                row.getInt("keyword_type_id"),
                row.getDouble("impression"),
                row.getDouble("conversion"),
                row.getDouble("revenue"),
                row.getString("basis"),
                row.getDouble("scaled_impression"),
                row.getDouble("pirate_score"),
                row.getString("url"));
    }
}
