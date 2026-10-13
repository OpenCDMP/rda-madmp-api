package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.LicenseModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(LicenseValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LicenseValidator extends BaseValidator<LicenseModel> {

        public static final String ValidatorName = "LicenseValidator";

        private final MessageSource messageSource;

        protected LicenseValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<LicenseModel> modelClass() {
            return LicenseModel.class;
        }

        @Override
        protected List<Specification> specifications(LicenseModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isNull(item.getLicenseRef()))
                            .failOn(LicenseModel._licenseRef).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{LicenseModel._licenseRef}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isNull(item.getStartDate()))
                            .failOn(LicenseModel._startDate).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{LicenseModel._startDate}, LocaleContextHolder.getLocale()))
            );
        }
    }


