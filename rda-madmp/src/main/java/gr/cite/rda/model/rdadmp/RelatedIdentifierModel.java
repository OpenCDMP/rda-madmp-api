
package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.net.URI;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        RelatedIdentifierModel._identifier,
        RelatedIdentifierModel._type,
        RelatedIdentifierModel._relationType,
        RelatedIdentifierModel._metadataScheme,
        RelatedIdentifierModel._schemeUri,
        RelatedIdentifierModel._schemeType,
        RelatedIdentifierModel._resourceType,
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "The Related Identifier Schema", hidden = true)
public class RelatedIdentifierModel implements Serializable
{

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("identifier")
    @JsonPropertyDescription("Value of the identifier")
    private String identifier;
    public static final String _identifier = "identifier";

    @JsonProperty("metadata_scheme")
    @JsonPropertyDescription("Name of the related metadata schema (if applicable)")
    private String metadataScheme;
    public static final String _metadataScheme = "metadataScheme";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("relation_type")
    @JsonPropertyDescription("Type of relation between the resource and the related resource")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String relationType;
    public static final String _relationType = "relationType";

    @JsonProperty("resource_type")
    @JsonPropertyDescription("Project start date")
    private String resourceType;
    public static final String _resourceType = "resourceType";

    @JsonProperty("scheme_type")
    @JsonPropertyDescription("Type of the related metadata scheme linked with scheme URI ")
    private String schemeType;
    public static final String _schemeType = "schemeType";

    @JsonProperty("scheme_uri")
    @JsonPropertyDescription("Link to the scheme of the identifier")
    private URI schemeUri;
    public static final String _schemeUri = "schemeUri";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    @JsonPropertyDescription("Type of the identifier")
    private String type;
    public static final String _type = "type";

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getMetadataScheme() {
        return metadataScheme;
    }

    public void setMetadataScheme(String metadataScheme) {
        this.metadataScheme = metadataScheme;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }

    public URI getSchemeUri() {
        return schemeUri;
    }

    public void setSchemeUri(URI schemeUri) {
        this.schemeUri = schemeUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
