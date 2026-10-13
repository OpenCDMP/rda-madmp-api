package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.CreatorIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(CreatorIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreatorIdValidator extends BaseValidator<CreatorIdModel> {

        public static final String ValidatorName = "CreatorIdValidator";

        private final MessageSource messageSource;

        protected CreatorIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<CreatorIdModel> modelClass() {
            return CreatorIdModel.class;
        }


        @Override
        protected List<Specification> specifications(CreatorIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(CreatorIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{CreatorIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(CreatorIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{CreatorIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


