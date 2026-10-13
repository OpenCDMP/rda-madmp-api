
package gr.cite.rda.elastic.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class RelatedIdentifierElasticEntity
{

    @Field(value = RelatedIdentifierElasticEntity._identifier, type = FieldType.Keyword)
    private String identifier;
    public static final String _identifier = "identifier";

    @JsonProperty("metadata_scheme")
    @Field(value = RelatedIdentifierElasticEntity._metadataScheme, type = FieldType.Text)
    private String metadataScheme;
    public static final String _metadataScheme = "metadataScheme";

    @JsonProperty("relation_type")
    @Field(value = RelatedIdentifierElasticEntity._relationType, type = FieldType.Text)
    private String relationType;
    public static final String _relationType = "relationType";

    @JsonProperty("resource_type")
    @Field(value = RelatedIdentifierElasticEntity._resourceType, type = FieldType.Text)
    private String resourceType;
    public static final String _resourceType = "resourceType";

    @JsonProperty("scheme_type")
    @Field(value = RelatedIdentifierElasticEntity._schemeType, type = FieldType.Text)
    private String schemeType;
    public static final String _schemeType = "schemeType";

    @JsonProperty("scheme_uri")
    @Field(value = RelatedIdentifierElasticEntity._schemeUri, type = FieldType.Text)
    private String schemeUri;
    public static final String _schemeUri = "schemeUri";

    @Field(value = RelatedIdentifierElasticEntity._type, type = FieldType.Text)
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

    public String getSchemeUri() {
        return schemeUri;
    }

    public void setSchemeUri(String schemeUri) {
        this.schemeUri = schemeUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
