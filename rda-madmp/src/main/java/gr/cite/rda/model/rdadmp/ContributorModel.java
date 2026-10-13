package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonPropertyOrder({ ContributorModel._affiliation, ContributorModel._contributorId, ContributorModel._mbox, ContributorModel._name, ContributorModel._role})
@Schema(name = "The DMP Contributor Items Schema")
public class ContributorModel {

    @JsonProperty("affiliation")
    private List<AffiliationModel> affiliation;
    public static final String _affiliation = "affiliation";

    @JsonProperty("contributor_id")
    private List<ContributorIdModel> contributorId;
    public static final String _contributorId = "contributorId";

    @Schema(
            format = "email",
            example = "cc@example.com"
    )
    private String mbox;
    public static final String _mbox = "mbox";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    public static final String _name = "name";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> role;
    public static final String _role = "role";

    public List<AffiliationModel> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<AffiliationModel> affiliation) {
        this.affiliation = affiliation;
    }

    public List<ContributorIdModel> getContributorId() {
        return contributorId;
    }

    public void setContributorId(List<ContributorIdModel> contributorId) {
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