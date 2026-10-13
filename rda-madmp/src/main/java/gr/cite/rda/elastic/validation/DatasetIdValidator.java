package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.DatasetIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(DatasetIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DatasetIdValidator extends BaseValidator<DatasetIdModel> {

        public static final String ValidatorName = "DatasetIdValidator";

        private final MessageSource messageSource;

        protected DatasetIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<DatasetIdModel> modelClass() {
            return DatasetIdModel.class;
        }


        @Override
        protected List<Specification> specifications(DatasetIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(DatasetIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DatasetIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(DatasetIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DatasetIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


