package gr.cite.rda.integrationevent.inbox;

public interface IntegrationEventHandler {

    EventProcessingStatus handle(IntegrationEventProperties properties, String message);

}
