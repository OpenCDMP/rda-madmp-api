package gr.cite.rda.web.config.swagger;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SwaggerConfigProperties.class, SwaggerSpringDocProperties.class})
public class SwaggerConfiguration {

	private final SwaggerSpringDocProperties springDocProperties;
	private final SwaggerConfigProperties configProperties;

    public SwaggerConfiguration(SwaggerSpringDocProperties springDocProperties, SwaggerConfigProperties configProperties) {
        this.springDocProperties = springDocProperties;
        this.configProperties = configProperties;
    }

	public SwaggerSpringDocProperties getSpringDocProperties() {
		return springDocProperties;
	}

	public SwaggerConfigProperties getConfigProperties() {
		return configProperties;
	}
}
