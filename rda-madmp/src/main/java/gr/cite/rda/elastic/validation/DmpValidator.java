package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.DmpModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(DmpValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DmpValidator extends BaseValidator<DmpModel> {

        public static final String ValidatorName = "DmpValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected DmpValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<DmpModel> modelClass() {
            return DmpModel.class;
        }


        @Override
        protected List<Specification> specifications(DmpModel item) {
            return Arrays.asList(
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getAlternateIdentifier()))
                            .on(DmpModel._alternateIdentifier)
                            .over(item.getAlternateIdentifier())
                            .using((itm) -> this.validatorFactory.validator(AlternateIdentifierValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getContact()))
                            .failOn(DmpModel._contact).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpModel._contact}, LocaleContextHolder.getLocale())),
                    this.refSpec()
                            .iff(() -> !this.isNull(item.getContact()))
                            .on(DmpModel._contact)
                            .over(item.getContact())
                            .using(() -> this.validatorFactory.validator(ContactValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getContributor()))
                            .on(DmpModel._contributor)
                            .over(item.getContributor())
                            .using((itm) -> this.validatorFactory.validator(ContributorValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getCost()))
                            .on(DmpModel._cost)
                            .over(item.getCost())
                            .using((itm) -> this.validatorFactory.validator(CostValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getCreated()))
                            .failOn(DmpModel._created).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpModel._created}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isListNullOrEmpty(item.getDataset()))
                            .failOn(DmpModel._dataset).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpModel._dataset}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getDataset()))
                            .on(DmpModel._dataset)
                            .over(item.getDataset())
                            .using((itm) -> this.validatorFactory.validator(DatasetValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getDmpId()))
                            .failOn(DmpModel._dmpId).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpModel._dmpId}, LocaleContextHolder.getLocale())),
                    this.refSpec()
                            .iff(() -> !this.isNull(item.getDmpId()))
                            .on(DmpModel._dmpId)
                            .over(item.getDmpId())
                            .using(() -> this.validatorFactory.validator(DmpIdValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getEthicalIssuesExist()))
                            .failOn(DmpModel._ethicalIssuesExist).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpModel._ethicalIssuesExist}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isNull(item.getLanguage()))
                            .failOn(DmpModel._language).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpModel._language}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isNull(item.getModified()))
                            .failOn(DmpModel._modified).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpModel._modified}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getProject()))
                            .on(DmpModel._project)
                            .over(item.getProject())
                            .using((itm) -> this.validatorFactory.validator(ProjectValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getRelatedIdentifier()))
                            .on(DmpModel._relatedIdentifier)
                            .over(item.getRelatedIdentifier())
                            .using((itm) -> this.validatorFactory.validator(RelatedIdentifierValidator.class)),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getTitle()))
                            .failOn(DmpModel._title).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpModel._title}, LocaleContextHolder.getLocale()))
            );
        }


}
