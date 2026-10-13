package gr.cite.rda.service.user;

import tools.jackson.core.JacksonException;
import gr.cite.rda.integrationevent.inbox.usertouched.UserTouchedIntegrationEvent;
import gr.cite.rda.model.User;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;

import javax.management.InvalidApplicationException;
import java.util.UUID;

public interface UserService {

    User persist(UserTouchedIntegrationEvent model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException, JacksonException;

    void deleteAndSave(UUID id) throws MyForbiddenException, InvalidApplicationException;

}
