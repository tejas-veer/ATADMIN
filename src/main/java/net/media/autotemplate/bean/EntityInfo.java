package net.media.autotemplate.bean;


public class EntityInfo {
    private final String domain;
    private final String adtagId;
    private final Long entityId;

    public EntityInfo(String domain, String adtagId, Long entityId) {
        this.domain = domain;
        this.adtagId = adtagId;
        this.entityId = entityId;
    }

    public String getDomain() {
        return domain;
    }

    public String getAdtagId() {
        return adtagId;
    }

    public Long getEntityId() {
        return entityId;
    }
}
