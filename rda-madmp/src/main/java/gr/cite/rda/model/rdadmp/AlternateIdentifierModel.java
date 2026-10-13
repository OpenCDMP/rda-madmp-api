package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "identifier",
        "type"
})
@Schema(name = "The Alternate Identifier Schema", hidden = true)
public class AlternateIdentifierModel implements Serializable
{

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("identifier")
    @JsonPropertyDescription("Identifier for a DMP for an Affiliation")
    private String identifier;
    public static final String _identifier = "identifier";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    @JsonPropertyDescription("The DMP Identifier Type For an Affiliation. Suggested Values: ror, grid, isni")
    private String type;
    public static final String _type = "type";

    @JsonProperty("identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("identifier")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }


}
