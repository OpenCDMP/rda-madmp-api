package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.TechnicalResourceModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(TechnicalResourceValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TechnicalResourceValidator extends BaseValidator<TechnicalResourceModel> {

        public static final String ValidatorName = "TechnicalResourceValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected TechnicalResourceValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<TechnicalResourceModel> modelClass() {
            return TechnicalResourceModel.class;
        }

        @Override
        protected List<Specification> specifications(TechnicalResourceModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getName()))
                            .failOn(TechnicalResourceModel._name).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{TechnicalResourceModel._name}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getTechnicalResourceId()))
                            .on(TechnicalResourceModel._technicalResourceId)
                            .over(item.getTechnicalResourceId())
                            .using((itm) -> this.validatorFactory.validator(TechnicalResourceIdValidator.class))
            );
        }
    }


