package gr.cite.rda.elastic.persist;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.validation.DmpValidator;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.model.rdadmp.DmpModel;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Schema(name = "DMPDocument")
public class DmpRootPersist {

    private DmpModel dmp;
    public static final String _dmp = "dmp";

    public DmpModel getDmp() {
        return dmp;
    }

    public void setDmp(DmpModel dmp) {
        this.dmp = dmp;
    }

    @Component(DmpRootPersistValidator.ValidatorName)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public static class DmpRootPersistValidator extends BaseValidator<DmpRootPersist> {

        public static final String ValidatorName = "DmpRootPersistValidator";

        private final ValidatorFactory validatorFactory;

        private final MessageSource messageSource;

        protected DmpRootPersistValidator(ConventionService conventionService, ErrorThesaurusProperties errors, ValidatorFactory validatorFactory, MessageSource messageSource) {
            super(conventionService, errors);
            this.validatorFactory = validatorFactory;
            this.messageSource = messageSource;
        }

        @Override
        protected Class<DmpRootPersist> modelClass() {
            return DmpRootPersist.class;
        }

        @Override
        protected List<Specification> specifications(DmpRootPersist item) {

            return Arrays.asList(
                    this.spec()
                            .must(() -> !this.isNull(item.getDmp()))
                            .failOn(DmpRootPersist._dmp).failWith(this.messageSource.getMessage("Validation_Required", new Object[]{DmpRootPersist._dmp}, LocaleContextHolder.getLocale())),
                    this.refSpec()
                            .iff(() -> !this.isNull(item.getDmp()))
                            .on(DmpRootPersist._dmp)
                            .over(item.getDmp())
                            .using(() -> this.validatorFactory.validator(DmpValidator.class))
                    );


        }
    }
}
