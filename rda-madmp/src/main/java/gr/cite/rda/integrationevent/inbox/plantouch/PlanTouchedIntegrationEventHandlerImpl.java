package gr.cite.rda.integrationevent.inbox.plantouch;

import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorProperties;
import gr.cite.rda.audit.AuditableAction;
import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.common.enums.AccessType;
import gr.cite.rda.common.enums.Status;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DmpRootElasticEntity;
import gr.cite.rda.elastic.data.DmpUserElasticEntity;
import gr.cite.rda.integrationevent.inbox.EventProcessingStatus;
import gr.cite.rda.integrationevent.inbox.InboxPrincipal;
import gr.cite.rda.integrationevent.inbox.IntegrationEventProperties;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.logging.LoggerService;
import org.opencdmp.commonmodels.enums.PlanAccessType;
import org.opencdmp.commonmodels.enums.PlanStatus;
import org.opencdmp.commonmodels.models.FileEnvelopeModel;
import org.opencdmp.commonmodels.models.PlanUserModel;
import org.opencdmp.commonmodels.models.plan.PlanModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlanTouchedIntegrationEventHandlerImpl implements PlanTouchedIntegrationEventHandler {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(PlanTouchedIntegrationEventHandlerImpl.class));

    private final JsonHandlingService jsonHandlingService;
    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final ClaimExtractorProperties claimExtractorProperties;
    private final AuditService auditService;
    private final ElasticService elasticService;
    private final ConventionService conventionService;

    public PlanTouchedIntegrationEventHandlerImpl(
            JsonHandlingService jsonHandlingService,
            CurrentPrincipalResolver currentPrincipalResolver, ClaimExtractorProperties claimExtractorProperties, AuditService auditService, ElasticService elasticService, ConventionService conventionService) {
        this.jsonHandlingService = jsonHandlingService;
	    this.currentPrincipalResolver = currentPrincipalResolver;
	    this.claimExtractorProperties = claimExtractorProperties;
	    this.auditService = auditService;
        this.elasticService = elasticService;
        this.conventionService = conventionService;
    }

    @Override
    public EventProcessingStatus handle(IntegrationEventProperties properties, String message) {
        PlanTouchedIntegrationEvent event = this.jsonHandlingService.fromJsonSafe(PlanTouchedIntegrationEvent.class, message);
        if (event == null)
            return EventProcessingStatus.Error;

        logger.debug("Handling {}", PlanTouchedIntegrationEvent.class.getSimpleName());

        EventProcessingStatus status = EventProcessingStatus.Success;
        try {

            currentPrincipalResolver.push(InboxPrincipal.build(properties, claimExtractorProperties));

            if (event.getPlans() != null) {
                for (PlanTouchedIntegrationEvent.PlanTouched planEvent : event.getPlans()) {

                    PlanModel plan = planEvent.getPlan();
                    if (plan == null) {
                        status = EventProcessingStatus.Error;
                        break;
                    }
                    if(plan.getRdaJsonFile() == null) {
                        logger.error("Problem getting rda json file for Plan Id:" + plan.getId() + " Skipping:");
                        status = EventProcessingStatus.Error;
                        break;
                    }

                    FileEnvelopeModel fileEnvelopeModel = plan.getRdaJsonFile();

                    byte[] fileBytes = fileEnvelopeModel.getFile();
                    String jsonContent = new String(fileBytes, StandardCharsets.UTF_8);

                    if(jsonContent.isEmpty()){
                        logger.error("Problem reading rda jsonContent. Skipping:");
                        status = EventProcessingStatus.Error;
                        break;
                    }
                    DmpRootElasticEntity dmp = this.jsonHandlingService.fromJsonSafe(DmpRootElasticEntity.class, jsonContent);

                    if(dmp == null){
                        logger.error("Problem reading rda jsonContent. Skipping:");
                        status = EventProcessingStatus.Error;
                        break;
                    }
                    if (plan.getId() != null) dmp.setId(plan.getId().toString());

                    if (plan.getAccessType() != null) {
                        if (plan.getAccessType().equals(PlanAccessType.Public)) dmp.setAccessType(AccessType.Public);
                        else dmp.setAccessType(AccessType.Restricted);
                    }

                    if (plan.getStatus() != null && plan.getStatus().getInternalStatus() != null && plan.getStatus().getInternalStatus().equals(PlanStatus.Finalized)) dmp.setStatus(Status.Finalized);
                    else dmp.setStatus(Status.NotFinalized);


                    if (!this.conventionService.isListNullOrEmpty(plan.getUsers())) {
                        List<DmpUserElasticEntity> dmpUsers = new ArrayList<>();
                        for (PlanUserModel planUser: plan.getUsers()) {
                            DmpUserElasticEntity dmpUser = new DmpUserElasticEntity();
                            dmpUser.setUserId(planUser.getUser().getId());
                            dmpUser.setRole(planUser.getRole());

                            dmpUsers.add(dmpUser);
                        }
                        dmp.setUsers(dmpUsers);
                    }

                    dmp.setTenantId(planEvent.getTenantId());

                    this.elasticService.persistRda(dmp, dmp.getDmp().getDmpId().getIdentifier(),true, null, null);
                }
            }

            auditService.track(AuditableAction.Dmp_Persist, Map.ofEntries(
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
