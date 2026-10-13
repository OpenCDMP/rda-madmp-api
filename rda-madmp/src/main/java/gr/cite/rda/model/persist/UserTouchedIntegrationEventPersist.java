package gr.cite.rda.model.persist;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.integrationevent.inbox.usertouched.UserTouchedIntegrationEvent;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserTouchedIntegrationEventPersist {

	private UUID id;
	public static final String _id = "id";

	private String name;

	public static final String _name = "name";

	public static final int _nameLength = 200;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Component(UserTouchedIntegrationEventPersistValidator.ValidatorName)
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public static class UserTouchedIntegrationEventPersistValidator extends BaseValidator<UserTouchedIntegrationEventPersist> {

		public static final String ValidatorName = "UserTouchedIntegrationEventPersistValidator";

		private final MessageSource messageSource;

		protected UserTouchedIntegrationEventPersistValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
			super(conventionService, errors);
			this.messageSource = messageSource;
		}

		@Override
		protected Class<UserTouchedIntegrationEventPersist> modelClass() {
			return UserTouchedIntegrationEventPersist.class;
		}

		@Override
		protected List<Specification> specifications(UserTouchedIntegrationEventPersist item) {
			return Arrays.asList(
					this.spec()
							.iff(() -> !this.isNull(item.getId()))
							.must(() -> this.isValidGuid(item.getId()))
							.failOn(UserTouchedIntegrationEvent._id).failWith(messageSource.getMessage("Validation_Required", new Object[]{UserTouchedIntegrationEventPersist._id}, LocaleContextHolder.getLocale())),
					this.spec()
							.must(() -> !this.isEmpty(item.getName()))
							.failOn(UserTouchedIntegrationEvent._name).failWith(messageSource.getMessage("Validation_Required", new Object[]{UserTouchedIntegrationEventPersist._name}, LocaleContextHolder.getLocale())),
					this.spec()
							.iff(() -> !this.isEmpty(item.getName()))
							.must(() -> this.lessEqualLength(item.getName(), UserTouchedIntegrationEvent._nameLength))
							.failOn(UserTouchedIntegrationEvent._name).failWith(messageSource.getMessage("Validation_MaxLength", new Object[]{UserTouchedIntegrationEventPersist._name}, LocaleContextHolder.getLocale()))
			);
		}
	}
}
