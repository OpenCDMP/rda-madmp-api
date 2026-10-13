package gr.cite.rda.service.elastic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AppElasticProperties.class})
public class AppElasticConfiguration {
	private final AppElasticProperties appElasticProperties;

	@Autowired
	public AppElasticConfiguration(AppElasticProperties appElasticProperties) {
		this.appElasticProperties = appElasticProperties;
	}

	public AppElasticProperties getAppElasticProperties() {
		return this.appElasticProperties;
	}

}
