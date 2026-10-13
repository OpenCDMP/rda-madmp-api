package gr.cite.rda.authorization.authorizationcontentresolver;

import gr.cite.rda.authorization.AffiliatedResource;
import gr.cite.rda.authorization.PermissionNameProvider;
import gr.cite.rda.common.scope.tenant.TenantScope;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.elastic.data.DmpRootElasticEntity;
import gr.cite.rda.elastic.data.DmpUserElasticEntity;
import gr.cite.rda.elastic.query.DmpRootElasticQuery;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import javax.management.InvalidApplicationException;
import java.util.*;


@Service
@RequestScope
public class AuthorizationContentResolverImpl implements AuthorizationContentResolver {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(AuthorizationContentResolverImpl.class));
	private static final Logger log = LoggerFactory.getLogger(AuthorizationContentResolverImpl.class);
	private final QueryFactory queryFactory;
	private final UserScope userScope;
	private final TenantScope tenantScope;
	private final AffiliationCacheService affiliationCacheService;
	private final PermissionNameProvider permissionNameProvider;
	private final TenantEntityManager tenantEntityManager;
	public AuthorizationContentResolverImpl(QueryFactory queryFactory, UserScope userScope, TenantScope tenantScope, AffiliationCacheService affiliationCacheService, PermissionNameProvider permissionNameProvider, TenantEntityManager tenantEntityManager) {
		this.queryFactory = queryFactory;
		this.userScope = userScope;
		this.tenantScope = tenantScope;
		this.affiliationCacheService = affiliationCacheService;
		this.permissionNameProvider = permissionNameProvider;
		this.tenantEntityManager = tenantEntityManager;
	}
	
	@Override
	public List<String> getPermissionNames() {
		return this.permissionNameProvider.getPermissions();
	}

	@Override
	public AffiliatedResource dmpAffiliation(String id) {
		return this.dmpsAffiliation(List.of(id)).getOrDefault(id, new AffiliatedResource());
	}
	@Override
	public Map<String, AffiliatedResource> dmpsAffiliation(List<String> ids){
		UUID userId = this.userScope.getUserIdSafe();
		Map<String, AffiliatedResource> affiliatedResources = new HashMap<>();
		for (String id : ids){
			affiliatedResources.put(id, new AffiliatedResource());
		}
		if (userId == null || !this.userScope.isSet()) return affiliatedResources;

		List<String> idsToResolve = this.getAffiliatedFromCache(ids, userId, affiliatedResources, DmpRootElasticEntity.class.getSimpleName());
		if (idsToResolve.isEmpty()) return affiliatedResources;
		List<DmpRootElasticEntity> dmps;
		try {
			this.tenantEntityManager.loadExplicitTenantFilters();
			dmps = this.queryFactory.query(DmpRootElasticQuery.class).ids(ids).collect();
		} catch (InvalidApplicationException e) {
			log.error(e.getMessage(), e);
			throw new MyApplicationException(e.getMessage());
		} finally {
			try {
				this.tenantEntityManager.reloadTenantFilters();
			} catch (InvalidApplicationException e) {
				log.error(e.getMessage(), e);
				throw new MyApplicationException(e.getMessage());
			}
		}

		for (DmpRootElasticEntity dmp: dmps) {
			for (DmpUserElasticEntity dmpUser : dmp.getUsers()){
				if (dmp.getUsers().stream().anyMatch(x -> x.getUserId() != null && x.getUserId().equals(userId))) affiliatedResources.get(dmp.getId()).getDmpUserRoles().add(dmpUser.getRole());
			}
		}


		this.ensureAffiliatedInCache(idsToResolve, userId, affiliatedResources, DmpRootElasticEntity.class.getSimpleName());
		return affiliatedResources;
	}


	private List<String> getAffiliatedFromCache(List<String> ids, UUID userId, Map<String, AffiliatedResource> affiliatedResources, String entityType)  {
		List<String> idsToResolve = new ArrayList<>();
		for (String id : ids){
			AffiliationCacheService.AffiliationCacheValue cacheValue = null;
			try {
				cacheValue = this.affiliationCacheService.lookup(this.affiliationCacheService.buildKey(this.tenantScope.isSet() ? this.tenantScope.getTenant(): null, userId, id, entityType));
			} catch (InvalidApplicationException e) {
				throw new RuntimeException(e);
			}
			if (cacheValue != null) affiliatedResources.put(id, cacheValue.getAffiliatedResource());
			else  idsToResolve.add(id);
		}
		return idsToResolve;
	}
	
	private void ensureAffiliatedInCache(List<String> idsToResolve, UUID userId, Map<String, AffiliatedResource> affiliatedResources, String entityType)  {
		for (String id : idsToResolve){
			AffiliatedResource affiliatedResource = affiliatedResources.getOrDefault(id, null);
			if (affiliatedResource != null) {
				AffiliationCacheService.AffiliationCacheValue cacheValue = null;
				try {
					cacheValue = new AffiliationCacheService.AffiliationCacheValue(this.tenantScope.isSet() ? this.tenantScope.getTenant(): null, userId, id, entityType, affiliatedResource);
				} catch (InvalidApplicationException e) {
					throw new RuntimeException(e);
				}
				this.affiliationCacheService.put(cacheValue);
			}
		}
	}
}
