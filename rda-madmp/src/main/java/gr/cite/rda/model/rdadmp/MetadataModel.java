package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gr.cite.rda.common.enums.LanguageType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonPropertyOrder({ MetadataModel._description, MetadataModel._language, MetadataModel._metadataStandardId})
@Schema(name = "The Dataset Metadata Items Schema")
public class MetadataModel {

    private String description;
    public static final String _description = "description";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LanguageType language;
    public static final String _language = "language";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("metadata_standard_id")
    private List<MetadataStandardIdModel> metadataStandardId;
    public static final String _metadataStandardId = "metadataStandardId";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LanguageType getLanguage() {
        return language;
    }

    public void setLanguage(LanguageType language) {
        this.language = language;
    }

    public List<MetadataStandardIdModel> getMetadataStandardId() {
        return metadataStandardId;
    }

    public void setMetadataStandardId(List<MetadataStandardIdModel> metadataStandardId) {
        this.metadataStandardId = metadataStandardId;
    }
}