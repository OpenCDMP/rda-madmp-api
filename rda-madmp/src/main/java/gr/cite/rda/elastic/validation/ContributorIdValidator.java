package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.ContributorIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(ContributorIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContributorIdValidator extends BaseValidator<ContributorIdModel> {

        public static final String ValidatorName = "ContributorIdValidator";

        private final MessageSource messageSource;

        protected ContributorIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<ContributorIdModel> modelClass() {
            return ContributorIdModel.class;
        }


        @Override
        protected List<Specification> specifications(ContributorIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(ContributorIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContributorIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(ContributorIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContributorIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


