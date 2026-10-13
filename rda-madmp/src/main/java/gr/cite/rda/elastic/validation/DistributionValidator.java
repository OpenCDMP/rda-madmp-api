package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.DistributionModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(DistributionValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DistributionValidator extends BaseValidator<DistributionModel> {

        public static final String ValidatorName = "DistributionValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected DistributionValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<DistributionModel> modelClass() {
            return DistributionModel.class;
        }


        @Override
        protected List<Specification> specifications(DistributionModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isNull(item.getDataAccess()))
                            .failOn(DistributionModel._dataAccess).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DistributionModel._dataAccess}, LocaleContextHolder.getLocale())),
                    this.refSpec()
                            .iff(() -> !this.isNull(item.getHost()))
                            .on(DistributionModel._host)
                            .over(item.getHost())
                            .using(() -> this.validatorFactory.validator(HostValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getLicense()))
                            .on(DistributionModel._license)
                            .over(item.getLicense())
                            .using((itm) -> this.validatorFactory.validator(LicenseValidator.class)),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getTitle()))
                            .failOn(DistributionModel._title).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DistributionModel._title}, LocaleContextHolder.getLocale()))
            );
        }


}
