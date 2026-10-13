
package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "identifier",
    "type"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "The HostId Schema")
public class HostIdModel implements Serializable
{

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("identifier")
    @JsonPropertyDescription("To indicate the specific value of an identifier for a host")
    private String identifier;
    public static final String _identifier = "identifier";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    @JsonPropertyDescription("To specify a type of an identifier for a host. Suggested Values: url")
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
