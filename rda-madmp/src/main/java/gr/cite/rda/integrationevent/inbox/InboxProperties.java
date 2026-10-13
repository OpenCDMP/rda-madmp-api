package gr.cite.rda.integrationevent.inbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "queue.task.listener.options")
public class InboxProperties {

    private final String exchange;

    private final List<String> tenantRemovalTopic;

    private final List<String> tenantTouchTopic;

    private final List<String> userRemovalTopic;

    private final List<String> userTouchTopic;

    private final List<String> planTouchTopic;
    private final List<String> planRemovalTopic;

    public InboxProperties(
            String exchange,
            List<String> tenantRemovalTopic,
            List<String> tenantTouchTopic,
            List<String> userRemovalTopic,
            List<String> userTouchTopic,
            List<String> planTouchTopic, List<String> planRemovalTopic) {
        this.exchange = exchange;
        this.tenantRemovalTopic = tenantRemovalTopic;
        this.tenantTouchTopic = tenantTouchTopic;
        this.userRemovalTopic = userRemovalTopic;
        this.userTouchTopic = userTouchTopic;
        this.planTouchTopic = planTouchTopic;
	    this.planRemovalTopic = planRemovalTopic;
    }

    public List<String> getTenantRemovalTopic() {
        return tenantRemovalTopic;
    }

    public List<String> getTenantTouchTopic() {
        return tenantTouchTopic;
    }

    public List<String> getUserRemovalTopic() {
        return userRemovalTopic;
    }

    public List<String> getUserTouchTopic() {
        return userTouchTopic;
    }

    public List<String> getPlanTouchTopic() {
        return planTouchTopic;
    }

    public List<String> getPlanRemovalTopic() {
        return planRemovalTopic;
    }

    public String getExchange() {
        return exchange;
    }

}
