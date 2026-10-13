package gr.cite.rda.integrationevent.inbox.userremoval;


import gr.cite.rda.integrationevent.inbox.ConsistencyPredicates;

import java.util.UUID;

public class UserRemovalConsistencyPredicates implements ConsistencyPredicates {

    private UUID userId;

    public UserRemovalConsistencyPredicates(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

}
