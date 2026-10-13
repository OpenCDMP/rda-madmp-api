package gr.cite.rda.model.rdadmp;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({ FunderIdModel._identifier, FunderIdModel._type})
@Schema(name = "The Funder ID Schema")
public class FunderIdModel {

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