package gr.cite.rda.integrationevent.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "queue.task.publisher.options")
public class OutboxProperties {

    private final String exchange;

    private final String dmpTouchTopic;

    private final Integer handleAckRetries;

    private final Integer handleAckWaitInMilliSeconds;

    private final Integer handleNackRetries;

    private final Integer handleNackWaitInMilliSeconds;


    public OutboxProperties(String exchange, String dmpTouchTopic,
                            Integer handleAckRetries, Integer handleAckWaitInMilliSeconds, Integer handleNackRetries, Integer handleNackWaitInMilliSeconds) {
        this.exchange = exchange;
        this.dmpTouchTopic = dmpTouchTopic;
        this.handleAckRetries = handleAckRetries;
        this.handleAckWaitInMilliSeconds = handleAckWaitInMilliSeconds;
        this.handleNackRetries = handleNackRetries;
        this.handleNackWaitInMilliSeconds = handleNackWaitInMilliSeconds;
    }

    public String getExchange() {
        return exchange;
    }

    public String getDmpTouchTopic() {
        return dmpTouchTopic;
    }

    public Integer getHandleAckRetries() {
        return handleAckRetries;
    }

    public Integer getHandleAckWaitInMilliSeconds() {
        return handleAckWaitInMilliSeconds;
    }

    public Integer getHandleNackRetries() {
        return handleNackRetries;
    }

    public Integer getHandleNackWaitInMilliSeconds() {
        return handleNackWaitInMilliSeconds;
    }
}
