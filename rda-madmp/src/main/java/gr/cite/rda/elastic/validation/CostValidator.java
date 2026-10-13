package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.CostModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(CostValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CostValidator extends BaseValidator<CostModel> {

        public static final String ValidatorName = "CostValidator";

        private final MessageSource messageSource;


        protected CostValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<CostModel> modelClass() {
            return CostModel.class;
        }


        @Override
        protected List<Specification> specifications(CostModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getTitle()))
                            .failOn(CostModel._title).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{CostModel._title}, LocaleContextHolder.getLocale()))
            );
        }
    }


