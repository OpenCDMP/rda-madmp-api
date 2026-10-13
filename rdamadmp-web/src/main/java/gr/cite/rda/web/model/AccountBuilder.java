package gr.cite.rda.web.model;

import gr.cite.commons.web.authz.configuration.PermissionPolicyContext;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.data.UserEntity;
import gr.cite.commons.web.authz.configuration.AuthorizationConfiguration;
import gr.cite.commons.web.authz.configuration.Permission;
import gr.cite.commons.web.oidc.principal.CurrentPrincipalResolver;
import gr.cite.commons.web.oidc.principal.MyPrincipal;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractor;
import gr.cite.commons.web.oidc.principal.extractor.ClaimExtractorKeys;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.tools.fieldset.BaseFieldSet;
import gr.cite.tools.fieldset.FieldSet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountBuilder {

	private final ClaimExtractor claimExtractor;
	private final Set<String> excludeMoreClaim;
	private final AuthorizationConfiguration authorizationConfiguration;
	private final CurrentPrincipalResolver currentPrincipalResolver;
	private final UserScope userScope;
	private final TenantEntityManager entityManager;
	private final PermissionPolicyContext permissionPolicyContext;

	public AccountBuilder(ClaimExtractor claimExtractor, AuthorizationConfiguration authorizationConfiguration, CurrentPrincipalResolver currentPrincipalResolver, UserScope userScope, TenantEntityManager entityManager, PermissionPolicyContext permissionPolicyContext) {
		this.claimExtractor = claimExtractor;
		this.authorizationConfiguration = authorizationConfiguration;
		this.currentPrincipalResolver = currentPrincipalResolver;
		this.userScope = userScope;
		this.entityManager = entityManager;
        this.permissionPolicyContext = permissionPolicyContext;
        this.excludeMoreClaim = Set.of(
				ClaimExtractorKeys.Subject,
				ClaimExtractorKeys.Name,
				ClaimExtractorKeys.Scope,
				ClaimExtractorKeys.Client,
				ClaimExtractorKeys.IssuedAt,
				ClaimExtractorKeys.NotBefore,
				ClaimExtractorKeys.AuthenticatedAt,
				ClaimExtractorKeys.ExpiresAt);
	}

	public Account build(FieldSet fields, MyPrincipal principal) throws InvalidApplicationException {
		Account model = new Account();
		if (principal == null || !principal.isAuthenticated()) {
			model.setIsAuthenticated(false);
			return model;
		}
		model.setIsAuthenticated(true);

		FieldSet principalFields = fields.extractPrefixed(BaseFieldSet.asIndexerPrefix(Account._principal));
		if (!principalFields.isEmpty()) model.setPrincipal(new Account.PrincipalInfo());
		if (principalFields.hasField(Account.PrincipalInfo._subject)) model.getPrincipal().setSubject(this.claimExtractor.subjectUUID(principal));
		if (principalFields.hasField(Account.PrincipalInfo._userId)) model.getPrincipal().setUserId(this.userScope.getUserIdSafe());
		if (principalFields.hasField(Account.PrincipalInfo._name)) model.getPrincipal().setName(this.claimExtractor.name(principal));
		if (principalFields.hasField(Account.PrincipalInfo._scope)) model.getPrincipal().setScope(this.claimExtractor.scope(principal));
		if (principalFields.hasField(Account.PrincipalInfo._client)) model.getPrincipal().setClient(this.claimExtractor.client(principal));
		if (principalFields.hasField(Account.PrincipalInfo._issuedAt)) model.getPrincipal().setIssuedAt(this.claimExtractor.issuedAt(principal));
		if (principalFields.hasField(Account.PrincipalInfo._notBefore)) model.getPrincipal().setNotBefore(this.claimExtractor.notBefore(principal));
		if (principalFields.hasField(Account.PrincipalInfo._authenticatedAt)) model.getPrincipal().setAuthenticatedAt(this.claimExtractor.authenticatedAt(principal));
		if (principalFields.hasField(Account.PrincipalInfo._expiresAt)) model.getPrincipal().setExpiresAt(this.claimExtractor.expiresAt(principal));
		if (principalFields.hasField(Account.PrincipalInfo._more)) {
			model.getPrincipal().setMore(new HashMap<>());
			for (String key : this.claimExtractor.knownPublicKeys()) {
				if (this.excludeMoreClaim.contains(key)) continue;
				List<String> values = this.claimExtractor.asStrings(principal, key);
				if (values == null || values.size() == 0) continue;
				if (!model.getPrincipal().getMore().containsKey(key)) model.getPrincipal().getMore().put(key, new ArrayList<>());
				model.getPrincipal().getMore().get(key).addAll(values);
			}
		}

		if (fields.hasField(Account._permissions)) {
			List<String> roles = claimExtractor.roles(currentPrincipalResolver.currentPrincipal());
			Set<String> permissions = this.permissionPolicyContext.permissionsOfRoles(roles);
			for (Map.Entry<String, Permission> permissionEntry : authorizationConfiguration.getRawPolicies().entrySet()){
				if (permissionEntry.getValue().getAllowAuthenticated()){
					permissions.add(permissionEntry.getKey());
				}
			}
			model.setPermissions(new ArrayList<>(permissions));
		}

		UserEntity data = fields.hasField(Account._userExists) && this.userScope.getUserIdSafe() != null ? this.entityManager.find(UserEntity.class, this.userScope.getUserIdSafe()) : null;
		if (fields.hasField(Account._userExists)) model.setUserExists(data != null);
		return model;
	}
}
