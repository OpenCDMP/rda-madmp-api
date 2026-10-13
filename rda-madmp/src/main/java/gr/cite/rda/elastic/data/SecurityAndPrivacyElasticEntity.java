package gr.cite.rda.elastic.data;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class SecurityAndPrivacyElasticEntity {

    @Field(value = SecurityAndPrivacyElasticEntity._title, type = FieldType.Text)
    private String title;
    public static final String _title = "title";

    @Field(value = SecurityAndPrivacyElasticEntity._description, type = FieldType.Text)
    private String description;
    public static final String _description = "description";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}