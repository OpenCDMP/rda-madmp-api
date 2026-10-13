package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gr.cite.rda.common.enums.EthicalIssuesExistType;
import gr.cite.rda.common.enums.LanguageType;
import io.swagger.v3.oas.annotations.media.Schema;


import java.net.URI;
import java.util.Date;
import java.util.List;

@JsonPropertyOrder({ DmpModel._affiliation, DmpModel._alternateIdentifier, DmpModel._contact,  DmpModel._contributor, DmpModel._cost, DmpModel._created, DmpModel._dataset, DmpModel._description, DmpModel._dmpId, DmpModel._ethicalIssuesDescription,
            DmpModel._ethicalIssuesExist, DmpModel._ethicalIssuesReport, DmpModel._language, DmpModel._modified, DmpModel._project, DmpModel._relatedIdentifier, DmpModel._title})
@Schema(name = "The DMP Schema")
public class DmpModel {

    @JsonProperty("alternate_identifier")
    private List<AlternateIdentifierModel> alternateIdentifier;
    public static final String _alternateIdentifier = "alternateIdentifier";

    @JsonProperty("affiliation")
    private List<AffiliationModel> affiliation;
    public static final String _affiliation = "affiliation";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ContactModel contact;
    public static final String _contact = "contact";

    private List<ContributorModel> contributor;
    public static final String _contributor = "contributor";

    private List<CostModel> cost;
    public static final String _cost = "cost";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Date created;
    public static final String _created = "created";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DatasetModel> dataset;
    public static final String _dataset = "dataset";

    private String description;
    public static final String _description = "description";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("dmp_id")
    private DmpIdModel dmpId;
    public static final String _dmpId = "dmpId";

    @JsonProperty("ethical_issues_description")
    private String ethicalIssuesDescription;
    public static final String _ethicalIssuesDescription = "ethicalIssuesDescription";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("ethical_issues_exist")
    private EthicalIssuesExistType ethicalIssuesExist;
    public static final String _ethicalIssuesExist = "ethicalIssuesExist";

    @JsonProperty("ethical_issues_report")
    private String ethicalIssuesReport;
    public static final String _ethicalIssuesReport = "ethicalIssuesReport";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LanguageType language;
    public static final String _language = "language";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Date modified;
    public static final String _modified = "modified";

    private List<ProjectModel> project;
    public static final String _project = "project";

    @JsonProperty("related_identifier")
    private List<RelatedIdentifierModel> relatedIdentifier;
    public static final String _relatedIdentifier = "relatedIdentifier";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    public static final String _title = "title";

    public List<AffiliationModel> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<AffiliationModel> affiliation) {
        this.affiliation = affiliation;
    }

    public List<AlternateIdentifierModel> getAlternateIdentifier() {
        return alternateIdentifier;
    }

    public void setAlternateIdentifier(List<AlternateIdentifierModel> alternateIdentifier) {
        this.alternateIdentifier = alternateIdentifier;
    }

    public ContactModel getContact() {
        return contact;
    }

    public void setContact(ContactModel contact) {
        this.contact = contact;
    }

    public List<ContributorModel> getContributor() {
        return contributor;
    }

    public void setContributor(List<ContributorModel> contributor) {
        this.contributor = contributor;
    }

    public List<CostModel> getCost() {
        return cost;
    }

    public void setCost(List<CostModel> cost) {
        this.cost = cost;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<DatasetModel> getDataset() {
        return dataset;
    }

    public void setDataset(List<DatasetModel> dataset) {
        this.dataset = dataset;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DmpIdModel getDmpId() {
        return dmpId;
    }

    public void setDmpId(DmpIdModel dmpId) {
        this.dmpId = dmpId;
    }

    public String getEthicalIssuesDescription() {
        return ethicalIssuesDescription;
    }

    public void setEthicalIssuesDescription(String ethicalIssuesDescription) {
        this.ethicalIssuesDescription = ethicalIssuesDescription;
    }

    public EthicalIssuesExistType getEthicalIssuesExist() {
        return ethicalIssuesExist;
    }

    public void setEthicalIssuesExist(EthicalIssuesExistType ethicalIssuesExist) {
        this.ethicalIssuesExist = ethicalIssuesExist;
    }

    public String getEthicalIssuesReport() {
        return ethicalIssuesReport;
    }

    public void setEthicalIssuesReport(String ethicalIssuesReport) {
        this.ethicalIssuesReport = ethicalIssuesReport;
    }

    public LanguageType getLanguage() {
        return language;
    }

    public void setLanguage(LanguageType language) {
        this.language = language;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public List<ProjectModel> getProject() {
        return project;
    }

    public void setProject(List<ProjectModel> project) {
        this.project = project;
    }

    public List<RelatedIdentifierModel> getRelatedIdentifier() {
        return relatedIdentifier;
    }

    public void setRelatedIdentifier(List<RelatedIdentifierModel> relatedIdentifier) {
        this.relatedIdentifier = relatedIdentifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}