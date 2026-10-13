package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.MetadataModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(MetadataValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MetadataValidator extends BaseValidator<MetadataModel> {

        public static final String ValidatorName = "MetadataValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected MetadataValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<MetadataModel> modelClass() {
            return MetadataModel.class;
        }


        @Override
        protected List<Specification> specifications(MetadataModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isNull(item.getLanguage()))
                            .failOn(MetadataModel._language).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{MetadataModel._language}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isListNullOrEmpty(item.getMetadataStandardId()))
                            .failOn(MetadataModel._metadataStandardId).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{MetadataModel._metadataStandardId}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getMetadataStandardId()))
                            .on(MetadataModel._metadataStandardId)
                            .over(item.getMetadataStandardId())
                            .using((itm) -> this.validatorFactory.validator(StandardIdValidator.class))
            );
        }


}
