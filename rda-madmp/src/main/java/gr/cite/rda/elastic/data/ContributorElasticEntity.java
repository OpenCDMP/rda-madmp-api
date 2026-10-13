package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.model.rdadmp.AffiliationModel;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class ContributorElasticEntity {

    @Field(value = ContributorElasticEntity._affiliation, type = FieldType.Nested)
    private List<AffiliationElasticEntity> affiliation;
    public static final String _affiliation = "affiliation";

    @JsonProperty(value = "contributor_id")
    @Field(value = ContributorElasticEntity._contributorId, type = FieldType.Nested)
    private List<ContributorIdElasticEntity> contributorId;
    public static final String _contributorId = "contributorId";

    @Field(value = ContributorElasticEntity._mbox, type = FieldType.Text)
    private String mbox;
    public static final String _mbox = "mbox";

    @Field(value = ContributorElasticEntity._name, type = FieldType.Text)
    private String name;
    public static final String _name = "name";

    @Field(value = ContributorElasticEntity._role, type = FieldType.Keyword)
    private List<String> role;
    public static final String _role = "role";

    public List<AffiliationElasticEntity> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<AffiliationElasticEntity> affiliation) {
        this.affiliation = affiliation;
    }

    public List<ContributorIdElasticEntity> getContributorId() {
        return contributorId;
    }

    public void setContributorId(List<ContributorIdElasticEntity> contributorId) {
        this.contributorId = contributorId;
    }

    public String getMbox() {
        return mbox;
    }

    public void setMbox(String mbox) {
        this.mbox = mbox;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }
}