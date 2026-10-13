package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.ContactIdModel;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(ContactIdValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContactIdValidator extends BaseValidator<ContactIdModel> {

        public static final String ValidatorName = "ContactIdValidator";

        private final MessageSource messageSource;

        protected ContactIdValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<ContactIdModel> modelClass() {
            return ContactIdModel.class;
        }


        @Override
        protected List<Specification> specifications(ContactIdModel item) {
            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isEmpty(item.getIdentifier()))
                            .failOn(ContactIdModel._identifier).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContactIdModel._identifier}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getType()))
                            .failOn(ContactIdModel._type).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContactIdModel._type}, LocaleContextHolder.getLocale()))
            );
        }
    }


