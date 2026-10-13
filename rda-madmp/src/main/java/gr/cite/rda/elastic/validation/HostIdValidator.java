package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.HostIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(HostIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostIdValidator extends BaseValidator<HostIdModel> {

        public static final String ValidatorName = "HostIdValidator";

        private final MessageSource messageSource;

        protected HostIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<HostIdModel> modelClass() {
            return HostIdModel.class;
        }


        @Override
        protected List<Specification> specifications(HostIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(HostIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{HostIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(HostIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{HostIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


