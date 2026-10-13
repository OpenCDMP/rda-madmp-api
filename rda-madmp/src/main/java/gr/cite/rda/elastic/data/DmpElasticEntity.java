package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.common.enums.EthicalIssuesExistType;
import gr.cite.rda.common.enums.LanguageType;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;
import java.util.List;

public class DmpElasticEntity {

	@Field(value = CreatorElasticEntity._affiliation, type = FieldType.Nested)
	private List<AffiliationElasticEntity> affiliation;
	public static final String _affiliation = "affiliation";

	@JsonProperty("alternate_identifier")
	@Field(value = DmpElasticEntity._alternateIdentifier, type = FieldType.Nested)
	private List<AlternativeIdentifierElasticEntity> alternateIdentifier;
	public static final String _alternateIdentifier = "alternate_identifier";

	@Field(value = DmpElasticEntity._contact, type = FieldType.Object)
	private ContactElasticEntity contact;
	public static final String _contact = "contact";

	@Field(value = DmpElasticEntity._contributor, type = FieldType.Nested)
	private List<ContributorElasticEntity> contributor;
	public static final String _contributor = "contributor";

	@Field(value = DmpElasticEntity._cost, type = FieldType.Nested)
	private List<CostElasticEntity> cost;
	public static final String _cost = "cost";

	@Field(value = DmpElasticEntity._created, type = FieldType.Date)
	private Date created;
	public static final String _created = "created";

	@Field(value = DmpElasticEntity._dataset, type = FieldType.Nested)
	private List<DatasetElasticEntity> dataset;
	public static final String _dataset = "dataset";

	@Field(value = DmpElasticEntity._description, type = FieldType.Text)
	private String description;
	public static final String _description = "description";

	@JsonProperty("dmp_id")
	@Field(value = DmpElasticEntity._dmpId, type = FieldType.Object)
	private DmpIdElasticEntity dmpId;
	public static final String _dmpId = "dmpId";

	@JsonProperty("ethical_issues_description")
	@Field(value = DmpElasticEntity._ethicalIssuesDescription, type = FieldType.Text)
	private String ethicalIssuesDescription;
	public static final String _ethicalIssuesDescription = "ethicalIssuesDescription";

	@JsonProperty("ethical_issues_exist")
	@Field(value = DmpElasticEntity._ethicalIssuesExist, type = FieldType.Keyword)
	private EthicalIssuesExistType ethicalIssuesExist;
	public static final String _ethicalIssuesExist = "ethicalIssuesExist";

	@JsonProperty("ethical_issues_report")
	@Field(value = DmpElasticEntity._ethicalIssuesReport, type = FieldType.Text)
	private String ethicalIssuesReport;
	public static final String _ethicalIssuesReport = "ethicalIssuesReport";

	@Field(value = DmpElasticEntity._language, type = FieldType.Keyword)
	private LanguageType language;
	public static final String _language = "language";

	@Field(value = DmpElasticEntity._modified, type = FieldType.Date)
	private Date modified;
	public static final String _modified = "modified";

	@Field(value = DmpElasticEntity._project, type = FieldType.Nested)
	private List<ProjectElasticEntity> project;
	public static final String _project = "project";

	@JsonProperty("related_identifier")
	@Field(value = DmpElasticEntity._relatedIdentifier, type = FieldType.Nested)
	private List<RelatedIdentifierElasticEntity> relatedIdentifier;
	public static final String _relatedIdentifier = "related_identifier";

	@Field(value = DmpElasticEntity._title, type = FieldType.Text)
	private String title;
	public static final String _title = "title";

	public ContactElasticEntity getContact() {
		return contact;
	}

	public void setContact(ContactElasticEntity contact) {
		this.contact = contact;
	}

	public List<AffiliationElasticEntity> getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(List<AffiliationElasticEntity> affiliation) {
		this.affiliation = affiliation;
	}

	public List<ContributorElasticEntity> getContributor() {
		return contributor;
	}

	public void setContributor(List<ContributorElasticEntity> contributor) {
		this.contributor = contributor;
	}

	public List<CostElasticEntity> getCost() {
		return cost;
	}

	public void setCost(List<CostElasticEntity> cost) {
		this.cost = cost;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public List<DatasetElasticEntity> getDataset() {
		return dataset;
	}

	public void setDataset(List<DatasetElasticEntity> dataset) {
		this.dataset = dataset;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DmpIdElasticEntity getDmpId() {
		return dmpId;
	}

	public void setDmpId(DmpIdElasticEntity dmpId) {
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

	public List<ProjectElasticEntity> getProject() {
		return project;
	}

	public void setProject(List<ProjectElasticEntity> project) {
		this.project = project;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<AlternativeIdentifierElasticEntity> getAlternateIdentifier() {
		return alternateIdentifier;
	}

	public void setAlternateIdentifier(List<AlternativeIdentifierElasticEntity> alternateIdentifier) {
		this.alternateIdentifier = alternateIdentifier;
	}

	public List<RelatedIdentifierElasticEntity> getRelatedIdentifier() {
		return relatedIdentifier;
	}

	public void setRelatedIdentifier(List<RelatedIdentifierElasticEntity> relatedIdentifier) {
		this.relatedIdentifier = relatedIdentifier;
	}
}
