package gr.cite.rda.web.authorization;

import gr.cite.rda.authorization.AffiliatedAuthorizationRequirement;
import gr.cite.rda.authorization.AffiliatedResource;
import gr.cite.commons.web.authz.handler.AuthorizationHandler;
import gr.cite.commons.web.authz.handler.AuthorizationHandlerContext;
import gr.cite.commons.web.authz.policy.AuthorizationRequirement;
import gr.cite.commons.web.oidc.principal.MyPrincipal;
import org.opencdmp.commonmodels.enums.PlanUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component("affiliatedAuthorizationHandler")
public class AffiliatedAuthorizationHandler extends AuthorizationHandler<AffiliatedAuthorizationRequirement> {

	private final CustomPermissionAttributesConfiguration myConfiguration;

	@Autowired
	public AffiliatedAuthorizationHandler(CustomPermissionAttributesConfiguration myConfiguration) {
		this.myConfiguration = myConfiguration;
	}

	@Override
	public int handleRequirement(AuthorizationHandlerContext context, Object resource, AuthorizationRequirement requirement) {
		AffiliatedAuthorizationRequirement req = (AffiliatedAuthorizationRequirement) requirement;
		if (req.getRequiredPermissions() == null)
			return ACCESS_NOT_DETERMINED;

		AffiliatedResource rs = (AffiliatedResource) resource;

		boolean isAuthenticated = ((MyPrincipal) context.getPrincipal()).isAuthenticated();
		if (!isAuthenticated)
			return ACCESS_NOT_DETERMINED;

		if (this.myConfiguration.getMyPolicies() == null)
			return ACCESS_NOT_DETERMINED;

		int hits = 0;
		HashSet<PlanUserRole> dmpUserRoles = rs != null && rs.getDmpUserRoles() != null ? rs.getDmpUserRoles() : null;

		for (String permission : req.getRequiredPermissions()) {
			CustomPermissionAttributesProperties.MyPermission policy = this.myConfiguration.getMyPolicies().get(permission);
			boolean hasPlanPermission = policy != null && this.hasPermission(policy.getDmp(), dmpUserRoles);

			if (hasPlanPermission) hits += 1;
		}
		if ((req.getMatchAll() && req.getRequiredPermissions().size() == hits) || (!req.getMatchAll() && hits > 0))
			return ACCESS_GRANTED;

		return ACCESS_NOT_DETERMINED;
	}


	private Boolean hasPermission(DmpRole dmpRole, HashSet<PlanUserRole> roles) {
		if (roles == null)
			return Boolean.FALSE;
		if (dmpRole == null || dmpRole.getRoles() == null)
			return Boolean.FALSE;
		for (PlanUserRole role : dmpRole.getRoles()) {
			if (roles.contains(role))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@Override
	public Class<? extends AuthorizationRequirement> supporting() {
		return AffiliatedAuthorizationRequirement.class;
	}

}
