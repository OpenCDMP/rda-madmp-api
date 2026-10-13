package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gr.cite.rda.common.enums.LanguageType;
import gr.cite.rda.common.enums.PersonalDataType;
import gr.cite.rda.common.enums.SensitiveDataType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@JsonPropertyOrder({ DatasetModel._alternateIdentifier, DatasetModel._creator, DatasetModel._dataQualityAssurance, DatasetModel._datasetId, DatasetModel._description, DatasetModel._distribution, DatasetModel._isReused, DatasetModel._isReused, DatasetModel._issued, DatasetModel._keyword,
        DatasetModel._language, DatasetModel._metadata, DatasetModel._personalData, DatasetModel._preservationStatement, DatasetModel._rights, DatasetModel._relatedIdentifier, DatasetModel._securityAndPrivacy, DatasetModel._sensitiveData, DatasetModel._technicalResource, DatasetModel._title, DatasetModel._type})
@Schema(name = "The Dataset Items Schema")
public class DatasetModel {

    @JsonProperty("alternate_identifier")
    private List<AlternateIdentifierModel> alternateIdentifier;
    public static final String _alternateIdentifier = "alternateIdentifier";

    @JsonProperty("creator")
    private List<CreatorModel> creator;
    public static final String _creator = "creator";

    @JsonProperty("data_quality_assurance")
    private List<String> dataQualityAssurance;
    public static final String _dataQualityAssurance = "dataQualityAssurance";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("dataset_id")
    private DatasetIdModel datasetId;
    public static final String _datasetId = "datasetId";

    private String description;
    public static final String _description = "description";

    private List<DistributionModel> distribution;
    public static final String _distribution = "distribution";

    @JsonProperty("is_reused")
    private Boolean isReused;
    public static final String _isReused = "isReused";

    @Schema(
            type = "string",
            format = "date",
            example = "2019-06-30"
    )
    private Date issued;
    public static final String _issued = "issued";

    private List<String> keyword;
    public static final String _keyword = "keyword";

    private LanguageType language;
    public static final String _language = "language";

    private List<MetadataModel> metadata;
    public static final String _metadata = "metadata";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("personal_data")
    private PersonalDataType personalData;
    public static final String _personalData = "personalData";

    @JsonProperty("preservation_statement")
    private String preservationStatement;
    public static final String _preservationStatement = "preservationStatement";

    private String rights;
    public static final String _rights = "rights";

    @JsonProperty("related_identifier")
    private List<RelatedIdentifierModel> relatedIdentifier;
    public static final String _relatedIdentifier = "relatedIdentifier";

    @JsonProperty("security_and_privacy")
    private List<SecurityAndPrivacyModel> securityAndPrivacy;
    public static final String _securityAndPrivacy = "securityAndPrivacy";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("sensitive_data")
    private SensitiveDataType sensitiveData;
    public static final String _sensitiveData = "sensitiveData";

    @JsonProperty("technical_resource")
    private List<TechnicalResourceModel> technicalResource;
    public static final String _technicalResource = "technicalResource";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    public static final String _title = "title";

    private String type;
    public static final String _type = "type";

    public List<String> getDataQualityAssurance() {
        return dataQualityAssurance;
    }

    public void setDataQualityAssurance(List<String> dataQualityAssurance) {
        this.dataQualityAssurance = dataQualityAssurance;
    }

    public DatasetIdModel getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(DatasetIdModel datasetId) {
        this.datasetId = datasetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DistributionModel> getDistribution() {
        return distribution;
    }

    public void setDistribution(List<DistributionModel> distribution) {
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

    public List<MetadataModel> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<MetadataModel> metadata) {
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

    public List<SecurityAndPrivacyModel> getSecurityAndPrivacy() {
        return securityAndPrivacy;
    }

    public void setSecurityAndPrivacy(List<SecurityAndPrivacyModel> securityAndPrivacy) {
        this.securityAndPrivacy = securityAndPrivacy;
    }

    public SensitiveDataType getSensitiveData() {
        return sensitiveData;
    }

    public void setSensitiveData(SensitiveDataType sensitiveData) {
        this.sensitiveData = sensitiveData;
    }

    public List<TechnicalResourceModel> getTechnicalResource() {
        return technicalResource;
    }

    public void setTechnicalResource(List<TechnicalResourceModel> technicalResource) {
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

    public List<AlternateIdentifierModel> getAlternateIdentifier() {
        return alternateIdentifier;
    }

    public void setAlternateIdentifier(List<AlternateIdentifierModel> alternateIdentifier) {
        this.alternateIdentifier = alternateIdentifier;
    }

    public List<CreatorModel> getCreator() {
        return creator;
    }

    public void setCreator(List<CreatorModel> creator) {
        this.creator = creator;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public List<RelatedIdentifierModel> getRelatedIdentifier() {
        return relatedIdentifier;
    }

    public void setRelatedIdentifier(List<RelatedIdentifierModel> relatedIdentifier) {
        this.relatedIdentifier = relatedIdentifier;
    }
}