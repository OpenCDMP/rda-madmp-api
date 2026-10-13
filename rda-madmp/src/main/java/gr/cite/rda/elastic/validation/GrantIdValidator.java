package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.GrantIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(GrantIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GrantIdValidator extends BaseValidator<GrantIdModel> {

        public static final String ValidatorName = "GrantIdValidator";

        private final MessageSource messageSource;

        protected GrantIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<GrantIdModel> modelClass() {
            return GrantIdModel.class;
        }


        @Override
        protected List<Specification> specifications(GrantIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(GrantIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{GrantIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(GrantIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{GrantIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


