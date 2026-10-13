package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.DmpIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(gr.cite.rda.elastic.validation.DmpIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DmpIdValidator extends BaseValidator<DmpIdModel> {

        public static final String ValidatorName = "DmpIdValidator";

        private final MessageSource messageSource;

        protected DmpIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<DmpIdModel> modelClass() {
            return DmpIdModel.class;
        }


        @Override
        protected List<Specification> specifications(DmpIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(DmpIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(DmpIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


