package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.CreatorModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(CreatorValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreatorValidator extends BaseValidator<CreatorModel> {

        public static final String ValidatorName = "CreatorValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected CreatorValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<CreatorModel> modelClass() {
            return CreatorModel.class;
        }


        @Override
        protected List<Specification> specifications(CreatorModel item) {
            return Arrays.asList(
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getAffiliation()))
                            .on(CreatorModel._affiliation)
                            .over(item.getAffiliation())
                            .using((itm) -> this.validatorFactory.validator(AffiliationValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getCreatorId()))
                            .failOn(CreatorModel._creator_id).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{CreatorModel._creator_id}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getCreatorId()))
                            .on(CreatorModel._creator_id)
                            .over(item.getCreatorId())
                            .using((itm) -> this.validatorFactory.validator(CreatorIdValidator.class)),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getName()))
                            .failOn(CreatorModel._name).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{CreatorModel._name}, LocaleContextHolder.getLocale()))

            );
        }
    }


