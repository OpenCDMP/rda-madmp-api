package gr.cite.rda.elastic.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class ContactElasticEntity {

    @Field(value = ContactElasticEntity._affiliation, type = FieldType.Nested)
    private List<AffiliationElasticEntity> affiliation;
    public static final String _affiliation = "affiliation";

    @JsonProperty(value = "contact_id")
    @Field(value = ContactElasticEntity._contactId, type = FieldType.Nested)
    private List<ContactIdElasticEntity> contactId;
    public static final String _contactId = "contactId";

    @Field(value = ContactElasticEntity._mbox, type = FieldType.Text)
    private String mbox;
    public static final String _mbox = "mbox";

    @Field(value = ContactElasticEntity._name, type = FieldType.Text)
    private String name;
    public static final String _name = "name";

    public List<AffiliationElasticEntity> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<AffiliationElasticEntity> affiliation) {
        this.affiliation = affiliation;
    }

    public List<ContactIdElasticEntity> getContactId() {
        return contactId;
    }

    public void setContactId(List<ContactIdElasticEntity> contactId) {
        this.contactId = contactId;
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