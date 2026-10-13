package gr.cite.rda.elastic.validation;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.ProjectModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(ProjectValidator.ValidatorName)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProjectValidator extends BaseValidator<ProjectModel> {

        public static final String ValidatorName = "ProjectValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected ProjectValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
            this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<ProjectModel> modelClass() {
            return ProjectModel.class;
        }


        @Override
        protected List<Specification> specifications(ProjectModel item) {
            return Arrays.asList(
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getFunding()))
                            .on(ProjectModel._funding)
                            .over(item.getFunding())
                            .using((itm) -> this.validatorFactory.validator(FundingValidator.class)),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getProjectId()))
                            .on(ProjectModel._projectId)
                            .over(item.getProjectId())
                            .using((itm) -> this.validatorFactory.validator(ProjectIdValidator.class)),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getTitle()))
                            .failOn(ProjectModel._title).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{ProjectModel._title}, LocaleContextHolder.getLocale()))
            );
        }


}
