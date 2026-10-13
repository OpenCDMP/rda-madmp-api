package gr.cite.rda.integrationevent.inbox.tenantremoval;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorProperties;
import gr.cite.rda.audit.AuditableAction;
import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.integrationevent.inbox.EventProcessingStatus;
import gr.cite.rda.integrationevent.inbox.InboxPrincipal;
import gr.cite.rda.integrationevent.inbox.IntegrationEventProperties;
import gr.cite.rda.service.tenant.TenantService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TenantRemovalIntegrationEventHandlerImpl implements TenantRemovalIntegrationEventHandler {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantRemovalIntegrationEventHandlerImpl.class));

    private final JsonHandlingService jsonHandlingService;
    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final ClaimExtractorProperties claimExtractorProperties;
    private final TenantService tenantService;
    private final AuditService auditService;
    private final TenantEntityManager tenantEntityManager;
    private final TenantRemovalConsistencyHandler tenantRemovalConsistencyHandler;
    public TenantRemovalIntegrationEventHandlerImpl(JsonHandlingService jsonHandlingService,  CurrentPrincipalResolver currentPrincipalResolver, ClaimExtractorProperties claimExtractorProperties, TenantService tenantService, AuditService auditService, TenantEntityManager tenantEntityManager, TenantRemovalConsistencyHandler tenantRemovalConsistencyHandler) {
        this.jsonHandlingService = jsonHandlingService;
	    this.currentPrincipalResolver = currentPrincipalResolver;
	    this.claimExtractorProperties = claimExtractorProperties;
	    this.tenantService = tenantService;
	    this.auditService = auditService;
	    this.tenantEntityManager = tenantEntityManager;
	    this.tenantRemovalConsistencyHandler = tenantRemovalConsistencyHandler;
    }

    @Override
    public EventProcessingStatus handle(IntegrationEventProperties properties, String message) {
        TenantRemovalIntegrationEvent event = this.jsonHandlingService.fromJsonSafe(TenantRemovalIntegrationEvent.class, message);
        if (event == null)
            return EventProcessingStatus.Error;

        EventProcessingStatus status = EventProcessingStatus.Success;
        try {

            currentPrincipalResolver.push(InboxPrincipal.build(properties, claimExtractorProperties));

            if (!(tenantRemovalConsistencyHandler.isConsistent(new TenantRemovalConsistencyPredicates(event.getId())))) {
                status = EventProcessingStatus.Postponed;
                currentPrincipalResolver.pop();
                return status;
            }

            tenantEntityManager.disableTenantFilters();
            tenantService.deleteAndSave(event.getId());


            auditService.track(AuditableAction.Tenant_Delete, Map.ofEntries(
                    new AbstractMap.SimpleEntry<String, Object>("id", event.getId())
            ));
            //auditService.trackIdentity(AuditableAction.IdentityTracking_Action);


        } catch (Exception ex) {
            status = EventProcessingStatus.Error;
            logger.error("Problem getting list of queue outbox. Skipping: {}", ex.getMessage(), ex);
        } finally {
            currentPrincipalResolver.pop();
        }
        return status;
    }

}
