package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.SecurityAndPrivacyModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(SecurityAndPrivacyValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityAndPrivacyValidator extends BaseValidator<SecurityAndPrivacyModel> {

        public static final String ValidatorName = "SecurityAndPrivacyValidator";

        private final MessageSource messageSource;

        protected SecurityAndPrivacyValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<SecurityAndPrivacyModel> modelClass() {
            return SecurityAndPrivacyModel.class;
        }

        @Override
        protected List<Specification> specifications(SecurityAndPrivacyModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getTitle()))
                            .failOn(SecurityAndPrivacyModel._title).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{SecurityAndPrivacyModel._title}, LocaleContextHolder.getLocale()))
            );
        }
    }


