package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.common.enums.LanguageType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class MetadataElasticEntity {

    @Field(value = MetadataElasticEntity._description, type = FieldType.Text)
    private String description;
    public static final String _description = "description";

    @Field(value = MetadataElasticEntity._language, type = FieldType.Keyword)
    private LanguageType language;
    public static final String _language = "language";

    @JsonProperty("metadata_standard_id")
    @Field(value = MetadataElasticEntity._metadataStandardId, type = FieldType.Nested)
    private List<MetadataStandardIdElasticEntity> metadataStandardId;
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

    public List<MetadataStandardIdElasticEntity> getMetadataStandardId() {
        return metadataStandardId;
    }

    public void setMetadataStandardId(List<MetadataStandardIdElasticEntity> metadataStandardId) {
        this.metadataStandardId = metadataStandardId;
    }
}