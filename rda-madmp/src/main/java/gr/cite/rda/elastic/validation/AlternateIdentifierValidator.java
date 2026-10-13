package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.AlternateIdentifierModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(AlternateIdentifierValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AlternateIdentifierValidator extends BaseValidator<AlternateIdentifierModel> {

        public static final String ValidatorName = "AlternateIdentifierValidator";

        private final MessageSource messageSource;

        protected AlternateIdentifierValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<AlternateIdentifierModel> modelClass() {
            return AlternateIdentifierModel.class;
        }


        @Override
        protected List<Specification> specifications(AlternateIdentifierModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(AlternateIdentifierModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{AlternateIdentifierModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(AlternateIdentifierModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{AlternateIdentifierModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


