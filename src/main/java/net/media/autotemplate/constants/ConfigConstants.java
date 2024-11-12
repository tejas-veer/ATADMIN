package net.media.autotemplate.constants;

import net.media.autotemplate.util.SysProperties;

public class ConfigConstants {
    public static boolean LOCAL = SysProperties.getPropertyAsBoolean("LOCAL");
    public static boolean IS_CRON = SysProperties.getPropertyAsBoolean("IS_CRON");
    public static final String WHITE_LIST = "W";
    public static final String BLACK_LIST = "B";
    public static final String GLOBAL_CONFIG = "GLOBAL_CONFIG";
    public static final String GLOBAL_ENTITY = "GLOBAL_ENTITY";
    public static final String AD_ATTRIBUTION_PROPERTY = "AD_ATTRIBUTION";
    public static final String HEADER_TEXT_PROPERTY = "HEADER_TEXT";
    public static final String AD_ATTRIBUTION_SEPARATOR = "||";
    public static final String AD_ATTRIBUTION_SEPARATOR_SPLIT = "\\|\\|";
    public static final String GENERATOR_BASE_URL = SysProperties.getProperty("GENERATOR_BASE_URL");
    public static final String PIPE_SEPARATOR = "\\|";
    public static final String UNDERSCORE_SEPARATOR = "_";
    public static final String MAX_ACL_PROPERTY = "MAX_ACL";
    public static final String COMMA_SEPARATED = ",";
    public static final int MAX_RETRY = 3;
    public static final String DOLLAR_SEPARATOR = "$";
    public static final String DOUBLE_DOLLAR_SEPARATOR = "$$";
    public static final String DOUBLE_DOLLAR_REGEX_SEPARATOR = "\\$\\$";


}
