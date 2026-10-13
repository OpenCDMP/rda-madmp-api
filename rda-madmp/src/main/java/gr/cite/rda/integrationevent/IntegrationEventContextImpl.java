package gr.cite.rda.integrationevent;

import gr.cite.rabbitmq.IntegrationEventContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IntegrationEventContextImpl implements IntegrationEventContext {

    private UUID tenant;

    public IntegrationEventContextImpl() {
    }

    public UUID getTenant() {
        return tenant;
    }

    public void setTenant(UUID tenant) {
        this.tenant = tenant;
    }

}
