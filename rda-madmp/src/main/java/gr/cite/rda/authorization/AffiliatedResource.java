package gr.cite.rda.authorization;

import gr.cite.commons.web.authz.policy.AuthorizationResource;
import org.opencdmp.commonmodels.enums.PlanUserRole;

import java.util.HashSet;

public class AffiliatedResource extends AuthorizationResource {
	private HashSet<PlanUserRole> dmpUserRoles;

	public AffiliatedResource() {
		this.dmpUserRoles = new HashSet<>();
	}

	public HashSet<PlanUserRole> getDmpUserRoles() {
		return dmpUserRoles;
	}

	public void setDmpUserRoles(HashSet<PlanUserRole> dmpUserRoles) {
		this.dmpUserRoles = dmpUserRoles;
	}
}
