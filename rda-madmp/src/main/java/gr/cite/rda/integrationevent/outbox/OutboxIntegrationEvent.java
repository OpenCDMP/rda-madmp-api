package gr.cite.rda.integrationevent.outbox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gr.cite.rda.integrationevent.TrackedEvent;
import gr.cite.rabbitmq.IntegrationEvent;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboxIntegrationEvent extends IntegrationEvent {

	public static final String DMP_TOUCH = "DMP_TOUCH";

	private TrackedEvent event;

	public TrackedEvent getEvent() {
		return event;
	}

	public void setEvent(TrackedEvent event) {
		this.event = event;
	}

}
