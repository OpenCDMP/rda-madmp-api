package gr.cite.rda.errorcode;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "error-thesaurus")
public class ErrorThesaurusProperties {

	private ErrorDescription hashConflict;
	private ErrorDescription forbidden;
	private ErrorDescription systemError;
	private ErrorDescription missingTenant;
	private ErrorDescription modelValidation;
	private ErrorDescription tenantNotAllowed;
	private ErrorDescription tenantTampering;

	public ErrorDescription getHashConflict() {
		return hashConflict;
	}

	public void setHashConflict(ErrorDescription hashConflict) {
		this.hashConflict = hashConflict;
	}

	public ErrorDescription getForbidden() {
		return forbidden;
	}

	public void setForbidden(ErrorDescription forbidden) {
		this.forbidden = forbidden;
	}

	public ErrorDescription getSystemError() {
		return systemError;
	}

	public void setSystemError(ErrorDescription systemError) {
		this.systemError = systemError;
	}

	public ErrorDescription getMissingTenant() {
		return missingTenant;
	}

	public void setMissingTenant(ErrorDescription missingTenant) {
		this.missingTenant = missingTenant;
	}

	public ErrorDescription getModelValidation() {
		return modelValidation;
	}

	public void setModelValidation(ErrorDescription modelValidation) {
		this.modelValidation = modelValidation;
	}

	public ErrorDescription getTenantNotAllowed() {
		return tenantNotAllowed;
	}

	public void setTenantNotAllowed(ErrorDescription tenantNotAllowed) {
		this.tenantNotAllowed = tenantNotAllowed;
	}

	public ErrorDescription getTenantTampering() {
		return tenantTampering;
	}

	public void setTenantTampering(ErrorDescription tenantTampering) {
		this.tenantTampering = tenantTampering;
	}
}
