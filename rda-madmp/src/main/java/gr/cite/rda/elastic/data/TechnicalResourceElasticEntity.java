package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class TechnicalResourceElasticEntity {

    @Field(value = TechnicalResourceElasticEntity._description, type = FieldType.Text)
    private String description;
    public static final String _description = "description";

    @Field(value = TechnicalResourceElasticEntity._name, type = FieldType.Text)
    private String name;
    public static final String _name = "name";

    @JsonProperty("technical_resource_id")
    @Field(value = TechnicalResourceElasticEntity._technicalResourceId, type = FieldType.Nested)
    private List<TechnicalResourceIdElasticEntity> technicalResourceId;
    public static final String _technicalResourceId = "technical_resource_id";

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

    public List<TechnicalResourceIdElasticEntity> getTechnicalResourceId() {
        return technicalResourceId;
    }

    public void setTechnicalResourceId(List<TechnicalResourceIdElasticEntity> technicalResourceId) {
        this.technicalResourceId = technicalResourceId;
    }
}