package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.ContributorModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(ContributorValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContributorValidator extends BaseValidator<ContributorModel> {

        public static final String ValidatorName = "ContributorValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected ContributorValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<ContributorModel> modelClass() {
            return ContributorModel.class;
        }


        @Override
        protected List<Specification> specifications(ContributorModel item) {
            return Arrays.asList(
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getAffiliation()))
                            .on(ContributorModel._affiliation)
                            .over(item.getAffiliation())
                            .using((itm) -> this.validatorFactory.validator(AffiliationValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getContributorId()))
                            .failOn(ContributorModel._contributorId).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContributorModel._contributorId}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getContributorId()))
                            .on(ContributorModel._contributorId)
                            .over(item.getContributorId())
                            .using((itm) -> this.validatorFactory.validator(ContributorIdValidator.class)),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getName()))
                            .failOn(ContributorModel._name).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContributorModel._name}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isListNullOrEmpty(item.getRole()))
                            .failOn(ContributorModel._role).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContributorModel._role}, LocaleContextHolder.getLocale()))

            );
        }
    }


