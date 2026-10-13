package gr.cite.rda.integrationevent.inbox.userremoval;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorProperties;
import gr.cite.rda.audit.AuditableAction;
import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.common.scope.tenant.TenantScope;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.integrationevent.inbox.EventProcessingStatus;
import gr.cite.rda.integrationevent.inbox.InboxPrincipal;
import gr.cite.rda.integrationevent.inbox.IntegrationEventProperties;
import gr.cite.rda.service.user.UserService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserRemovalIntegrationEventHandlerImpl implements UserRemovalIntegrationEventHandler {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserRemovalIntegrationEventHandlerImpl.class));

    private final JsonHandlingService jsonHandlingService;


    private final ErrorThesaurusProperties errors;

    private final MessageSource messageSource;
    private final TenantScope tenantScope;
    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final ClaimExtractorProperties claimExtractorProperties;
    private final UserRemovalConsistencyHandler userRemovalConsistencyHandler;
    private final UserService userService;
    private final AuditService auditService;
    private final TenantEntityManager tenantEntityManager;

    public UserRemovalIntegrationEventHandlerImpl(
		    JsonHandlingService jsonHandlingService,
		    ErrorThesaurusProperties errors,
		    MessageSource messageSource, TenantScope tenantScope, CurrentPrincipalResolver currentPrincipalResolver, ClaimExtractorProperties claimExtractorProperties, UserRemovalConsistencyHandler userRemovalConsistencyHandler, UserService userService, AuditService auditService, TenantEntityManager tenantEntityManager
    ) {
        this.jsonHandlingService = jsonHandlingService;
        this.errors = errors;
        this.messageSource = messageSource;
	    this.tenantScope = tenantScope;
	    this.currentPrincipalResolver = currentPrincipalResolver;
	    this.claimExtractorProperties = claimExtractorProperties;
	    this.userRemovalConsistencyHandler = userRemovalConsistencyHandler;
	    this.userService = userService;
	    this.auditService = auditService;
	    this.tenantEntityManager = tenantEntityManager;
    }

    @Override
    public EventProcessingStatus handle(IntegrationEventProperties properties, String message) {
        UserRemovalIntegrationEvent event = this.jsonHandlingService.fromJsonSafe(UserRemovalIntegrationEvent.class, message);
        if (event == null)
            return EventProcessingStatus.Error;
        if (event.getUserId() == null)
            throw new MyValidationException(this.errors.getModelValidation().getCode(), "userId", messageSource.getMessage("Validation_Required", new Object[]{"userId"}, LocaleContextHolder.getLocale()));

        logger.debug("Handling {}", UserRemovalIntegrationEvent.class.getSimpleName());

        EventProcessingStatus status = EventProcessingStatus.Success;
        try {
            currentPrincipalResolver.push(InboxPrincipal.build(properties, claimExtractorProperties));

			try{
				this.tenantEntityManager.disableTenantFilters();
	            if (!(userRemovalConsistencyHandler.isConsistent(new UserRemovalConsistencyPredicates(event.getUserId())))) {
	                status = EventProcessingStatus.Postponed;
	                currentPrincipalResolver.pop();
	                return status;
	            }
	
	            userService.deleteAndSave(event.getUserId());
			} finally {
				this.tenantEntityManager.reloadTenantFilters();
			}

            auditService.track(AuditableAction.User_Delete, Map.ofEntries(
                    new AbstractMap.SimpleEntry<String, Object>("id", event.getUserId())
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
