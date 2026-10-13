package gr.cite.rda.web.config.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "springdoc")
public class SwaggerSpringDocProperties {

	private SwaggerPathProperties apiDocs;
	private SwaggerPathProperties swaggerUi;

	public SwaggerPathProperties getApiDocs() {
		return apiDocs;
	}

	public void setApiDocs(SwaggerPathProperties apiDocs) {
		this.apiDocs = apiDocs;
	}

	public SwaggerPathProperties getSwaggerUi() {
		return swaggerUi;
	}

	public void setSwaggerUi(SwaggerPathProperties swaggerUi) {
		this.swaggerUi = swaggerUi;
	}
}
