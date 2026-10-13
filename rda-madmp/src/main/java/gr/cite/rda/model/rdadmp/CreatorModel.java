
package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "affiliation",
    "creator_id",
    "mbox",
    "name"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "The Creator Schema", hidden = true)
public class CreatorModel implements Serializable
{

    @JsonProperty("affiliation")
    private List<AffiliationModel> affiliation;
    public static final String _affiliation = "affiliation";

    @JsonProperty("creator_id")
    private List<CreatorIdModel> creatorId;
    public static final String _creator_id = "creatorId";

    @Schema(
            format = "email",
            example = "cc@example.com"
    )
    @JsonProperty("mbox")
    @JsonPropertyDescription("Contact Person's E-mail address")
    private String mbox;
    public static final String _mbox = "mbox";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    @JsonPropertyDescription("Name of the contact person")
    private String name;
    public static final String _name = "name";

    public List<AffiliationModel> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<AffiliationModel> affiliation) {
        this.affiliation = affiliation;
    }

    public List<CreatorIdModel> getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(List<CreatorIdModel> creatorId) {
        this.creatorId = creatorId;
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


}
