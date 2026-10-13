package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonPropertyOrder({ TechnicalResourceModel._description, TechnicalResourceModel._name, TechnicalResourceModel._technicalResourceId})
@Schema(name = "The Dataset Technical Resource Items Schema")
public class TechnicalResourceModel {

    private String description;
    public static final String _description = "description";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    public static final String _name = "name";

    @JsonProperty("technical_resource_id")
    private List<TechnicalResourceIdModel> technicalResourceId;
    public static final String _technicalResourceId = "technicalResourceId";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TechnicalResourceIdModel> getTechnicalResourceId() {
        return technicalResourceId;
    }

    public void setTechnicalResourceId(List<TechnicalResourceIdModel> technicalResourceId) {
        this.technicalResourceId = technicalResourceId;
    }
}