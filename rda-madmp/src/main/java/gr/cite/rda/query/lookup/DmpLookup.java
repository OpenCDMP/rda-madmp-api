package gr.cite.rda.query.lookup;

import gr.cite.rda.common.enums.ActionType;
import gr.cite.rda.common.enums.DataAccessType;
import gr.cite.rda.common.enums.EthicalIssuesExistType;
import gr.cite.rda.common.enums.FundingStatusType;
import gr.cite.rda.common.enums.LanguageType;
import gr.cite.rda.common.enums.PersonalDataType;
import gr.cite.rda.common.enums.SensitiveDataType;
import gr.cite.rda.common.exception.InvalidQueryStringException;
import gr.cite.rda.elastic.data.DmpElasticEntity;
import gr.cite.rda.elastic.data.DmpRootElasticEntity;
import gr.cite.rda.elastic.query.*;
import gr.cite.tools.data.query.Ordering;
import gr.cite.tools.data.query.Paging;
import gr.cite.tools.data.query.QueryFactory;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class DmpLookup {

    @Parameter(
            description = "Number of items to skip from the start.",
            schema = @Schema(
                    type = "integer",
                    defaultValue = "0",
                    minimum = "0"
            )
    )
    private Integer offset = 0;

    @Parameter(
            description = "Number of items to fetch.",
            schema = @Schema(
                    type = "integer",
                    defaultValue = "20",
                    minimum = "1",
                    maximum = "100"
            )
    )
    private Integer count = 20;

    @Parameter(
            description = "A list of fields to sort by with the sort order.",
            array = @ArraySchema(
                    arraySchema = @Schema(
                            type = "array",
                            defaultValue = "[\"created,desc\"]"
                    ),
                    schema = @Schema(
                            type = "string",
                            allowableValues = {
                                    "title,asc","title,desc",
                                    "created,asc","created,desc",
                                    "modified,asc","modified,desc",
                                    "language,asc","language,desc",
                                    "embargo,asc","embargo,desc",
                                    "keyword,asc","keyword,desc"
                            },
                            defaultValue = "created,desc"
                    )
            )
    )
    private List<String> sort = List.of(DmpElasticEntity._created + ",desc");

    private String created_before;

    private String created_after;

    private String modified_before;

    private String modified_after;

    @Parameter(
            array = @ArraySchema(schema = @Schema(implementation = LanguageType.class))
    )
    private List<String> languages;

    private List<String> contact_ids;

    private List<String> contributor_ids;

    private List<String> dataset_ids;

    private List<String> metadata_standard_ids;

    private List<String> dmp_ids;

    private List<String> funder_ids;

    private List<String> grant_ids;

    @Parameter(description = "Look up DMPs by alternate identifier values (dmp.alternate_identifier[].identifier).")
    private List<String> dmp_alternate_identifier;

    @Parameter(
            description = "Filter by whether any dataset in the DMP contains personal data (dmp.dataset[].personal_data). Allowed values: yes, no, unknown.",
            array = @ArraySchema(schema = @Schema(type = "string", allowableValues = {"yes", "no", "unknown"}))
    )
    private List<String> dataset_personal_data;

    @Parameter(
            description = "Filter by whether any dataset in the DMP contains sensitive data (dmp.dataset[].sensitive_data). Allowed values: yes, no, unknown.",
            array = @ArraySchema(schema = @Schema(type = "string", allowableValues = {"yes", "no", "unknown"}))
    )
    private List<String> dataset_sensitive_data;

    @Parameter(description = "Filter DMPs to those containing at least one distribution whose format matches any of the specified MIME types (dmp.dataset[].distribution[].format[]).")
    private List<String> distribution_formats;

    @Parameter(
            description = "Filter DMPs to those containing at least one distribution with the specified data access mode (dmp.dataset[].distribution[].data_access). Allowed values: open, restricted, closed; shared is deprecated and treated as equivalent to restricted.",
            array = @ArraySchema(schema = @Schema(type = "string", allowableValues = {"open", "restricted", "closed", "shared"}))
    )
    private List<String> distribution_data_access;

    @Parameter(description = "Filter by the persistent identifier of a host of any dataset's distribution(s) (dmp.dataset[].distribution[].host.host_id.identifier).")
    private List<String> host_ids;

    @Parameter(description = "Filter DMPs to those where at least one distribution host has a name matching a string in this array (dmp.dataset[].distribution[].host.title).")
    private List<String> host_titles;

    @Parameter(description = "Filter DMPs to those where at least one distribution carries a license matching any of the specified license reference URLs (dmp.dataset[].distribution[].license[].license_ref).")
    private List<String> license_refs;

    @Parameter(
            description = "Filter by the funding status of any project funding entry in the DMP (dmp.project[].funding[].funding_status). Allowed values: planned, applied, granted, rejected.",
            array = @ArraySchema(schema = @Schema(type = "string", allowableValues = {"planned", "applied", "granted", "rejected"}))
    )
    private List<String> funding_status;

    @Parameter(description = "A free-text search string matched case-insensitively against dmp.title, dmp.description, dmp.dataset[].title and dmp.dataset[].description.")
    private List<String> query;

    @Parameter(
            description = "Filter by whether the DMP declares ethical issues. Allowed values: yes, no, unknown.",
            array = @ArraySchema(schema = @Schema(type = "string", allowableValues = {"yes", "no", "unknown"}))
    )
    private List<String> ethical_issues_exist;

    @Schema(
            type = "string",
            format = "date"
    )
    private String embargo_before;

    @Schema(
            type = "string",
            format = "date"
    )
    private String embargo_after;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            return;
        }

        List<String> fixedSort = new ArrayList<>();
        int i = 0;
        while (i < sort.size()) {
            String current = sort.get(i);

            if (i + 1 < sort.size() && isSortField(current) && isDirection(sort.get(i + 1))) {
                fixedSort.add(current + "," + sort.get(i + 1));
                i += 2;
            } else {
                fixedSort.add(current);
                i++;
            }
        }

        this.sort = fixedSort;
    }

    public String getCreated_before() {
        return created_before;
    }

    public void setCreated_before(String created_before) {
        this.created_before = created_before;
    }

    public String getCreated_after() {
        return created_after;
    }

    public void setCreated_after(String created_after) {
        this.created_after = created_after;
    }

    public String getModified_before() {
        return modified_before;
    }

    public void setModified_before(String modified_before) {
        this.modified_before = modified_before;
    }

    public String getModified_after() {
        return modified_after;
    }

    public void setModified_after(String modified_after) {
        this.modified_after = modified_after;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getContact_ids() {
        return contact_ids;
    }

    public void setContact_ids(List<String> contact_ids) {
        this.contact_ids = contact_ids;
    }

    public List<String> getContributor_ids() {
        return contributor_ids;
    }

    public void setContributor_ids(List<String> contributor_ids) {
        this.contributor_ids = contributor_ids;
    }

    public List<String> getDataset_ids() {
        return dataset_ids;
    }

    public void setDataset_ids(List<String> dataset_ids) {
        this.dataset_ids = dataset_ids;
    }

    public List<String> getMetadata_standard_ids() {
        return metadata_standard_ids;
    }

    public void setMetadata_standard_ids(List<String> metadata_standard_ids) {
        this.metadata_standard_ids = metadata_standard_ids;
    }

    public List<String> getDmp_ids() {
        return dmp_ids;
    }

    public void setDmp_ids(List<String> dmp_ids) {
        this.dmp_ids = dmp_ids;
    }

    public List<String> getFunder_ids() {
        return funder_ids;
    }

    public void setFunder_ids(List<String> funder_ids) {
        this.funder_ids = funder_ids;
    }

    public List<String> getGrant_ids() {
        return grant_ids;
    }

    public void setGrant_ids(List<String> grant_ids) {
        this.grant_ids = grant_ids;
    }

    public List<String> getDmp_alternate_identifier() {
        return dmp_alternate_identifier;
    }

    public void setDmp_alternate_identifier(List<String> dmp_alternate_identifier) {
        this.dmp_alternate_identifier = dmp_alternate_identifier;
    }

    public List<String> getDataset_personal_data() {
        return dataset_personal_data;
    }

    public void setDataset_personal_data(List<String> dataset_personal_data) {
        this.dataset_personal_data = dataset_personal_data;
    }

    public List<String> getDataset_sensitive_data() {
        return dataset_sensitive_data;
    }

    public void setDataset_sensitive_data(List<String> dataset_sensitive_data) {
        this.dataset_sensitive_data = dataset_sensitive_data;
    }

    public List<String> getDistribution_formats() {
        return distribution_formats;
    }

    public void setDistribution_formats(List<String> distribution_formats) {
        this.distribution_formats = distribution_formats;
    }

    public List<String> getDistribution_data_access() {
        return distribution_data_access;
    }

    public void setDistribution_data_access(List<String> distribution_data_access) {
        this.distribution_data_access = distribution_data_access;
    }

    public List<String> getHost_ids() {
        return host_ids;
    }

    public void setHost_ids(List<String> host_ids) {
        this.host_ids = host_ids;
    }

    public List<String> getHost_titles() {
        return host_titles;
    }

    public void setHost_titles(List<String> host_titles) {
        this.host_titles = host_titles;
    }

    public List<String> getLicense_refs() {
        return license_refs;
    }

    public void setLicense_refs(List<String> license_refs) {
        this.license_refs = license_refs;
    }

    public List<String> getFunding_status() {
        return funding_status;
    }

    public void setFunding_status(List<String> funding_status) {
        this.funding_status = funding_status;
    }

    public List<String> getQuery() {
        return query;
    }

    public void setQuery(List<String> query) {
        this.query = query;
    }

    public List<String> getEthical_issues_exist() {
        return ethical_issues_exist;
    }

    public void setEthical_issues_exist(List<String> ethical_issues_exist) {
        this.ethical_issues_exist = ethical_issues_exist;
    }

    public String getEmbargo_before() {
        return embargo_before;
    }

    public void setEmbargo_before(String embargo_before) {
        this.embargo_before = embargo_before;
    }

    public String getEmbargo_after() {
        return embargo_after;
    }

    public void setEmbargo_after(String embargo_after) {
        this.embargo_after = embargo_after;
    }

    public void validate() throws InvalidQueryStringException {
        if (this.offset != null && this.offset < 0) throw new InvalidQueryStringException("For 'offset': value must be greater than or equal to 0");
        if (this.count != null && (this.count < 1 || this.count > 100)) throw new InvalidQueryStringException("For 'count': value must be between 1 and 100");

        if (this.sort != null) {
            for (String sortField : this.sort) {
                if (sortField == null || !sortField.contains(",")) throw new InvalidQueryStringException("For 'sort': value '" + sortField + "' must be in the form <field>,<direction>");
                String[] parts = sortField.split(",");
                if (parts.length != 2 || !isSortField(parts[0].trim()) || !isDirection(parts[1].trim())) throw new InvalidQueryStringException("For 'sort': unsupported sort value '" + sortField + "'");
            }
        }

        this.validateDate("created_before", this.created_before);
        this.validateDate("created_after", this.created_after);
        this.validateDate("modified_before", this.modified_before);
        this.validateDate("modified_after", this.modified_after);
        this.validateDate("embargo_before", this.embargo_before);
        this.validateDate("embargo_after", this.embargo_after);

        if (this.languages != null) {
            for (String language : this.languages) {
                if (LanguageType.of(language) == null) throw new InvalidQueryStringException("For 'languages': value '" + language + "' is not a valid ISO 639-3 language code");
            }
        }
        this.validateBooleanish("ethical_issues_exist", this.ethical_issues_exist);
        this.validateBooleanish("dataset_personal_data", this.dataset_personal_data);
        this.validateBooleanish("dataset_sensitive_data", this.dataset_sensitive_data);
        if (this.distribution_data_access != null) {
            for (String value : this.distribution_data_access) {
                if (DataAccessType.of(value) == null) throw new InvalidQueryStringException("For 'distribution_data_access': value '" + value + "' is not one of: open, restricted, closed, shared");
            }
        }
        if (this.funding_status != null) {
            for (String value : this.funding_status) {
                if (FundingStatusType.of(value) == null) throw new InvalidQueryStringException("For 'funding_status': value '" + value + "' is not one of: planned, applied, granted, rejected");
            }
        }
    }

    private void validateDate(String name, String value) throws InvalidQueryStringException {
        if (value == null || value.isBlank()) return;
        try {
            this.convertStringToInstant(value);
        } catch (DateTimeParseException e) {
            throw new InvalidQueryStringException("For '" + name + "': value '" + value + "' is not a valid ISO 8601 date-time");
        }
    }

    private void validateBooleanish(String name, List<String> values) throws InvalidQueryStringException {
        if (values == null) return;
        for (String value : values) {
            if (EthicalIssuesExistType.of(value) == null) throw new InvalidQueryStringException("For '" + name + "': value '" + value + "' is not one of: yes, no, unknown");
        }
    }

    public DmpRootElasticQuery enrich(QueryFactory queryFactory, boolean useActionType) {
        this.validate();

        DmpRootElasticQuery query = queryFactory.query(DmpRootElasticQuery.class);

        if (useActionType) query.actionTypes(ActionType.None);

        boolean hasFilters = false;

        DmpElasticQuery dmpElasticQuery = queryFactory.query(DmpElasticQuery.class);
        if (this.query != null && !this.query.isEmpty()) {
            dmpElasticQuery.like(this.query.stream().filter(Objects::nonNull).map(value -> StringUtils.strip(value, "%")).toList());
            hasFilters = true;
        }
        if (this.created_after != null) {
            Instant instant = this.convertStringToInstant(this.created_after);
            if (instant != null) {
                dmpElasticQuery.createdAfter(instant);
                hasFilters = true;
            }
        }
        if (this.created_before != null) {
            Instant instant = this.convertStringToInstant(this.created_before);
            if (instant != null) {
                dmpElasticQuery.createdBefore(instant);
                hasFilters = true;
            }
        }
        if (this.modified_after != null) {
            Instant instant = this.convertStringToInstant(this.modified_after);
            if (instant != null) {
                dmpElasticQuery.modifiedAfter(instant);
                hasFilters = true;
            }
        }
        if (this.modified_before != null) {
            Instant instant = this.convertStringToInstant(this.modified_before);
            if (instant != null) {
                dmpElasticQuery.modifiedBefore(instant);
                hasFilters = true;
            }
        }

        if (this.ethical_issues_exist != null && !this.ethical_issues_exist.isEmpty()) {
            dmpElasticQuery.ethicalIssuesExistTypes(this.ethical_issues_exist.stream().map(EthicalIssuesExistType::of).filter(Objects::nonNull).toList());
            hasFilters = true;
        }

        if (this.languages != null) {

            List<LanguageType> validLanguages = this.languages.stream().map(LanguageType::of).filter(Objects::nonNull).toList();

            if(!validLanguages.isEmpty()){
                dmpElasticQuery.languageTypes(validLanguages);
                hasFilters = true;
            }

        }

        if(this.contact_ids != null){
            ContactIdElasticQuery contactIdElasticQuery = queryFactory.query(ContactIdElasticQuery.class);
            contactIdElasticQuery.identifiers(this.contact_ids);

            ContactElasticQuery contributorElasticQuery = queryFactory.query(ContactElasticQuery.class);
            contributorElasticQuery.contactIdSubQuery(contactIdElasticQuery);

            dmpElasticQuery.contactSubQuery(contributorElasticQuery);
            hasFilters = true;
        }

        if(this.contributor_ids != null){
            ContributorIdElasticQuery contributorIdElasticQuery = queryFactory.query(ContributorIdElasticQuery.class);
            contributorIdElasticQuery.identifiers(this.contributor_ids);

            ContributorElasticQuery contributorElasticQuery = queryFactory.query(ContributorElasticQuery.class);
            contributorElasticQuery.contributorIdElasticQuery(contributorIdElasticQuery);

            dmpElasticQuery.contributorSubQuery(contributorElasticQuery);
            hasFilters = true;
        }

        if(this.dmp_alternate_identifier != null){
            AlternativeIdentifierElasticQuery alternativeIdentifierElasticQuery = queryFactory.query(AlternativeIdentifierElasticQuery.class);
            alternativeIdentifierElasticQuery.identifiers(this.dmp_alternate_identifier);

            dmpElasticQuery.alternativeIdentifierSubQuery(alternativeIdentifierElasticQuery);
            hasFilters = true;
        }

        if(this.dataset_ids != null){
            DatasetIdElasticQuery datasetIdElasticQuery = queryFactory.query(DatasetIdElasticQuery.class);
            datasetIdElasticQuery.identifiers(this.dataset_ids);

            DatasetElasticQuery datasetElasticQuery = queryFactory.query(DatasetElasticQuery.class);
            datasetElasticQuery.datasetIdElasticQuery(datasetIdElasticQuery);

            dmpElasticQuery.datasetSubQuery(datasetElasticQuery);
            hasFilters = true;
        }

        if(this.dataset_personal_data != null && !this.dataset_personal_data.isEmpty()){
            DatasetElasticQuery datasetElasticQuery = queryFactory.query(DatasetElasticQuery.class);
            datasetElasticQuery.personalDataTypes(this.dataset_personal_data.stream().map(PersonalDataType::of).filter(Objects::nonNull).toList());

            dmpElasticQuery.datasetSubQuery(datasetElasticQuery);
            hasFilters = true;
        }

        if(this.dataset_sensitive_data != null && !this.dataset_sensitive_data.isEmpty()){
            DatasetElasticQuery datasetElasticQuery = queryFactory.query(DatasetElasticQuery.class);
            datasetElasticQuery.sensitiveDataTypes(this.dataset_sensitive_data.stream().map(SensitiveDataType::of).filter(Objects::nonNull).toList());

            dmpElasticQuery.datasetSubQuery(datasetElasticQuery);
            hasFilters = true;
        }

        if(this.metadata_standard_ids != null){
            MetadataStandardIdElasticQuery metadataStandardIdElasticQuery = queryFactory.query(MetadataStandardIdElasticQuery.class);
            metadataStandardIdElasticQuery.identifiers(this.metadata_standard_ids);

            MetadataElasticQuery metadataElasticQuery = queryFactory.query(MetadataElasticQuery.class);
            metadataElasticQuery.standardIdSubQuery(metadataStandardIdElasticQuery);

            DatasetElasticQuery datasetElasticQuery = queryFactory.query(DatasetElasticQuery.class);
            datasetElasticQuery.metadataElasticQuery(metadataElasticQuery);

            dmpElasticQuery.datasetSubQuery(datasetElasticQuery);
            hasFilters = true;
        }

        if(this.distribution_formats != null && !this.distribution_formats.isEmpty()){
            DistributionElasticQuery distributionElasticQuery = queryFactory.query(DistributionElasticQuery.class);
            distributionElasticQuery.formats(this.distribution_formats);

            dmpElasticQuery.datasetSubQuery(this.wrapDistribution(queryFactory, distributionElasticQuery));
            hasFilters = true;
        }

        if(this.distribution_data_access != null && !this.distribution_data_access.isEmpty()){
            DistributionElasticQuery distributionElasticQuery = queryFactory.query(DistributionElasticQuery.class);
            // shared is deprecated in favour of restricted; the spec asks to treat both as equivalent when filtering
            distributionElasticQuery.dataAccessTypes(this.distribution_data_access.stream().map(DataAccessType::of).filter(Objects::nonNull)
                    .flatMap(x -> x == DataAccessType.RESTRICTED || x == DataAccessType.SHARED ? Stream.of(DataAccessType.RESTRICTED, DataAccessType.SHARED) : Stream.of(x))
                    .distinct().toList());

            dmpElasticQuery.datasetSubQuery(this.wrapDistribution(queryFactory, distributionElasticQuery));
            hasFilters = true;
        }

        if(this.host_ids != null && !this.host_ids.isEmpty()){
            HostIdElasticQuery hostIdElasticQuery = queryFactory.query(HostIdElasticQuery.class);
            hostIdElasticQuery.identifiers(this.host_ids);

            HostElasticQuery hostElasticQuery = queryFactory.query(HostElasticQuery.class);
            hostElasticQuery.hostIdSubQuery(hostIdElasticQuery);

            DistributionElasticQuery distributionElasticQuery = queryFactory.query(DistributionElasticQuery.class);
            distributionElasticQuery.hostElasticQuery(hostElasticQuery);

            dmpElasticQuery.datasetSubQuery(this.wrapDistribution(queryFactory, distributionElasticQuery));
            hasFilters = true;
        }

        if(this.host_titles != null && !this.host_titles.isEmpty()){
            HostElasticQuery hostElasticQuery = queryFactory.query(HostElasticQuery.class);
            hostElasticQuery.titles(this.host_titles);

            DistributionElasticQuery distributionElasticQuery = queryFactory.query(DistributionElasticQuery.class);
            distributionElasticQuery.hostElasticQuery(hostElasticQuery);

            dmpElasticQuery.datasetSubQuery(this.wrapDistribution(queryFactory, distributionElasticQuery));
            hasFilters = true;
        }

        if(this.license_refs != null && !this.license_refs.isEmpty()){
            LicenseElasticQuery licenseElasticQuery = queryFactory.query(LicenseElasticQuery.class);
            licenseElasticQuery.licenseRefs(this.license_refs);

            DistributionElasticQuery distributionElasticQuery = queryFactory.query(DistributionElasticQuery.class);
            distributionElasticQuery.licenseElasticQuery(licenseElasticQuery);

            dmpElasticQuery.datasetSubQuery(this.wrapDistribution(queryFactory, distributionElasticQuery));
            hasFilters = true;
        }

        if(this.dmp_ids != null) {
            DmpIdElasticQuery dmpIdElasticQuery = queryFactory.query(DmpIdElasticQuery.class);
            dmpIdElasticQuery.identifiers(this.dmp_ids);
            dmpElasticQuery.dmpIdSubQuery(dmpIdElasticQuery);
            hasFilters = true;
        }

        if(this.funder_ids != null){
            FunderIdElasticQuery funderIdElasticQuery = queryFactory.query(FunderIdElasticQuery.class);
            funderIdElasticQuery.identifiers(this.funder_ids);

            FundingElasticQuery fundingElasticQuery = queryFactory.query(FundingElasticQuery.class);
            fundingElasticQuery.funderIdSubQuery(funderIdElasticQuery);

            ProjectElasticQuery projectElasticQuery = queryFactory.query(ProjectElasticQuery.class);
            projectElasticQuery.fundingElasticQuery(fundingElasticQuery);

            dmpElasticQuery.projectSubQuery(projectElasticQuery);
            hasFilters = true;
        }

        if(this.grant_ids != null){
            GrantIdElasticQuery grantIdElasticQuery = queryFactory.query(GrantIdElasticQuery.class);
            grantIdElasticQuery.identifiers(this.grant_ids);

            FundingElasticQuery fundingElasticQuery = queryFactory.query(FundingElasticQuery.class);
            fundingElasticQuery.grantIdSubQuery(grantIdElasticQuery);

            ProjectElasticQuery projectElasticQuery = queryFactory.query(ProjectElasticQuery.class);
            projectElasticQuery.fundingElasticQuery(fundingElasticQuery);

            dmpElasticQuery.projectSubQuery(projectElasticQuery);
            hasFilters = true;
        }

        if(this.funding_status != null && !this.funding_status.isEmpty()){
            FundingElasticQuery fundingElasticQuery = queryFactory.query(FundingElasticQuery.class);
            fundingElasticQuery.fundingStatusTypes(this.funding_status.stream().map(FundingStatusType::of).filter(Objects::nonNull).toList());

            ProjectElasticQuery projectElasticQuery = queryFactory.query(ProjectElasticQuery.class);
            projectElasticQuery.fundingElasticQuery(fundingElasticQuery);

            dmpElasticQuery.projectSubQuery(projectElasticQuery);
            hasFilters = true;
        }

        if(this.embargo_after != null){
            Instant instant = this.convertStringToInstant(this.embargo_after);
            if(instant != null) {
                LicenseElasticQuery licenseElasticQuery = queryFactory.query(LicenseElasticQuery.class);
                licenseElasticQuery.embargoAfter(instant);

                DistributionElasticQuery distributionElasticQuery = queryFactory.query(DistributionElasticQuery.class);
                distributionElasticQuery.licenseElasticQuery(licenseElasticQuery);

                dmpElasticQuery.datasetSubQuery(this.wrapDistribution(queryFactory, distributionElasticQuery));
                hasFilters = true;
            }
        }

        if(this.embargo_before != null){
            Instant instant = this.convertStringToInstant(this.embargo_before);
            if(instant != null) {
                LicenseElasticQuery licenseElasticQuery = queryFactory.query(LicenseElasticQuery.class);
                licenseElasticQuery.embargoBefore(instant);

                DistributionElasticQuery distributionElasticQuery = queryFactory.query(DistributionElasticQuery.class);
                distributionElasticQuery.licenseElasticQuery(licenseElasticQuery);

                dmpElasticQuery.datasetSubQuery(this.wrapDistribution(queryFactory, distributionElasticQuery));
                hasFilters = true;
            }
        }


        if (this.count != null || this.offset != null) {
            Paging paging = new Paging();
            if (count != null) paging.setSize(this.count);
            if (offset != null) paging.setOffset(this.offset);

            query.setPage(paging);
        }

        if (this.sort != null && !this.sort.isEmpty()) {
            Ordering ordering = new Ordering();
            ordering.setItems(new ArrayList<>());

            for (String sortField : this.sort) {
                if (sortField != null && sortField.contains(",")) {
                    String[] parts = sortField.split(",");
                    if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                        String field = parts[0].trim();
                        String order = parts[1].trim();

                        if (order.equalsIgnoreCase("asc")) {
                            ordering.getItems().add("+" + DmpRootElasticEntity._dmp + "." + field);
                        }

                        if (order.equalsIgnoreCase("desc")) {
                            ordering.getItems().add("-" + DmpRootElasticEntity._dmp + "." + field);
                        }
                    }
                }
            }

            query.setOrder(ordering);
        }

        if (hasFilters) query.dmpElasticQuery(dmpElasticQuery);


        return query;
    }

    private DatasetElasticQuery wrapDistribution(QueryFactory queryFactory, DistributionElasticQuery distributionElasticQuery) {
        DatasetElasticQuery datasetElasticQuery = queryFactory.query(DatasetElasticQuery.class);
        datasetElasticQuery.distributionElasticQuery(distributionElasticQuery);
        return datasetElasticQuery;
    }

    private Instant convertStringToInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e1) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return ldt.atZone(ZoneOffset.UTC).toInstant();
            } catch (DateTimeParseException e2) {
                LocalDate ld = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return ld.atStartOfDay(ZoneOffset.UTC).toInstant();
            }
        }
    }

    private boolean isSortField(String value) {
        if (value == null) return false;
        String lower = value.toLowerCase();
        return List.of(DmpElasticEntity._title, DmpElasticEntity._created, DmpElasticEntity._modified, DmpElasticEntity._language, "embargo", "keyword")
                .contains(lower);
    }

    private boolean isDirection(String value) {
        if (value == null) return false;
        String lower = value.toLowerCase();
        return "asc".equals(lower) || "desc".equals(lower);
    }

}
