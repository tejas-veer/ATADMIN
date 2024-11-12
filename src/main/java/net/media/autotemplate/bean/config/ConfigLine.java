package net.media.autotemplate.bean.config;

import net.media.autotemplate.bean.EntityConfig;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.util.Util;

/*
    Created by shubham-ar
    on 12/3/18 1:03 PM   
*/
public class ConfigLine extends EntityConfig {
    private final String environment;
    private final String updation_date;
    private final String admin_id;
    private CreativeConstants.Level level;

    public ConfigLine(String entity, String property, String value, String environment, String updation_date, String admin_id, CreativeConstants.Level level) {
        super(entity, property, value);
        this.environment = environment;
        this.updation_date = updation_date;
        this.admin_id = admin_id;
        this.level = level;
    }

    public ConfigLine(String entity, String property, String value, String environment, String updation_date, String admin_id) {
        this(entity, property, value, environment, updation_date, admin_id, null);
    }

    public String getEnvironment() {
        return environment;
    }

    public String getUpdation_date() {
        return updation_date;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public CreativeConstants.Level getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return super.toString() + "|" + Util.getGson().toJson(this);
    }

    public void setLevel(CreativeConstants.Level level) {
        this.level = level;
    }

}
