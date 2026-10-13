package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "affiliation_id",
        "name"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "The Affiliation Schema", hidden = true)
public class AffiliationModel implements Serializable
{

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("affiliation_id")
    @JsonPropertyDescription("Identifier for an affiliation")
    private AffiliationIdModel affiliationId;
    public static final String _affiliationId = "affiliationId";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    @JsonPropertyDescription("Name of an affiliation")
    private String name;
    public static final String _name = "name";

    public AffiliationIdModel getAffiliationId() {
        return affiliationId;
    }

    public void setAffiliationId(AffiliationIdModel affiliationId) {
        this.affiliationId = affiliationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
