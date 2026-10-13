package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.DatasetModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(DatasetValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DatasetValidator extends BaseValidator<DatasetModel> {

        public static final String ValidatorName = "DatasetValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected DatasetValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<DatasetModel> modelClass() {
            return DatasetModel.class;
        }


        @Override
        protected List<Specification> specifications(DatasetModel item) {
            return Arrays.asList(
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getAlternateIdentifier()))
                            .on(DatasetModel._alternateIdentifier)
                            .over(item.getAlternateIdentifier())
                            .using((itm) -> this.validatorFactory.validator(AlternateIdentifierValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getCreator()))
                            .on(DatasetModel._creator)
                            .over(item.getCreator())
                            .using((itm) -> this.validatorFactory.validator(CreatorValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getDatasetId()))
                            .failOn(DatasetModel._datasetId).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DatasetModel._datasetId}, LocaleContextHolder.getLocale())),
                    this.refSpec()
                            .iff(() -> !this.isNull(item.getDatasetId()))
                            .on(DatasetModel._datasetId)
                            .over(item.getDatasetId())
                            .using(() -> this.validatorFactory.validator(DatasetIdValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getDistribution()))
                            .on(DatasetModel._distribution)
                            .over(item.getDistribution())
                            .using((itm) -> this.validatorFactory.validator(DistributionValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getMetadata()))
                            .on(DatasetModel._metadata)
                            .over(item.getMetadata())
                            .using((itm) -> this.validatorFactory.validator(MetadataValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getPersonalData()))
                            .failOn(DatasetModel._personalData).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DatasetModel._personalData}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getRelatedIdentifier()))
                            .on(DatasetModel._relatedIdentifier)
                            .over(item.getRelatedIdentifier())
                            .using((itm) -> this.validatorFactory.validator(RelatedIdentifierValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getSecurityAndPrivacy()))
                            .on(DatasetModel._securityAndPrivacy)
                            .over(item.getSecurityAndPrivacy())
                            .using((itm) -> this.validatorFactory.validator(SecurityAndPrivacyValidator.class)),
                    this.spec()
                            .must(() -> !this.isNull(item.getSensitiveData()))
                            .failOn(DatasetModel._sensitiveData).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DatasetModel._sensitiveData}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getTechnicalResource()))
                            .on(DatasetModel._technicalResource)
                            .over(item.getTechnicalResource())
                            .using((itm) -> this.validatorFactory.validator(TechnicalResourceValidator.class)),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getTitle()))
                            .failOn(DatasetModel._title).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DatasetModel._title}, LocaleContextHolder.getLocale()))
            );
        }


}
