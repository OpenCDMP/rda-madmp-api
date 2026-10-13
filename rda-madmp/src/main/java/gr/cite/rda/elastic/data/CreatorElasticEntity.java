
package gr.cite.rda.elastic.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;


public class CreatorElasticEntity {

    @Field(value = CreatorElasticEntity._affiliation, type = FieldType.Nested)
    private List<AffiliationElasticEntity> affiliation;
    public static final String _affiliation = "affiliation";

    @JsonProperty(value = "creator_id")
    @Field(value = CreatorElasticEntity._creatorId, type = FieldType.Nested)
    private List<CreatorIdElasticEntity> creatorId;
    public static final String _creatorId = "creatorId";

    @Field(value = CreatorElasticEntity._mbox, type = FieldType.Text)
    private String mbox;
    public static final String _mbox = "mbox";

    @Field(value = CreatorElasticEntity._name, type = FieldType.Text)
    private String name;
    public static final String _name = "name";

    public List<AffiliationElasticEntity> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<AffiliationElasticEntity> affiliation) {
        this.affiliation = affiliation;
    }

    public List<CreatorIdElasticEntity> getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(List<CreatorIdElasticEntity> creatorId) {
        this.creatorId = creatorId;
    }

    public String getMbox() {
        return mbox;
    }

    public void setMbox(String mbox) {
        this.mbox = mbox;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
