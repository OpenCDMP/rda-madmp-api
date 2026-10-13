package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.MetadataStandardIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(StandardIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StandardIdValidator extends BaseValidator<MetadataStandardIdModel> {

        public static final String ValidatorName = "StandardIdValidator";

        private final MessageSource messageSource;

        protected StandardIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<MetadataStandardIdModel> modelClass() {
            return MetadataStandardIdModel.class;
        }


        @Override
        protected List<Specification> specifications(MetadataStandardIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(MetadataStandardIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{MetadataStandardIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(MetadataStandardIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{MetadataStandardIdModel._type}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> this.isEmpty(item.getType()) || "url".equals(item.getType()) || "other".equals(item.getType()))
                            .failOn(MetadataStandardIdModel._type).failWith("Allowed values for '" + MetadataStandardIdModel._type + "': url, other")
            );
        }
    }


