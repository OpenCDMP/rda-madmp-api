package gr.cite.rda.service.tenant;

import tools.jackson.core.JacksonException;
import gr.cite.rda.model.Tenant;
import gr.cite.rda.model.persist.TenantTouchedIntegrationEventPersist;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface TenantService {

    Tenant persist(TenantTouchedIntegrationEventPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, JacksonException;

    void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;

}
