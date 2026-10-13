package gr.cite.rda.integrationevent.inbox.tenanttouched;


import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.integrationevent.TrackedEvent;

import java.util.UUID;

public class TenantTouchedIntegrationEvent extends TrackedEvent {

    @JsonProperty(value = "Id")
    private UUID id;

    @JsonProperty(value = "Code")
    private String code;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
