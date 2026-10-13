package gr.cite.rda.common.scope.tenant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tenant.multitenancy")
public class MultitenancyProperties {
    private boolean isMultitenant;
    private boolean supportExpansionTenant;
    private String defaultTenantCode;

    public boolean isMultitenant() {
        return isMultitenant;
    }

    public void setIsMultitenant(boolean multitenant) {
        isMultitenant = multitenant;
    }

    public void setMultitenant(boolean multitenant) {
        isMultitenant = multitenant;
    }

    public String getDefaultTenantCode() {
        return defaultTenantCode;
    }

    public void setDefaultTenantCode(String defaultTenantCode) {
        this.defaultTenantCode = defaultTenantCode;
    }

    public boolean getSupportExpansionTenant() {
        return supportExpansionTenant;
    }

    public void setSupportExpansionTenant(boolean supportExpansionTenant) {
        this.supportExpansionTenant = supportExpansionTenant;
    }
}