package gr.cite.rda.web.config.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "swagger-config")
public class SwaggerConfigProperties {

	private String serverUrl;
	private SwaggerGroupConfig group;

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public SwaggerGroupConfig getGroup() {
		return group;
	}

	public void setGroup(SwaggerGroupConfig group) {
		this.group = group;
	}
}
