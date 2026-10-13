package gr.cite.rda.common.validation;

import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component(UuidValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UuidValidator extends BaseValidator<UUID> {

    public static final String ValidatorName = "UuidValidator";

    private final MessageSource messageSource;

    protected UuidValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
        super(conventionService, errors);
        this.messageSource = messageSource;
    }

    @Override
    protected Class<UUID> modelClass() {
        return UUID.class;
    }

    @Override
    protected List<Specification> specifications(UUID item) {
        return Collections.singletonList(
		        this.spec()
				        .must(() -> this.isValidGuid(item))
				        .failOn("uuid").failWith(messageSource.getMessage("Validation_Required", new Object[]{"uuid"}, LocaleContextHolder.getLocale()))
        );
    }
}
