package gr.cite.rda.integrationevent.outbox.dmptouch;


import java.util.UUID;

public interface DmpTouchedIntegrationEventHandler {

    void handle(DmpTouchedIntegrationEvent event, UUID tenantId);
}
