package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.HostModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(HostValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostValidator extends BaseValidator<HostModel> {

        public static final String ValidatorName = "HostValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected HostValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<HostModel> modelClass() {
            return HostModel.class;
        }

        @Override
        protected List<Specification> specifications(HostModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getTitle()))
                            .failOn(HostModel._title).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{HostModel._title}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getHostId()))
                            .on(HostModel._hostId)
                            .over(item.getHostId())
                            .using((itm) -> this.validatorFactory.validator(HostIdValidator.class))
            );
        }
    }


