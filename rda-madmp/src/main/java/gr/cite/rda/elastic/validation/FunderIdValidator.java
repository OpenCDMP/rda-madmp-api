package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.FunderIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(FunderIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FunderIdValidator extends BaseValidator<FunderIdModel> {

        public static final String ValidatorName = "FunderIdValidator";

        private final MessageSource messageSource;

        protected FunderIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<FunderIdModel> modelClass() {
            return FunderIdModel.class;
        }


        @Override
        protected List<Specification> specifications(FunderIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(FunderIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{FunderIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(FunderIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{FunderIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


