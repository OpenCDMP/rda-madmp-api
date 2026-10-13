package gr.cite.rda.service.rdadmp;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RdaDmpServiceProperties.class})
public class RdaDmpServiceConfiguration {
	private final RdaDmpServiceProperties rdaDmpServiceProperties;

	public RdaDmpServiceConfiguration(RdaDmpServiceProperties rdaDmpServiceProperties) {
		this.rdaDmpServiceProperties = rdaDmpServiceProperties;
	}

	public RdaDmpServiceProperties getRdaDmpServiceProperties() {
		return rdaDmpServiceProperties;
	}
}
