package gr.cite.rda.integrationevent.inbox.planremoval;

import gr.cite.rda.integrationevent.TrackedEvent;

import java.util.List;
import java.util.UUID;

public class PlanRemovalIntegrationEvent extends TrackedEvent {

    private List<UUID> planIds;

    public List<UUID> getPlanIds() {
        return planIds;
    }

    public void setPlanIds(List<UUID> planIds) {
        this.planIds = planIds;
    }
}
