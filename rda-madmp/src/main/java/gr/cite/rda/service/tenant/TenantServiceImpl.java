package gr.cite.rda.service.tenant;

import tools.jackson.core.JacksonException;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.event.EventBroker;
import gr.cite.rda.event.TenantTouchedEvent;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.authorization.Permission;
import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.data.TenantEntity;
import gr.cite.rda.model.Tenant;
import gr.cite.rda.model.builder.TenantBuilder;
import gr.cite.rda.model.deleter.TenantDeleter;
import gr.cite.rda.model.persist.TenantTouchedIntegrationEventPersist;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
public class TenantServiceImpl implements TenantService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantServiceImpl.class));

    private final AuthorizationService authorizationService;

    private final DeleterFactory deleterFactory;

    private final ConventionService conventionService;

    private final TenantEntityManager entityManager;

    private final BuilderFactory builderFactory;
    private final EventBroker eventBroker;
    private final MessageSource messageSource;

    public TenantServiceImpl(AuthorizationService authorizationService,
                             DeleterFactory deleterFactory,
                             ConventionService conventionService,
                             TenantEntityManager entityManager,
                             BuilderFactory builderFactory, EventBroker eventBroker, MessageSource messageSource) {
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.conventionService = conventionService;
        this.entityManager = entityManager;
        this.builderFactory = builderFactory;
	    this.eventBroker = eventBroker;
	    this.messageSource = messageSource;
    }

    @Override
    public Tenant persist(TenantTouchedIntegrationEventPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, JacksonException {
        logger.debug(new MapLogEntry("persisting tenant").And("model", model).And("fields", fields));

        this.authorizationService.authorizeForce(Permission.EditTenant);

        Boolean isValid = this.conventionService.isValidGuid(model.getId());

        TenantEntity data;
        if (isValid) {
            data = this.entityManager.find(TenantEntity.class, model.getId());
            if (data == null) {
                data = new TenantEntity();
                data.setId(model.getId());
                data.setCode(model.getCode());
                data.setIsActive(IsActive.Active);
                data.setCreatedAt(Instant.now());
                data.setUpdatedAt(Instant.now());

                this.entityManager.persist(data);
            } else {
                data.setCode(model.getCode());
                data.setUpdatedAt(Instant.now());
                data.setIsActive(IsActive.Active);

                this.entityManager.merge(data);
            }
        } else {
            throw new MyApplicationException("Not valid tenant id");
        }

        this.entityManager.flush();

        this.eventBroker.emit(new TenantTouchedEvent(data.getId(), data.getCode()));
        return this.builderFactory.builder(TenantBuilder.class).authorize(EnumSet.of(AuthorizationFlags.None)).build(BaseFieldSet.build(fields, Tenant._id), data);
    }

    @Override
    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting Tenant: {}", id);
        
        TenantEntity data = this.entityManager.find(TenantEntity.class, id);
        if (data == null) throw new MyNotFoundException(this.messageSource.getMessage("General_ItemNotFound", new Object[]{id, Tenant.class.getSimpleName()}, LocaleContextHolder.getLocale()));

        this.authorizationService.authorizeForce(Permission.DeleteTenant);

        this.deleterFactory.deleter(TenantDeleter.class).deleteAndSaveByIds(List.of(id));

        this.eventBroker.emit(new TenantTouchedEvent(data.getId(), data.getCode()));
    }

}
