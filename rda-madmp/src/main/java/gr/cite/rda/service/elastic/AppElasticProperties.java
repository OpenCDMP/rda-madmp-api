package gr.cite.rda.service.elastic;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app-elastic")
public class AppElasticProperties {
	private String rdaIndexName;
	private int resetBatchSize;
	private boolean enableIcuAnalysisPlugin;

	public String getRdaIndexName() {
		return this.rdaIndexName;
	}

	public void setRdaIndexName(String planIndexName) {
		this.rdaIndexName = planIndexName;
	}

	public boolean isEnableIcuAnalysisPlugin() {
		return this.enableIcuAnalysisPlugin;
	}

	public void setEnableIcuAnalysisPlugin(boolean enableIcuAnalysisPlugin) {
		this.enableIcuAnalysisPlugin = enableIcuAnalysisPlugin;
	}

	public int getResetBatchSize() {
		return this.resetBatchSize;
	}

	public void setResetBatchSize(int resetBatchSize) {
		this.resetBatchSize = resetBatchSize;
	}
}
