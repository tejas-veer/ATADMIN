package net.media.autotemplate.routes.util;

import com.google.gson.*;
import net.media.autotemplate.bean.InboundParams;
import net.media.autotemplate.bean.RequestGlobal;
import net.media.autotemplate.bean.SupplyDemandHierarchy;
import net.media.autotemplate.util.Util;

import java.lang.reflect.Type;

public abstract class AbstractATRoute extends AbstractRoute {

    private static final Gson SUPPLY_DEMAND_GSON = getCustomGson();

    private static Gson getCustomGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<SupplyDemandHierarchy> deserializer = new CustomDeserializer();
        gsonBuilder.registerTypeAdapter(SupplyDemandHierarchy.class, deserializer);
        return gsonBuilder.create();
    }

    public SupplyDemandHierarchy getSupplyDemandHierarchy() throws Exception {
        SupplyDemandHierarchy supplyDemandHierarchy = new SupplyDemandHierarchy(
                InboundParams.getInstance().getSupply(),
                InboundParams.getInstance().getSupplyId(),
                InboundParams.getInstance().getDemand(),
                InboundParams.getInstance().getDemandID()
        );

        if(!supplyDemandHierarchy.isSupplyValid() && !supplyDemandHierarchy.isDemandValid()) {
            String json = RequestGlobal.getRequest().body();
            if(Util.isStringSet(json)) {
                supplyDemandHierarchy = SUPPLY_DEMAND_GSON.fromJson(json, SupplyDemandHierarchy.class);
                if(!Util.isSet(supplyDemandHierarchy)) {
                    throw new Exception("hierarchyLevel-MUST_BE_PRESENT_IN_JSON_BODY");
                }
                if(!supplyDemandHierarchy.isSupplyValid() && !supplyDemandHierarchy.isDemandValid()) {
                    throw new Exception("SUPPLY_DEMAND_NOT_VALID_IN_POST_REQUEST");
                }
                return supplyDemandHierarchy;
            }
            else throw new Exception("JSON_NOT_PRESENT_IN_REQUEST");
        }
        return supplyDemandHierarchy;
    }

    private static final class CustomDeserializer implements JsonDeserializer<SupplyDemandHierarchy> {

        @Override
        public SupplyDemandHierarchy deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root = jsonElement.getAsJsonObject();
            if(root.has("hierarchyLevel")) {
                JsonObject hierarchyLevel = root.get("hierarchyLevel").getAsJsonObject();
                return new SupplyDemandHierarchy(
                        hierarchyLevel.get("supply").getAsString(),
                        hierarchyLevel.get("supplyId").getAsString(),
                        hierarchyLevel.get("demand").getAsString(),
                        hierarchyLevel.get("demandId").getAsString()
                );
            }
            return null;
        }
    }
}
