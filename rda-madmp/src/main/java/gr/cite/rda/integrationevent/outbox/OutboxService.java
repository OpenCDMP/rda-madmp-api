package gr.cite.rda.integrationevent.outbox;

public interface OutboxService {
	void publish(OutboxIntegrationEvent event);
}
