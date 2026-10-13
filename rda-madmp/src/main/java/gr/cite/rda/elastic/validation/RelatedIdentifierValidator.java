package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.AlternateIdentifierModel;
import gr.cite.rda.model.rdadmp.RelatedIdentifierModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(RelatedIdentifierValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RelatedIdentifierValidator extends BaseValidator<RelatedIdentifierModel> {

        public static final String ValidatorName = "RelatedIdentifierValidator";

        private final MessageSource messageSource;

        protected RelatedIdentifierValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<RelatedIdentifierModel> modelClass() {
            return RelatedIdentifierModel.class;
        }


        @Override
        protected List<Specification> specifications(RelatedIdentifierModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(RelatedIdentifierModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{RelatedIdentifierModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getRelationType()))
                            .failOn(RelatedIdentifierModel._relationType).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{RelatedIdentifierModel._relationType}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(RelatedIdentifierModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{RelatedIdentifierModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


