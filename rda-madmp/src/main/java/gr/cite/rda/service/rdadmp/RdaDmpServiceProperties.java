package gr.cite.rda.service.rdadmp;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "rda-madmp")
public class RdaDmpServiceProperties {
	private String transformerId;

	private boolean useActionType;

	public String getTransformerId() {
		return transformerId;
	}

	public void setTransformerId(String transformerId) {
		this.transformerId = transformerId;
	}

	public boolean isUseActionType() {
		return useActionType;
	}

	public void setUseActionType(boolean useActionType) {
		this.useActionType = useActionType;
	}
}
