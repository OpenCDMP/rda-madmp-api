package gr.cite.rda.service.user;

import tools.jackson.core.JacksonException;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.authorization.Permission;
import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.common.scope.tenant.TenantScope;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.data.*;
import gr.cite.rda.event.EventBroker;
import gr.cite.rda.event.UserAddedToTenantEvent;
import gr.cite.rda.event.UserCredentialTouchedEvent;
import gr.cite.rda.event.UserTouchedEvent;
import gr.cite.rda.integrationevent.inbox.usertouched.UserTouchedIntegrationEvent;
import gr.cite.rda.model.Tenant;
import gr.cite.rda.model.User;
import gr.cite.rda.model.builder.UserBuilder;
import gr.cite.rda.model.deleter.TenantUserDeleter;
import gr.cite.rda.model.deleter.UserCredentialDeleter;
import gr.cite.rda.model.deleter.UserDeleter;
import gr.cite.rda.query.TenantQuery;
import gr.cite.rda.query.TenantUserQuery;
import gr.cite.rda.query.UserCredentialQuery;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.deleter.DeleterFactory;
import gr.cite.tools.data.query.QueryFactory;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(UserServiceImpl.class));

    private final AuthorizationService authorizationService;

    private final DeleterFactory deleterFactory;

    private final ConventionService conventionService;

    private final TenantEntityManager entityManager;

    private final BuilderFactory builderFactory;

    private final QueryFactory queryFactory;
    private final TenantScope tenantScope;

    private final MessageSource messageSource;
    private final EventBroker eventBroker;


    private final JsonHandlingService jsonHandlingService;

    public UserServiceImpl(AuthorizationService authorizationService,
                           DeleterFactory deleterFactory,
                           ConventionService conventionService,
                           TenantEntityManager entityManager,
                           BuilderFactory builderFactory, QueryFactory queryFactory, TenantScope tenantScope, MessageSource messageSource, EventBroker eventBroker,
                           JsonHandlingService jsonHandlingService) {
        this.authorizationService = authorizationService;
        this.deleterFactory = deleterFactory;
        this.conventionService = conventionService;
        this.entityManager = entityManager;
        this.builderFactory = builderFactory;
	    this.queryFactory = queryFactory;
	    this.tenantScope = tenantScope;
	    this.messageSource = messageSource;
	    this.eventBroker = eventBroker;
	    this.jsonHandlingService = jsonHandlingService;
    }

    @Override
    public User persist(UserTouchedIntegrationEvent model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, JacksonException {
        logger.debug(new MapLogEntry("persisting user").And("model", model).And("fields", fields));

//        this.authorizationService.authorizeAtLeastOneForce(model.getId() != null ? List.of(new OwnedResource(model.getId())) : null, Permission.EditUser);

        Boolean isValid = this.conventionService.isValidGuid(model.getId());

        UserEntity data;
        if (isValid) {
            data = this.entityManager.find(UserEntity.class, model.getId());
            if (data == null) {
                data = new UserEntity();
                data.setId(model.getId());
                data.setName(model.getName());
                data.setAdditionalInfo(this.jsonHandlingService.toJson(model.getProfile()));
                data.setCreatedAt(Instant.now());
                data.setUpdatedAt(Instant.now());
                data.setIsActive(IsActive.Active);

                this.entityManager.persist(data);

            } else {
                data.setName(model.getName());
                data.setAdditionalInfo(this.jsonHandlingService.toJson(model.getProfile()));
                data.setUpdatedAt(Instant.now());
                data.setIsActive(IsActive.Active);

                this.entityManager.merge(data);

            }
        } else {
            throw new MyApplicationException("Not valid user id");
        }

        this.entityManager.flush();

        this.persistUserCredential(model.getCredentials(), data.getId());
        this.persistTenantUser(model.getTenantUsers(), data.getId());

        this.entityManager.flush();
        this.eventBroker.emit(new UserTouchedEvent(data.getId()));

        return this.builderFactory.builder(UserBuilder.class).authorize(EnumSet.of(AuthorizationFlags.None)).build(BaseFieldSet.build(fields, User._id), data);
    }

    @Override
    public void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException {
        logger.debug("deleting User: {}", id);

        this.authorizationService.authorizeForce(Permission.DeleteUser);

        this.deleterFactory.deleter(UserDeleter.class).deleteAndSaveByIds(List.of(id));
        this.eventBroker.emit(new UserTouchedEvent(id));
    }

    private void persistUserCredential(List<UserTouchedIntegrationEvent.UserCredential> models, UUID userId) throws InvalidApplicationException {
        List<UserCredentialEntity> items = this.queryFactory.query(UserCredentialQuery.class)
                .userIds(userId)
                .isActive(IsActive.Active)
                .collect();
        List<UUID> updatedCreatedIds = new ArrayList<>();
        if (models != null) {
            for (UserTouchedIntegrationEvent.UserCredential model : models) {
                UserCredentialEntity data = items.stream().filter(x -> x.getExternalId().equals(model.getSubjectId())).findFirst().orElse(null);
                if (data == null) {
                    data = new UserCredentialEntity();
                    data.setId(UUID.randomUUID());
                    data.setUserId(userId);
                    data.setExternalId(model.getSubjectId());
                    data.setCreatedAt(Instant.now());
                    data.setUpdatedAt(Instant.now());
                    entityManager.persist(data);
                }
                this.eventBroker.emit(new UserCredentialTouchedEvent(data.getId(), data.getExternalId()));
                updatedCreatedIds.add(data.getId());
            }
        }
        List<UserCredentialEntity> toDelete = items.stream().filter(x -> updatedCreatedIds.stream().noneMatch(y -> y.equals(x.getId()))).collect(Collectors.toList());
        deleterFactory.deleter(UserCredentialDeleter.class).delete(toDelete);

        entityManager.flush();
    }

    private void persistTenantUser(List<UserTouchedIntegrationEvent.TenantUser> models, UUID userId) throws InvalidApplicationException {
        List<TenantUserEntity> items = this.queryFactory.query(TenantUserQuery.class)
                .userIds(userId)
                .isActive(IsActive.Active)
                .collect();
        
        List<UUID> updatedCreatedIds = new ArrayList<>();
        if (models != null) {
            List<TenantEntity> tenantEntities = this.queryFactory.query(TenantQuery.class)
                    .ids(models.stream().map(UserTouchedIntegrationEvent.TenantUser::getTenant).toList())
                    .isActive(IsActive.Active)
                    .collectAs(new BaseFieldSet().ensure(Tenant._id).ensure(Tenant._code));
            for (UserTouchedIntegrationEvent.TenantUser model : models) {
                TenantUserEntity data = items.stream().filter(x -> x.getTenantId().equals(model.getTenant())).findFirst().orElse(null);
                if (data == null) {
                    try {
                        TenantEntity tenant = tenantEntities.stream().filter(x -> x.getId().equals(model.getTenant())).findFirst().orElse(null);
                        if (tenant == null) throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{model.getTenant(), Tenant.class.getSimpleName()}, LocaleContextHolder.getLocale()));
                        this.tenantScope.setTempTenant(this.entityManager, tenant.getId(), tenant.getCode());
                        data = new TenantUserEntity();
                        data.setId(UUID.randomUUID());
                        data.setUserId(userId);
                        data.setTenantId(model.getTenant());
                        data.setCreatedAt(Instant.now());
                        data.setUpdatedAt(Instant.now());
                        data.setIsActive(IsActive.Active);
                        entityManager.persist(data);
                        this.eventBroker.emit(new UserAddedToTenantEvent(data.getUserId(), data.getTenantId()));
                    } finally {
                        this.tenantScope.removeTempTenant(this.entityManager);
                    }
                }
                updatedCreatedIds.add(data.getId());
            }
        }
        List<TenantUserEntity> toDelete = items.stream().filter(x -> updatedCreatedIds.stream().noneMatch(y -> y.equals(x.getId()))).collect(Collectors.toList());
        deleterFactory.deleter(TenantUserDeleter.class).delete(toDelete);

        entityManager.flush();
    }

}
