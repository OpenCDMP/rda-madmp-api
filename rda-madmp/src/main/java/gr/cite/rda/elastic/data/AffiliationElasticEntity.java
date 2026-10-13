package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


public class AffiliationElasticEntity {

    @JsonProperty(value = "affiliation_id")
    @Field(value = AffiliationElasticEntity._affiliationId, type = FieldType.Object)
    private AffiliationIdElasticEntity affiliationId;
    public static final String _affiliationId = "affiliationId";

    @Field(value = AffiliationElasticEntity._name, type = FieldType.Text)
    private String name;
    public static final String _name = "name";

    public AffiliationIdElasticEntity getAffiliationId() {
        return affiliationId;
    }

    public void setAffiliationId(AffiliationIdElasticEntity affiliationId) {
        this.affiliationId = affiliationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
