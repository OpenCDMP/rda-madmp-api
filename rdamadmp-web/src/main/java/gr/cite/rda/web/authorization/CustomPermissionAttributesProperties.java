package gr.cite.rda.web.authorization;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.HashMap;

@ConfigurationProperties(prefix = "permissions")
@ConditionalOnProperty(prefix = "permissions", name = "enabled", havingValue = "true")
public class CustomPermissionAttributesProperties {

	private final HashMap<String, MyPermission> policies;

	@ConstructorBinding
	public CustomPermissionAttributesProperties(HashMap<String, MyPermission> policies) {
		this.policies = policies;
	}

	public HashMap<String, MyPermission> getPolicies() {
		return this.policies;
	}

	public static class MyPermission {

		private final DmpRole dmp;

		@ConstructorBinding
		public MyPermission(DmpRole dmp) {
			this.dmp = dmp;
		}

		public DmpRole getDmp() {
			return dmp;
		}
	}

}
