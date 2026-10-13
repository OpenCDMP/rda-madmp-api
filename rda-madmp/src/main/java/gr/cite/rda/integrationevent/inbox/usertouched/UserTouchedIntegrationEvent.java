package gr.cite.rda.integrationevent.inbox.usertouched;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.common.enums.ContactInfoType;
import gr.cite.rda.common.validation.BaseValidator;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.errorcode.ErrorThesaurusProperties;
import gr.cite.rda.integrationevent.TrackedEvent;
import gr.cite.tools.validation.ValidatorFactory;
import gr.cite.tools.validation.specification.Specification;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UserTouchedIntegrationEvent extends TrackedEvent {

    @JsonProperty(value = "Id")
    private UUID id;

    public static final String _id = "id";

    @JsonProperty(value = "Name")
    private String name;

    public static final String _name = "name";

    public static final int _nameLength = 200;

    @JsonProperty(value = "Profile")
    private UserProfile profile;

    @JsonProperty(value = "UserContactInfos")
    private List<UserContactInfo> userContactInfo;

    public static final String _userContactInfo = "userContactInfo";
    @JsonProperty(value = "TenantUsers")
    private List<TenantUser> tenantUsers;

    public static final String _tenantUsers = "tenantUsers";
    @JsonProperty(value = "Credentials")
    private List<UserCredential> credentials;

    public static final String _credentials = "credentials";

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TenantUser> getTenantUsers() {
        return tenantUsers;
    }

    public void setTenantUsers(List<TenantUser> tenantUsers) {
        this.tenantUsers = tenantUsers;
    }

    public List<UserCredential> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<UserCredential> credentials) {
        this.credentials = credentials;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public List<UserContactInfo> getUserContactInfo() {
        return userContactInfo;
    }

    public void setUserContactInfo(List<UserContactInfo> userContactInfo) {
        this.userContactInfo = userContactInfo;
    }

    public static class UserProfile {

        @JsonProperty(value = "Timezone")
        private String timezone;

        @JsonProperty(value = "Culture")
        private String culture;

        @JsonProperty(value = "Language")
        private String language;

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getCulture() {
            return culture;
        }

        public void setCulture(String culture) {
            this.culture = culture;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    public static class UserContactInfo {

        @JsonProperty(value = "Type")
        private ContactInfoType type;

        public static final String _type = "type";

        @JsonProperty(value = "Value")
        private String value;

        public static final String _value = "value";

        @JsonProperty(value = "Ordinal")
        private Integer ordinal;

        public static final String _ordinal = "ordinal";

        public ContactInfoType getType() {
            return type;
        }

        public void setType(ContactInfoType type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Integer getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(Integer ordinal) {
            this.ordinal = ordinal;
        }

        @Component(UserTouchedIntegrationUserContactInfoEventValidator.ValidatorName)
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        public static class UserTouchedIntegrationUserContactInfoEventValidator extends BaseValidator<UserContactInfo> {

            public static final String ValidatorName = "UserTouchedIntegrationUserContactInfoEventValidator";

            private final MessageSource messageSource;

            protected UserTouchedIntegrationUserContactInfoEventValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
                super(conventionService, errors);
                this.messageSource = messageSource;
            }

            @Override
            protected Class<UserContactInfo> modelClass() {
                return UserContactInfo.class;
            }

            @Override
            protected List<Specification> specifications(UserContactInfo item) {
                return Arrays.asList(
                        this.spec()
                                .must(() -> !this.isEmpty(item.getValue()))
                                .failOn(UserContactInfo._value).failWith(messageSource.getMessage("Validation_Required", new Object[]{UserContactInfo._value}, LocaleContextHolder.getLocale())),
                        this.spec()
                                .must(() -> !this.isNull(item.getType()))
                                .failOn(UserContactInfo._type).failWith(messageSource.getMessage("Validation_Required", new Object[]{UserContactInfo._type}, LocaleContextHolder.getLocale())),
                        this.spec()
                                .must(() -> !this.isNull(item.getOrdinal()))
                                .failOn(UserContactInfo._ordinal).failWith(messageSource.getMessage("Validation_Required", new Object[]{UserContactInfo._ordinal}, LocaleContextHolder.getLocale()))
                        );
            }
        }
    }

    public static class UserCredential {

        @JsonProperty(value = "SubjectId")
        private String subjectId;

        public static final String _subjectId = "subjectId";

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        @Component(UserTouchedIntegrationUserCredentialEventValidator.ValidatorName)
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        public static class UserTouchedIntegrationUserCredentialEventValidator extends BaseValidator<UserCredential> {

            public static final String ValidatorName = "UserTouchedIntegrationUserCredentialEventValidator";

            private final MessageSource messageSource;

            protected UserTouchedIntegrationUserCredentialEventValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
                super(conventionService, errors);
                this.messageSource = messageSource;
            }

            @Override
            protected Class<UserCredential> modelClass() {
                return UserCredential.class;
            }

            @Override
            protected List<Specification> specifications(UserCredential item) {
                return Collections.singletonList(
		                this.spec()
				                .must(() -> !this.isEmpty(item.getSubjectId()))
				                .failOn(UserCredential._subjectId).failWith(messageSource.getMessage("Validation_Required", new Object[]{UserCredential._subjectId}, LocaleContextHolder.getLocale()))
                );
            }
        }
    }

    public static class TenantUser {

        @JsonProperty(value = "Tenant")
        private UUID tenant;
        public static final String _tenant = "tenant";

        public UUID getTenant() {
            return tenant;
        }

        public void setTenant(UUID tenant) {
            this.tenant = tenant;
        }

        @Component(UserTouchedIntegrationTenantUserEventValidator.ValidatorName)
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        public static class UserTouchedIntegrationTenantUserEventValidator extends BaseValidator<TenantUser> {

            public static final String ValidatorName = "UserTouchedIntegrationTenantUserEventValidator";

            private final MessageSource messageSource;

            protected UserTouchedIntegrationTenantUserEventValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource) {
                super(conventionService, errors);
                this.messageSource = messageSource;
            }

            @Override
            protected Class<TenantUser> modelClass() {
                return TenantUser.class;
            }

            @Override
            protected List<Specification> specifications(TenantUser item) {
                return Collections.singletonList(
		                this.spec()
				                .must(() -> !this.isNull(item.getTenant()))
				                .failOn(TenantUser._tenant).failWith(messageSource.getMessage("Validation_Required", new Object[]{TenantUser._tenant}, LocaleContextHolder.getLocale()))
                );
            }
        }
    }

    @Component(UserTouchedIntegrationEventValidator.ValidatorName)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public static class UserTouchedIntegrationEventValidator extends BaseValidator<UserTouchedIntegrationEvent> {

        public static final String ValidatorName = "UserTouchedIntegrationEventValidator";

        private final MessageSource messageSource;

        private final ValidatorFactory validatorFactory;

        protected UserTouchedIntegrationEventValidator(ConventionService conventionService, ErrorThesaurusProperties errors, MessageSource messageSource, ValidatorFactory validatorFactory) {
            super(conventionService, errors);
            this.messageSource = messageSource;
	        this.validatorFactory = validatorFactory;
        }

        @Override
        protected Class<UserTouchedIntegrationEvent> modelClass() {
            return UserTouchedIntegrationEvent.class;
        }

        @Override
        protected List<Specification> specifications(UserTouchedIntegrationEvent item) {
            return Arrays.asList(
                    this.spec()
                            .iff(() -> !this.isNull(item.getId()))
                            .must(() -> this.isValidGuid(item.getId()))
                            .failOn(UserTouchedIntegrationEvent._id).failWith(messageSource.getMessage("Validation_Required", new Object[]{UserTouchedIntegrationEvent._id}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .must(() -> !this.isEmpty(item.getName()))
                            .failOn(UserTouchedIntegrationEvent._name).failWith(messageSource.getMessage("Validation_Required", new Object[]{UserTouchedIntegrationEvent._name}, LocaleContextHolder.getLocale())),
                    this.spec()
                            .iff(() -> !this.isEmpty(item.getName()))
                            .must(() -> this.lessEqualLength(item.getName(), UserTouchedIntegrationEvent._nameLength))
                            .failOn(UserTouchedIntegrationEvent._name).failWith(messageSource.getMessage("Validation_MaxLength", new Object[]{UserTouchedIntegrationEvent._name}, LocaleContextHolder.getLocale())),
                    this.navSpec()
                            .iff(() -> !this.isListNullOrEmpty(item.getUserContactInfo()))
                            .on(UserTouchedIntegrationEvent._userContactInfo)
                            .over(item.getUserContactInfo())
                            .using((i) -> this.validatorFactory.validator(UserContactInfo.UserTouchedIntegrationUserContactInfoEventValidator.class))
                   );
        }
    }
}
