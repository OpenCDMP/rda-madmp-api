package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.FundingModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(FundingValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FundingValidator extends BaseValidator<FundingModel> {

        public static final String ValidatorName = "FundingValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected FundingValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<FundingModel> modelClass() {
            return FundingModel.class;
        }


        @Override
        protected List<Specification> specifications(FundingModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isNull(item.getFunderId()))
                            .failOn(FundingModel._funderId).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{FundingModel._funderId}, LocaleContextHolder.getLocale())),
                    this.refSpec()
                            .iff(() -> !this.isNull(item.getFunderId()))
                            .on(FundingModel._funderId)
                            .over(item.getFunderId())
                            .using(() -> this.validatorFactory.validator(FunderIdValidator.class)),
                    this.refSpec()
                            .iff(() -> !this.isNull(item.getGrantId()))
                            .on(FundingModel._grantId)
                            .over(item.getGrantId())
                            .using(() -> this.validatorFactory.validator(GrantIdValidator.class))
            );
        }


}
