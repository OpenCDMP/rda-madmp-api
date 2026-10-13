package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.TechnicalResourceIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(TechnicalResourceIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TechnicalResourceIdValidator extends BaseValidator<TechnicalResourceIdModel> {

        public static final String ValidatorName = "TechnicalResourceIdValidator";

        private final MessageSource messageSource;

        protected TechnicalResourceIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<TechnicalResourceIdModel> modelClass() {
            return TechnicalResourceIdModel.class;
        }


        @Override
        protected List<Specification> specifications(TechnicalResourceIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(TechnicalResourceIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{TechnicalResourceIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(TechnicalResourceIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{TechnicalResourceIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


