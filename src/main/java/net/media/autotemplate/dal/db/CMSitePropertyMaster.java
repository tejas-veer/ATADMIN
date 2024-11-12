//  Copyright (C) 2017 Media.net Advertising FZ-LLC All Rights Reserved

package net.media.autotemplate.dal.db;

import net.media.autotemplate.bean.Pair;
import net.media.autotemplate.bean.autoasset.SitePropertyDetail;
import net.media.autotemplate.util.XmlUtil;
import net.media.database.Database;
import net.media.database.DatabaseConfig;
import net.media.database.DatabaseException;
import net.media.database.StoredProcedureCall;

import java.sql.Types;
import java.util.List;

/**
 * Created by sumeet
 * on 19/6/17.
 */
public class CMSitePropertyMaster extends Database {

    private static final CMSitePropertyMaster CM_KEYWORD_DB = new CMSitePropertyMaster(DbConstants.CM_KEYWORD_DB);

    public CMSitePropertyMaster(DatabaseConfig databaseConfig) {
        super(databaseConfig);
    }

    public static List<SitePropertyDetail> getCmSitePropertyMasterValue(List<Pair<String, String>> siteNameAndPropertiesList) throws DatabaseException {
        StoredProcedureCall<SitePropertyDetail> sp = new StoredProcedureCall<>("GET_CM_SITE_PROPERTY_MASTER", RowMappers.GET_CM_SITE_PROPERTY_MASTER_MAPPER());
        sp.addParameter("xml", XmlUtil.getCmSitePropertyMasterGetXml(siteNameAndPropertiesList), Types.VARCHAR);
        return CM_KEYWORD_DB.executeQuery(sp);
    }

    public static void updateCmSitePropertyMasterValue(List<SitePropertyDetail> sitePropertyDetailList, int isActive, String adminEmail) throws DatabaseException {
        StoredProcedureCall sp = new StoredProcedureCall<>("Insert_Update_CM_Site_Common_Property_Master_Bulk");
        sp.addParameter("xml", XmlUtil.getCmSitePropertyMasterUpdateXml(sitePropertyDetailList), Types.VARCHAR);
        sp.addParameter("is_active", isActive, Types.INTEGER);
        sp.addParameter("admin_email", adminEmail, Types.VARCHAR);
        sp.addParameter("kbt", 0, Types.TINYINT);
        CM_KEYWORD_DB.executeUpdate(sp);
    }
}
