package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.AffiliationModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(AffiliationValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AffiliationValidator extends BaseValidator<AffiliationModel> {

        public static final String ValidatorName = "AffiliationValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected AffiliationValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<AffiliationModel> modelClass() {
            return AffiliationModel.class;
        }


        @Override
        protected List<Specification> specifications(AffiliationModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getName()))
                            .failOn(AffiliationModel._name).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{AffiliationModel._name}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isNull(item.getAffiliationId()))
                            .failOn(AffiliationModel._affiliationId).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{AffiliationModel._affiliationId}, LocaleContextHolder.getLocale())),
                    this.refSpec()
                            .iff(() -> !this.isNull(item.getAffiliationId()))
                            .on(AffiliationModel._affiliationId)
                            .over(item.getAffiliationId())
                            .using(() -> this.validatorFactory.validator(AffiliationIdValidator.class))
            );
        }
    }


