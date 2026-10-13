package gr.cite.rda.web.scope.user;

import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.common.lock.LockByKeyManager;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.rda.common.types.user.AdditionalInfoEntity;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.data.UserCredentialEntity;
import gr.cite.rda.data.UserEntity;
import gr.cite.rda.locale.LocaleProperties;
import gr.cite.rda.model.UserCredential;
import gr.cite.rda.query.UserCredentialQuery;
import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.fieldset.BaseFieldSet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class UserInterceptor implements WebRequestInterceptor {
    private final UserScope userScope;
    private final ClaimExtractor claimExtractor;
    private final CurrentPrincipalResolver currentPrincipalResolver;
    private final UserInterceptorCacheService userInterceptorCacheService;
    private final QueryFactory queryFactory;
    private final LockByKeyManager lockByKeyManager;
    private final LocaleProperties localeProperties;
    private final JsonHandlingService jsonHandlingService;
    private final PlatformTransactionManager transactionManager;
    @PersistenceContext
    public EntityManager entityManager;
    public final TenantEntityManager tenantEntityManager;

    @Autowired
    public UserInterceptor(
            UserScope userScope,
            ClaimExtractor claimExtractor,
            CurrentPrincipalResolver currentPrincipalResolver,
            UserInterceptorCacheService userInterceptorCacheService,
            QueryFactory queryFactory, LockByKeyManager lockByKeyManager,
            LocaleProperties localeProperties,
            JsonHandlingService jsonHandlingService, PlatformTransactionManager transactionManager, TenantEntityManager tenantEntityManager) {
        this.userScope = userScope;
        this.currentPrincipalResolver = currentPrincipalResolver;
        this.claimExtractor = claimExtractor;
        this.userInterceptorCacheService = userInterceptorCacheService;
        this.queryFactory = queryFactory;
        this.lockByKeyManager = lockByKeyManager;
        this.localeProperties = localeProperties;
        this.jsonHandlingService = jsonHandlingService;
        this.transactionManager = transactionManager;
        this.tenantEntityManager = tenantEntityManager;
    }

    @Override
    public void preHandle(@NotNull WebRequest request) throws InterruptedException, InvalidApplicationException {
        UUID userId = null;
        if (this.currentPrincipalResolver.currentPrincipal().isAuthenticated()) {
            String subjectId = this.claimExtractor.subjectString(this.currentPrincipalResolver.currentPrincipal());
            if (subjectId == null || subjectId.isBlank()) throw new MyForbiddenException("Empty subjects not allowed");
            
            UserInterceptorCacheService.UserInterceptorCacheValue cacheValue = this.userInterceptorCacheService.lookup(this.userInterceptorCacheService.buildKey(subjectId));
            if (cacheValue != null) {
                userId = cacheValue.getUserId();
            } else {
                boolean usedResource = false;
                try {
                    this.tenantEntityManager.disableTenantFilters();

                    usedResource = this.lockByKeyManager.tryLock(subjectId, 5000, TimeUnit.MILLISECONDS);

                    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
                    definition.setName(UUID.randomUUID().toString());
                    definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
                    definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                    TransactionStatus status = null;

                    try {
                        status = this.transactionManager.getTransaction(definition);

                        userId = this.findExistingUserFromDb(subjectId);
                        boolean isNewUser = userId == null;
                        if (isNewUser) {
                            UserEntity user = this.addNewUser(subjectId);
                            userId = user.getId();
                        }
                        this.entityManager.flush();

                        this.transactionManager.commit(status);
                    } catch (Exception ex) {
                        if (status != null) this.transactionManager.rollback(status);
                        throw ex;
                    }

                    if (userId != null) {
                        cacheValue = new UserInterceptorCacheService.UserInterceptorCacheValue(subjectId, userId);
                        this.userInterceptorCacheService.put(cacheValue);
                    }

                } finally {
                    if (usedResource) this.lockByKeyManager.unlock(subjectId);
                    this.tenantEntityManager.reloadTenantFilters();
                }
            }
        }
        this.userScope.setUserId(userId);
    }
    private UUID findExistingUserFromDb(String subjectId) {
        UserCredentialEntity userCredential = this.queryFactory.query(UserCredentialQuery.class).disableTracking().externalIds(subjectId).firstAs(new BaseFieldSet().ensure(UserCredential._user));
        if (userCredential != null) {
            return userCredential.getUserId();
        }
        return null;
    }

    private UserEntity addNewUser(String subjectId) {
        String name = this.claimExtractor.name(this.currentPrincipalResolver.currentPrincipal());

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setName(name);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setIsActive(IsActive.Active);
        AdditionalInfoEntity additionalInfoEntity = new AdditionalInfoEntity();
        additionalInfoEntity.setCulture(this.localeProperties.getCulture());
        additionalInfoEntity.setLanguage(this.localeProperties.getLanguage());
        additionalInfoEntity.setTimezone(this.localeProperties.getTimezone());
        user.setAdditionalInfo(this.jsonHandlingService.toJsonSafe(additionalInfoEntity));
        this.entityManager.persist(user);

        UserCredentialEntity credential = this.buildCredential(user.getId(), subjectId);
        this.entityManager.persist(credential);

        return user;
    }

    private UserCredentialEntity buildCredential(UUID userId, String subjectId) {
        UserCredentialEntity data = new UserCredentialEntity();

        data.setId(UUID.randomUUID());
        data.setUserId(userId);
        data.setCreatedAt(Instant.now());
        data.setUpdatedAt(Instant.now());
        data.setExternalId(subjectId);
        return data;
    }

    @Override
    public void postHandle(@NonNull WebRequest request, ModelMap model) {
        this.userScope.setUserId(null);
    }

    @Override
    public void afterCompletion(@NonNull WebRequest request, Exception ex) {
    }
}

