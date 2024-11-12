package net.media.autotemplate.bean;

import com.google.gson.annotations.SerializedName;
import net.media.autotemplate.dal.db.DbConstants;

import java.util.Optional;

public class Entity {
    @SerializedName("id")
    private final long entityId;
    @SerializedName("adtag")
    private final String adtagId;
    @SerializedName("domain")
    private final String domain;
    @SerializedName("size")
    private String size;
    private final String partnerId;
    private final String customerId;
    private AdtagInfo adtagInfo;

    public Entity(long entityId, String adtagId, String domain, String size, String customerId, String partnerId) {
        this.entityId = entityId;
        this.adtagId = adtagId;
        this.domain = domain;
        this.partnerId = partnerId;
        this.customerId = customerId;
        this.size = Optional.ofNullable(size).orElse(DbConstants.DEFAULT_TEMPLATE_SIZE);
    }

    public Entity(Long entity_id, String adtagId, String domain, String size) {
        this(Long.valueOf(entity_id), adtagId, domain, size, null, null);
    }

    public Entity(String adtagId, String domain, String size) {
        this(-1, adtagId, domain, size, null, null);
    }

    public Entity(long entityId, String adtagId, String domain) {
        this(entityId, adtagId, domain, null, null, null);
    }

    public Entity(String adtagId, String domain) {
        this(-1, adtagId, domain);
    }

    public Entity setAdtagInfo(AdtagInfo adtagInfo) {
        this.adtagInfo = adtagInfo;
        return this;
    }

    public AdtagInfo getAdtagInfo() {
        return adtagInfo;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getEntityId() {
        return entityId;
    }

    public long getAdtagIdAsLong() {
        return Long.parseLong(adtagId);
    }

    public String getAdtagId() {
        return adtagId;
    }

    public String getDomain() {
        return domain;
    }

    public String getSize() {
        return size;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getCustomerId() {
        return customerId;
    }
}
