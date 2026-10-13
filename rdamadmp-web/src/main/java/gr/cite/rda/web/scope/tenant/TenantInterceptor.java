package gr.cite.rda.web.scope.tenant;


import gr.cite.rda.authorization.ClaimNames;
import gr.cite.rda.authorization.Permission;
import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.common.scope.tenant.TenantScope;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.data.TenantUserEntity;
import gr.cite.rda.data.UserEntity;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.query.utils.BuildSubQueryInput;
import gr.cite.rda.query.utils.QueryUtilsService;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.logging.LoggerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.management.InvalidApplicationException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class TenantInterceptor implements WebRequestInterceptor {

	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantInterceptor.class));
	private final TenantScope tenantScope;
	private final UserScope userScope;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	private final ClaimExtractor claimExtractor;
	private final ApplicationContext applicationContext;
	private final TenantScopeProperties tenantScopeProperties;
	private final UserAllowedTenantCacheService userAllowedTenantCacheService;
	private final ErrorThesaurusProperties errors;
	private final QueryUtilsService queryUtilsService;
	public final TenantEntityManager tenantEntityManager;
	@PersistenceContext
	public EntityManager entityManager;

	@Autowired
	public TenantInterceptor(
			TenantScope tenantScope,
			UserScope userScope,
			CurrentPrincipalResolver currentPrincipalResolver,
			ClaimExtractor claimExtractor,
			ApplicationContext applicationContext,
			TenantScopeProperties tenantScopeProperties,
			UserAllowedTenantCacheService userAllowedTenantCacheService,
			ErrorThesaurusProperties errors, QueryUtilsService queryUtilsService, TenantEntityManager tenantEntityManager) {
		this.tenantScope = tenantScope;
		this.userScope = userScope;
		this.currentPrincipalResolver = currentPrincipalResolver;
		this.claimExtractor = claimExtractor;
		this.applicationContext = applicationContext;
		this.tenantScopeProperties = tenantScopeProperties;
		this.userAllowedTenantCacheService = userAllowedTenantCacheService;
		this.errors = errors;
		this.queryUtilsService = queryUtilsService;
		this.tenantEntityManager = tenantEntityManager;
	}

	@Override
	public void preHandle(@NotNull WebRequest request) throws InvalidApplicationException, InterruptedException {
		if (!this.currentPrincipalResolver.currentPrincipal().isAuthenticated()) return;
		if (!this.tenantScope.isMultitenant()) return;

		boolean isAllowedNoTenant = this.applicationContext.getBean(AuthorizationService.class).authorize(Permission.AllowNoTenant);
		if (tenantScope.isSet() && this.entityManager != null) {
			List<String> currentPrincipalTenantCodes = this.claimExtractor.asStrings(this.currentPrincipalResolver.currentPrincipal(), ClaimNames.TenantCodesClaimName);
			if ((currentPrincipalTenantCodes == null || !currentPrincipalTenantCodes.contains(tenantScope.getTenantCode())) && !isAllowedNoTenant) {
				logger.warn("tenant not allowed {}", this.tenantScope.getTenant());
				throw new MyForbiddenException(this.errors.getTenantNotAllowed().getCode(), this.errors.getTenantNotAllowed().getMessage());
			}

			boolean isUserAllowedTenant = false;
			if (this.tenantScope.supportExpansionTenant() && this.tenantScope.isDefaultTenant()) {
				isUserAllowedTenant = true;
			} else {
				UserAllowedTenantCacheService.UserAllowedTenantCacheValue cacheValue = this.userAllowedTenantCacheService.lookup(this.userAllowedTenantCacheService.buildKey(this.userScope.getUserId(), this.tenantScope.getTenant()));
				if (cacheValue != null) {
					isUserAllowedTenant = cacheValue.isAllowed();
				} else {
					isUserAllowedTenant = this.isUserAllowedTenant();
					this.userAllowedTenantCacheService.put(new UserAllowedTenantCacheService.UserAllowedTenantCacheValue(this.userScope.getUserId(), this.tenantScope.getTenant(), isUserAllowedTenant));
				}
			}

			if (isUserAllowedTenant) {
				this.tenantEntityManager.reloadTenantFilters();
			} else {
				if (isAllowedNoTenant || this.isWhiteListedEndpoint(request)) {
					tenantScope.setTenant(null, null);
				} else {
					logger.warn("tenant not allowed {}", this.tenantScope.getTenant());
					throw new MyForbiddenException(this.errors.getTenantNotAllowed().getCode(), this.errors.getTenantNotAllowed().getMessage());
				}
			}
		} else {
			if (!isAllowedNoTenant) {
				if (!this.isWhiteListedEndpoint(request)) {
					logger.warn("tenant scope not provided");
					throw new MyForbiddenException(this.errors.getMissingTenant().getCode(), this.errors.getMissingTenant().getMessage());
				}
			}
		}
	}

	private boolean isWhiteListedEndpoint(WebRequest request) {
		String servletPath = ((ServletWebRequest) request).getRequest().getServletPath();
		if (this.tenantScopeProperties.getWhiteListedEndpoints() != null) {
			for (String whiteListedEndpoint : this.tenantScopeProperties.getWhiteListedEndpoints()) {
				if (servletPath.toLowerCase(Locale.ROOT).startsWith(whiteListedEndpoint.toLowerCase(Locale.ROOT))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isUserAllowedTenant() throws InvalidApplicationException, InterruptedException {
		if (userScope.isSet()) {
			CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
			CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
			Root<UserEntity> root = query.from(UserEntity.class);
			query.where(criteriaBuilder.and(
					criteriaBuilder.equal(root.get(UserEntity._isActive), IsActive.Active),
					criteriaBuilder.in(root.get(UserEntity._id)).value(queryUtilsService.buildSubQuery(new BuildSubQueryInput<>(new BuildSubQueryInput.Builder<>(TenantUserEntity.class, UUID.class)
									.query(query)
									.criteriaBuilder(criteriaBuilder)
									.keyPathFunc((subQueryRoot) -> subQueryRoot.get(TenantUserEntity._userId))
									.filterFunc((subQueryRoot, cb) ->
											{
												try {
													return cb.and(
															criteriaBuilder.equal(subQueryRoot.get(TenantUserEntity._tenantId), this.tenantScope.getTenant()),
															criteriaBuilder.equal(subQueryRoot.get(TenantUserEntity._userId), this.userScope.getUserId()),
															criteriaBuilder.equal(subQueryRoot.get(TenantUserEntity._isActive), IsActive.Active)
													);
												} catch (InvalidApplicationException e) {
													throw new RuntimeException(e);
												}
											}
									)
							))
					)
			));
			query.multiselect(root.get(UserEntity._id).alias(UserEntity._id));
			List<UserEntity> results = this.entityManager.createQuery(query).getResultList();
			return !results.isEmpty();
		}

		return false;
	}

	@Override
	public void postHandle(@NonNull WebRequest request, ModelMap model) {
		this.tenantScope.setTenant(null, null);
		this.tenantEntityManager.disableTenantFilters();
	}
	@Override
	public void afterCompletion(@NonNull WebRequest request, Exception ex) {
	}
}
