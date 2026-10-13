package gr.cite.rda.model.persist;

import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TenantTouchedIntegrationEventPersist {

    private UUID id;
    public static final String _id = "id";

    private String code;
    public static final String _code = "code";
    public static final int _codeLength = 50;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Component(TenantTouchedIntegrationEventPersist.TenantTouchedIntegrationEventPersistValidator.ValidatorName)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public static class TenantTouchedIntegrationEventPersistValidator extends BaseValidator<TenantTouchedIntegrationEventPersist> {

        public static final String ValidatorName = "TenantTouchedIntegrationEventPersistValidator";

        private final MessageSource messageSource;

        protected TenantTouchedIntegrationEventPersistValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
            super(conventionService, errors);
            this.messageSource = messageSource;
        }

        @Override
        protected Class<TenantTouchedIntegrationEventPersist> modelClass() {
            return TenantTouchedIntegrationEventPersist.class;
        }

        @Override
        protected List<Specification> specifications(TenantTouchedIntegrationEventPersist item) {
            return Arrays.asList(
                    this.spec()
                            .iff(() -> !this.isNull(item.getId()))
                            .must(() -> this.isValidGuid(item.getId()))
                            .failOn(TenantTouchedIntegrationEventPersist._id).failWith(messageSource.getMessage("Validation_Required", new Object[]{TenantTouchedIntegrationEventPersist._id}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getCode()))
                            .failOn(TenantTouchedIntegrationEventPersist._code).failWith(messageSource.getMessage("Validation_Required", new Object[]{TenantTouchedIntegrationEventPersist._code}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .iff(() -> !this.isEmpty(item.getCode()))
                            .must(() -> this.lessEqualLength(item.getCode(), TenantTouchedIntegrationEventPersist._codeLength))
                            .failOn(TenantTouchedIntegrationEventPersist._code).failWith(messageSource.getMessage("Validation_MaxLength", new Object[]{TenantTouchedIntegrationEventPersist._code}, LocaleContextHolder.getLocale()))
            );
        }
    }

}
