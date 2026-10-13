package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonPropertyOrder({ ContactModel._affiliation, ContactModel._contactId, ContactModel._mbox, ContactModel._name})
@Schema(name = "The DMP Contact Schema")
public class ContactModel {

    @JsonProperty("affiliation")
    private List<AffiliationModel> affiliation;
    public static final String _affiliation = "affiliation";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("contact_id")
    private List<ContactIdModel> contactId;
    public static final String _contactId = "contactId";

    @Schema(
            format = "email",
            example = "cc@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String mbox;
    public static final String _mbox = "mbox";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    public static final String _name = "name";

    public List<AffiliationModel> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<AffiliationModel> affiliation) {
        this.affiliation = affiliation;
    }

    public List<ContactIdModel> getContactId() {
        return contactId;
    }

    public void setContactId(List<ContactIdModel> contactId) {
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