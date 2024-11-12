package net.media.autotemplate.dal.db;//  Copyright (C) 2017 Media.net Advertising FZ-LLC All Rights Reserved

import net.media.database.DatabaseConfig;
import net.media.database.DatabaseTypeImpl;

/**
 * Created by sumeet
 * on 21/4/17.
 */

public class DbConstants {

    public static final String DEFAULT_TEMPLATE_SIZE = "300x250";

    public enum Source {
        AT_GENERATED,
        MANUAL,
        UNKNOWN,
        BANDIT_MAPPED

    }

    public static final DatabaseConfig KEYWORD_MASTER_C8_LEARNING = new DatabaseConfig("KEYWORD_MASTER",
            DatabaseTypeImpl.MYSQL,
            "output_web",
            "sdAQJXQ9wq",
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://adc-learning-db.srv.media.net:3306/KEYWORD_MASTER"
    );

    public static final DatabaseConfig AUTO_TEMPLATE_ADC = new DatabaseConfig("AUTO_TEMPLATE",
            DatabaseTypeImpl.MYSQL,
            "output_web",
            "sdAQJXQ9wq",
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://adc-learning-db.srv.media.net:3306/AUTO_TEMPLATE"
    );

    public static final DatabaseConfig SEARCH_RETARGETING_ADC = new DatabaseConfig("AUTO_TEMPLATE",
            DatabaseTypeImpl.MYSQL,
            "output_web",
            "sdAQJXQ9wq",
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://adc-learning-db.srv.media.net:3306/SEARCH_RETARGETING"
    );

    public static final DatabaseConfig CM_GLOBAL_MASTER = new DatabaseConfig("GLOBAL_MASTER",
            DatabaseTypeImpl.MSSQL,
            "mnetweb-aopt",
            "BFwTfGJXYdEs",
            "net.sourceforge.jtds.jdbc.Driver",
            "jdbc:jtds:sqlserver://172.16.200.101;databaseName=GLOBAL_MASTER"
    );

    public static final DatabaseConfig ADMIN_DETAILS_CONFIG = new DatabaseConfig("REPLICATOR",
            DatabaseTypeImpl.MSSQL,
            "mnetweb-aopt-dr",
            "yppjWvGljQvRs6ZkwvR",
            "net.sourceforge.jtds.jdbc.Driver",
            "jdbc:jtds:sqlserver://172.16.208.70:1533;databaseName=REPLICATOR"
    );

    public static final DatabaseConfig CM_KEYWORD_DB = new DatabaseConfig("CM_KEYWORD_DB",
            DatabaseTypeImpl.MSSQL,
            "mnetweb-cmadm1",
            "r#u-W\\arTZ6rMW;u",
            "net.sourceforge.jtds.jdbc.Driver",
            "jdbc:jtds:sqlserver://172.16.200.153;databaseName=CM_KEYWORD_DB"
    );
}
