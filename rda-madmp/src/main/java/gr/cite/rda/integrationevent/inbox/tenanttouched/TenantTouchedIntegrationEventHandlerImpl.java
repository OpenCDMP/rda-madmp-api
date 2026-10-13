package gr.cite.rda.integrationevent.inbox.tenanttouched;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorProperties;
import gr.cite.rda.audit.AuditableAction;
import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.integrationevent.inbox.EventProcessingStatus;
import gr.cite.rda.integrationevent.inbox.InboxPrincipal;
import gr.cite.rda.integrationevent.inbox.IntegrationEventProperties;
import gr.cite.rda.model.persist.TenantTouchedIntegrationEventPersist;
import gr.cite.rda.service.tenant.TenantService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.validation.ValidatorFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TenantTouchedIntegrationEventHandlerImpl implements TenantTouchedIntegrationEventHandler {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantTouchedIntegrationEventHandlerImpl.class));


    private final JsonHandlingService jsonHandlingService;
    private final ValidatorFactory validatorFactory;
    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final ClaimExtractorProperties claimExtractorProperties;
    private final TenantService tenantService;
    private final AuditService auditService;
    private final TenantEntityManager tenantEntityManager;

    public TenantTouchedIntegrationEventHandlerImpl(JsonHandlingService jsonHandlingService, ValidatorFactory validatorFactory, CurrentPrincipalResolver currentPrincipalResolver, ClaimExtractorProperties claimExtractorProperties, TenantService tenantService, AuditService auditService, TenantEntityManager tenantEntityManager) {
        this.jsonHandlingService = jsonHandlingService;
        this.validatorFactory = validatorFactory;
        this.currentPrincipalResolver = currentPrincipalResolver;
        this.claimExtractorProperties = claimExtractorProperties;
        this.tenantService = tenantService;
        this.auditService = auditService;
        this.tenantEntityManager = tenantEntityManager;
    }

    @Override
    public EventProcessingStatus handle(IntegrationEventProperties properties, String message) {
        TenantTouchedIntegrationEvent event = this.jsonHandlingService.fromJsonSafe(TenantTouchedIntegrationEvent.class, message);
        if (event == null)
            return EventProcessingStatus.Error;

        TenantTouchedIntegrationEventPersist model = new TenantTouchedIntegrationEventPersist();
        model.setId(event.getId());
        model.setCode(event.getCode());
        this.validatorFactory.validator(TenantTouchedIntegrationEventPersist.TenantTouchedIntegrationEventPersistValidator.class).validateForce(model);

        EventProcessingStatus status = EventProcessingStatus.Success;
        tenantEntityManager.disableTenantFilters();
        try {
            currentPrincipalResolver.push(InboxPrincipal.build(properties, claimExtractorProperties));

            tenantService.persist(model, null);

            auditService.track(AuditableAction.Tenant_Persist, Map.ofEntries(
                    new AbstractMap.SimpleEntry<String, Object>("model", model)
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
