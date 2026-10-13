package gr.cite.rda.integrationevent.outbox.dmptouch;

import gr.cite.rda.integrationevent.outbox.OutboxIntegrationEvent;
import gr.cite.rda.integrationevent.outbox.OutboxService;
import gr.cite.tools.logging.LoggerService;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DmpTouchedIntegrationEventHandlerImpl implements DmpTouchedIntegrationEventHandler {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(DmpTouchedIntegrationEventHandlerImpl.class));

    private final OutboxService outboxService;

    public DmpTouchedIntegrationEventHandlerImpl(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    public void handle(DmpTouchedIntegrationEvent event, UUID tenantId) {
        OutboxIntegrationEvent message = new OutboxIntegrationEvent();
        message.setMessageId(UUID.randomUUID());
        message.setType(OutboxIntegrationEvent.DMP_TOUCH);
        message.setEvent(event);
        message.setTenantId(tenantId);
        this.outboxService.publish(message);
    }
}
