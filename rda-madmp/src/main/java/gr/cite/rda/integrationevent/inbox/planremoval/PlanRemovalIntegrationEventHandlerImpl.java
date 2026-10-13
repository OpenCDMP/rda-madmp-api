package gr.cite.rda.integrationevent.inbox.planremoval;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorProperties;
import gr.cite.rda.audit.AuditableAction;
import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.integrationevent.inbox.EventProcessingStatus;
import gr.cite.rda.integrationevent.inbox.InboxPrincipal;
import gr.cite.rda.integrationevent.inbox.IntegrationEventProperties;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlanRemovalIntegrationEventHandlerImpl implements PlanRemovalIntegrationEventHandler {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(PlanRemovalIntegrationEventHandlerImpl.class));

    private final JsonHandlingService jsonHandlingService;
    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final ClaimExtractorProperties claimExtractorProperties;
    private final AuditService auditService;
    private final ElasticService elasticService;

    public PlanRemovalIntegrationEventHandlerImpl(
            JsonHandlingService jsonHandlingService,
            CurrentPrincipalResolver currentPrincipalResolver, ClaimExtractorProperties claimExtractorProperties, AuditService auditService, ElasticService elasticService) {
        this.jsonHandlingService = jsonHandlingService;
	    this.currentPrincipalResolver = currentPrincipalResolver;
	    this.claimExtractorProperties = claimExtractorProperties;
	    this.auditService = auditService;
        this.elasticService = elasticService;
    }

    @Override
    public EventProcessingStatus handle(IntegrationEventProperties properties, String message) {
        PlanRemovalIntegrationEvent event = this.jsonHandlingService.fromJsonSafe(PlanRemovalIntegrationEvent.class, message);
        if (event == null)
            return EventProcessingStatus.Error;

        logger.debug("Handling {}", PlanRemovalIntegrationEvent.class.getSimpleName());

        EventProcessingStatus status = EventProcessingStatus.Success;
        try {

            currentPrincipalResolver.push(InboxPrincipal.build(properties, claimExtractorProperties));

            if (event.getPlanIds() != null) {
                for (UUID id: event.getPlanIds()) {
                    this.elasticService.deleteRda(id.toString(), true);
                }
            }

            auditService.track(AuditableAction.Dmp_Delete, Map.ofEntries(
                    new AbstractMap.SimpleEntry<String, Object>("model", event)
            ));

        } catch (Exception ex) {
            status = EventProcessingStatus.Error;
            logger.error("Problem getting list of queue outbox. Skipping: {}", ex.getMessage(), ex);
        } finally {
            currentPrincipalResolver.pop();
        }

        return status;
    }
}
