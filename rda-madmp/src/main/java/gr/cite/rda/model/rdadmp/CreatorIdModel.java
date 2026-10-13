
package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identifier",
    "type"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "The CreatorId Schema", hidden = true)
public class CreatorIdModel implements Serializable
{

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("identifier")
    private String identifier;
    public static final String _identifier = "identifier";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    @JsonPropertyDescription("Identifier type. Allowed values: orcid, isni, openid, other")
    private String type;
    public static final String _type = "type";

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
