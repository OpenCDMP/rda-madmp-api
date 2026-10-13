package gr.cite.rda.data.tenant;

import gr.cite.rda.common.scope.tenant.TenantScope;
import gr.cite.rda.common.scope.tenant.TenantScoped;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.logging.LoggerService;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public class TenantListener {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantListener.class));
	private final TenantScope tenantScope;

	private final ErrorThesaurusProperties errors;
	private final TenantEntityManager tenantEntityManager;


	@Autowired
	public TenantListener(
			TenantScope tenantScope, ErrorThesaurusProperties errors, TenantEntityManager tenantEntityManager
	) {
		this.tenantScope = tenantScope;
		this.errors = errors;
		this.tenantEntityManager = tenantEntityManager;
	}

	@PrePersist
	public void setTenantOnCreate(TenantScoped entity) throws InvalidApplicationException {
		if (this.tenantEntityManager.isTenantFiltersDisabled()) return;
		if (tenantScope.isMultitenant()) {
			if (entity.getTenantId() != null && (this.tenantScope.isDefaultTenant() || entity.getTenantId().compareTo(tenantScope.getTenant()) != 0)) {
				logger.error("somebody tried to set not login tenant");
				throw new MyForbiddenException(this.errors.getTenantTampering().getCode(), this.errors.getTenantTampering().getMessage());
			}
			if (!tenantScope.isDefaultTenant()) {
				final UUID tenantId = tenantScope.getTenant();
				entity.setTenantId(tenantId);
			}
		} else {
			entity.setTenantId(null);
		}
	}

	@PreUpdate
	@PreRemove
	public void setTenantOnUpdate(TenantScoped entity) throws InvalidApplicationException {
		if (this.tenantEntityManager.isTenantFiltersDisabled()) return;
		if (tenantScope.isMultitenant()) {
			if (!tenantScope.isDefaultTenant()) {
				if (entity.getTenantId() == null) {
					logger.error("somebody tried to set null tenant");
					throw new MyForbiddenException(this.errors.getTenantTampering().getCode(), this.errors.getTenantTampering().getMessage());
				}
				if (entity.getTenantId().compareTo(tenantScope.getTenant()) != 0) {
					logger.error("somebody tried to change an entries tenant");
					throw new MyForbiddenException(this.errors.getTenantTampering().getCode(), this.errors.getTenantTampering().getMessage());
				}

				final UUID tenantId = tenantScope.getTenant();
				entity.setTenantId(tenantId);
			} else {
				if (entity.getTenantId() != null) {
					logger.error("somebody tried to set null tenant");
					throw new MyForbiddenException(this.errors.getTenantTampering().getCode(), this.errors.getTenantTampering().getMessage());
				}
			}
		} else {
			if (entity.getTenantId() != null) {
				logger.error("somebody tried to change an entries tenant");
				throw new MyForbiddenException(this.errors.getTenantTampering().getCode(), this.errors.getTenantTampering().getMessage());
			}
		}

	}
}

