package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({ GrantIdModel._identifier, GrantIdModel._type})
@Schema(name = "The Funding Grant ID Schema")
public class GrantIdModel {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String identifier;
    public static final String _identifier = "identifier";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
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