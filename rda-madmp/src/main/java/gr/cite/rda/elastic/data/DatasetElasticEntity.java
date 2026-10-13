package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.common.enums.LanguageType;
import gr.cite.rda.common.enums.PersonalDataType;
import gr.cite.rda.common.enums.SensitiveDataType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;


public class DatasetElasticEntity {

    @JsonProperty("alternate_identifier")
    @Field(value = DatasetElasticEntity._alternateIdentifier, type = FieldType.Nested)
    private List<AlternativeIdentifierElasticEntity> alternateIdentifier;
    public static final String _alternateIdentifier = "alternate_identifier";

    @Field(value = DatasetElasticEntity._creator, type = FieldType.Nested)
    private List<CreatorElasticEntity> creator;
    public static final String _creator = "creator";

    @JsonProperty("data_quality_assurance")
    @Field(value = DatasetElasticEntity._dataQualityAssurance, type = FieldType.Text)
    private List<String> dataQualityAssurance;
    public static final String _dataQualityAssurance = "dataQualityAssurance";

    @JsonProperty("dataset_id")
    @Field(value = DatasetElasticEntity._datasetId, type = FieldType.Object)
    private DatasetIdElasticEntity datasetId;
    public static final String _datasetId = "datasetId";

    @Field(value = DatasetElasticEntity._description, type = FieldType.Text)
    private String description;
    public static final String _description = "description";

    @Field(value = DatasetElasticEntity._distribution, type = FieldType.Nested)
    private List<DistributionElasticEntity> distribution;
    public static final String _distribution = "distribution";

    @JsonProperty("is_reused")
    @Field(value = DatasetElasticEntity._isReused, type = FieldType.Boolean)
    private Boolean isReused;
    public static final String _isReused = "isReused";

    @Field(value = DatasetElasticEntity._issued, type = FieldType.Date)
    private Date issued;
    public static final String _issued = "issued";

    @Field(value = DatasetElasticEntity._keyword, type = FieldType.Text)
    private List<String> keyword;
    public static final String _keyword = "keyword";

    @Field(value = DatasetElasticEntity._language, type = FieldType.Keyword)
    private LanguageType language;
    public static final String _language = "language";

    @Field(value = DatasetElasticEntity._metadata, type = FieldType.Nested)
    private List<MetadataElasticEntity> metadata;
    public static final String _metadata = "metadata";

    @JsonProperty("personal_data")
    @Field(value = DatasetElasticEntity._personalData, type = FieldType.Keyword)
    private PersonalDataType personalData;
    public static final String _personalData = "personalData";

    @JsonProperty("preservation_statement")
    @Field(value = DatasetElasticEntity._preservationStatement, type = FieldType.Text)
    private String preservationStatement;
    public static final String _preservationStatement = "preservationStatement";

    @Field(value = DatasetElasticEntity._rights, type = FieldType.Text)
    private String rights;
    public static final String _rights = "rights";

   @JsonProperty("related_identifier")
    @Field(value = DatasetElasticEntity._relatedIdentifier, type = FieldType.Nested)
    private List<RelatedIdentifierElasticEntity> relatedIdentifier;
    public static final String _relatedIdentifier = "related_identifier";

    @JsonProperty("security_and_privacy")
    @Field(value = DatasetElasticEntity._securityAndPrivacy, type = FieldType.Nested)
    private List<SecurityAndPrivacyElasticEntity> securityAndPrivacy;
    public static final String _securityAndPrivacy = "securityAndPrivacy";

    @JsonProperty("sensitive_data")
    @Field(value = DatasetElasticEntity._sensitiveData, type = FieldType.Keyword)
    private SensitiveDataType sensitiveData;
    public static final String _sensitiveData = "sensitiveData";

    @JsonProperty("technical_resource")
    @Field(value = DatasetElasticEntity._technicalResource, type = FieldType.Nested)
    private List<TechnicalResourceElasticEntity> technicalResource;
    public static final String _technicalResource = "technicalResource";

    @Field(value = DatasetElasticEntity._title, type = FieldType.Text)
    private String title;
    public static final String _title = "title";

    @Field(value = DatasetElasticEntity._type, type = FieldType.Text)
    private String type;
    public static final String _type = "type";

    public List<String> getDataQualityAssurance() {
        return dataQualityAssurance;
    }

    public void setDataQualityAssurance(List<String> dataQualityAssurance) {
        this.dataQualityAssurance = dataQualityAssurance;
    }

    public DatasetIdElasticEntity getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(DatasetIdElasticEntity datasetId) {
        this.datasetId = datasetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DistributionElasticEntity> getDistribution() {
        return distribution;
    }

    public void setDistribution(List<DistributionElasticEntity> distribution) {
        this.distribution = distribution;
    }

    public Boolean getIsReused() {
        return isReused;
    }

    public void setIsReused(Boolean reused) {
        isReused = reused;
    }

    public Date getIssued() {
        return issued;
    }

    public void setIssued(Date issued) {
        this.issued = issued;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    public LanguageType getLanguage() {
        return language;
    }

    public void setLanguage(LanguageType language) {
        this.language = language;
    }

    public List<MetadataElasticEntity> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<MetadataElasticEntity> metadata) {
        this.metadata = metadata;
    }

    public PersonalDataType getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalDataType personalData) {
        this.personalData = personalData;
    }

    public String getPreservationStatement() {
        return preservationStatement;
    }

    public void setPreservationStatement(String preservationStatement) {
        this.preservationStatement = preservationStatement;
    }

    public List<SecurityAndPrivacyElasticEntity> getSecurityAndPrivacy() {
        return securityAndPrivacy;
    }

    public void setSecurityAndPrivacy(List<SecurityAndPrivacyElasticEntity> securityAndPrivacy) {
        this.securityAndPrivacy = securityAndPrivacy;
    }

    public SensitiveDataType getSensitiveData() {
        return sensitiveData;
    }

    public void setSensitiveData(SensitiveDataType sensitiveData) {
        this.sensitiveData = sensitiveData;
    }

    public List<TechnicalResourceElasticEntity> getTechnicalResource() {
        return technicalResource;
    }

    public void setTechnicalResource(List<TechnicalResourceElasticEntity> technicalResource) {
        this.technicalResource = technicalResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AlternativeIdentifierElasticEntity> getAlternateIdentifier() {
        return alternateIdentifier;
    }

    public void setAlternateIdentifier(List<AlternativeIdentifierElasticEntity> alternateIdentifier) {
        this.alternateIdentifier = alternateIdentifier;
    }

    public List<CreatorElasticEntity> getCreator() {
        return creator;
    }

    public void setCreator(List<CreatorElasticEntity> creator) {
        this.creator = creator;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public List<RelatedIdentifierElasticEntity> getRelatedIdentifier() {
        return relatedIdentifier;
    }

    public void setRelatedIdentifier(List<RelatedIdentifierElasticEntity> relatedIdentifier) {
        this.relatedIdentifier = relatedIdentifier;
    }
}