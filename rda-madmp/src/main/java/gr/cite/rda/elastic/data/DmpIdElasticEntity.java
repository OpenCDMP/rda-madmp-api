package gr.cite.rda.elastic.data;


import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class DmpIdElasticEntity {

    @Field(value = DmpIdElasticEntity._identifier, type = FieldType.Keyword)
    private String identifier;
    public static final String _identifier = "identifier";

    @Field(value = DmpIdElasticEntity._type, type = FieldType.Keyword)
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