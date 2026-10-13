package gr.cite.rda.integrationevent.inbox.userremoval;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.integrationevent.TrackedEvent;

import java.util.UUID;

public class UserRemovalIntegrationEvent extends TrackedEvent {

    @JsonProperty(value = "UserId")
    private UUID userId;


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
