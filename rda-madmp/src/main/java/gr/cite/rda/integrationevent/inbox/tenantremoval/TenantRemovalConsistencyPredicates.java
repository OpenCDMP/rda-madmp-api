package gr.cite.rda.integrationevent.inbox.tenantremoval;

import gr.cite.rda.integrationevent.inbox.ConsistencyPredicates;

import java.util.UUID;

public class TenantRemovalConsistencyPredicates implements ConsistencyPredicates {

    private UUID tenantId;

    public TenantRemovalConsistencyPredicates(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

}
