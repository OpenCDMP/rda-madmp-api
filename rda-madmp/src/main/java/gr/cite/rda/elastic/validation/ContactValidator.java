package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.ContactModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(gr.cite.rda.elastic.validation.ContactValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ContactValidator extends BaseValidator<ContactModel> {

        public static final String ValidatorName = "ContactValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected ContactValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<ContactModel> modelClass() {
            return ContactModel.class;
        }


        @Override
        protected List<Specification> specifications(ContactModel item) {
            return Arrays.asList(
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getAffiliation()))
                            .on(ContactModel._affiliation)
                            .over(item.getAffiliation())
                            .using((itm) -> this.validatorFactory.validator(AffiliationValidator.class)),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getMbox()))
                            .failOn(ContactModel._mbox).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContactModel._mbox}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getName()))
                            .failOn(ContactModel._name).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContactModel._name}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isNull(item.getContactId()))
                            .failOn(ContactModel._contactId).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ContactModel._contactId}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getContactId()))
                            .on(ContactModel._contactId)
                            .over(item.getContactId())
                            .using((itm) -> this.validatorFactory.validator(ContactIdValidator.class))
            );
        }
    }


