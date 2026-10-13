package gr.cite.rda.web.authorization;


import org.opencdmp.commonmodels.enums.PlanUserRole;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Set;

public class DmpRole {
	private final Set<PlanUserRole> roles;

	@ConstructorBinding
	public DmpRole(Set<PlanUserRole> roles) {
		this.roles = roles;
	}

	public Set<PlanUserRole> getRoles() {
		return this.roles;
	}

}
