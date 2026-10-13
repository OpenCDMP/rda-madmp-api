package gr.cite.rda.integrationevent.inbox.usertouched;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorProperties;
import gr.cite.rda.audit.AuditableAction;
import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.integrationevent.inbox.EventProcessingStatus;
import gr.cite.rda.integrationevent.inbox.InboxPrincipal;
import gr.cite.rda.integrationevent.inbox.IntegrationEventProperties;
import gr.cite.rda.service.user.UserService;
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
public class UserTouchedIntegrationEventHandlerImpl implements UserTouchedIntegrationEventHandler {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserTouchedIntegrationEventHandlerImpl.class));


    private final JsonHandlingService jsonHandlingService;

    private final ValidatorFactory validatorFactory;
    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final ClaimExtractorProperties claimExtractorProperties;
    private final UserService userService;
    private final AuditService auditService;
    private final TenantEntityManager tenantEntityManager;

    public UserTouchedIntegrationEventHandlerImpl(
		    JsonHandlingService jsonHandlingService,
		    ValidatorFactory validatorFactory, CurrentPrincipalResolver currentPrincipalResolver, ClaimExtractorProperties claimExtractorProperties, UserService userService, AuditService auditService, TenantEntityManager tenantEntityManager) {
        this.jsonHandlingService = jsonHandlingService;
        this.validatorFactory = validatorFactory;
	    this.currentPrincipalResolver = currentPrincipalResolver;
	    this.claimExtractorProperties = claimExtractorProperties;
	    this.userService = userService;
	    this.auditService = auditService;
	    this.tenantEntityManager = tenantEntityManager;
    }

    @Override
    public EventProcessingStatus handle(IntegrationEventProperties properties, String message) {
        UserTouchedIntegrationEvent event = this.jsonHandlingService.fromJsonSafe(UserTouchedIntegrationEvent.class, message);
        if (event == null)
            return EventProcessingStatus.Error;

        logger.debug("Handling {}", UserTouchedIntegrationEvent.class.getSimpleName());

        this.validatorFactory.validator(UserTouchedIntegrationEvent.UserTouchedIntegrationEventValidator.class).validateForce(event);

        EventProcessingStatus status = EventProcessingStatus.Success;
        try {

            currentPrincipalResolver.push(InboxPrincipal.build(properties, claimExtractorProperties));
			try {
				this.tenantEntityManager.disableTenantFilters();
				userService.persist(event, null);
			} finally {
				this.tenantEntityManager.reloadTenantFilters();
			}
            auditService.track(AuditableAction.User_Persist, Map.ofEntries(
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
