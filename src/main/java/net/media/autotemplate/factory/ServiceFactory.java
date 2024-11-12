package net.media.autotemplate.factory;

import net.media.autotemplate.bean.Admin;
import net.media.autotemplate.bean.Entity;
import net.media.autotemplate.bean.SupplyDemandHierarchy;
import net.media.autotemplate.constants.CreativeConstants;
import net.media.autotemplate.services.mapping.MappingService;
import net.media.database.DatabaseException;
import org.eclipse.jetty.server.Authentication;
import java.util.concurrent.ExecutionException;

/*
    Created by shubham-ar
    on 19/4/18 3:23 PM   
*/
public class ServiceFactory {
    public static MappingService getMappingService(SupplyDemandHierarchy supplyDemandHierarchy, CreativeConstants.MappingType mappingType, String buSelected) throws DatabaseException, Authentication.Failed, ExecutionException {
        switch (mappingType) {
            case ZERO_COLOR:
                return null;
            default:
                return new MappingService(supplyDemandHierarchy, mappingType, buSelected);
        }
    }

    public static MappingService getMappingServiceForEntity(Entity entity, Admin admin, String buSelected) throws ExecutionException, DatabaseException, Authentication.Failed {
        return new MappingService(new SupplyDemandHierarchy("entity", String.valueOf(entity.getEntityId()), "global", ""), CreativeConstants.MappingType.MANUAL, buSelected);
    }
}
