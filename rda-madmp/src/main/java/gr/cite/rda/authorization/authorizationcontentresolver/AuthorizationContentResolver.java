package gr.cite.rda.authorization.authorizationcontentresolver;


import gr.cite.rda.authorization.AffiliatedResource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AuthorizationContentResolver {
	List<String> getPermissionNames();

	AffiliatedResource dmpAffiliation(String id);

	Map<String, AffiliatedResource> dmpsAffiliation(List<String> ids);
}
