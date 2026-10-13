package gr.cite.rda.integrationevent;

import gr.cite.rda.integrationevent.inbox.InboxProperties;
import gr.cite.rda.integrationevent.inbox.InboxRepositoryImpl;
import gr.cite.queueinbox.InboxConfigurer;
import gr.cite.queueinbox.repository.InboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({InboxProperties.class})
@ConditionalOnProperty(prefix = "queue.task.listener", name = "enable", matchIfMissing = false)
public class InboxIntegrationEventConfigurer extends InboxConfigurer {

    private final ApplicationContext applicationContext;

    private final InboxProperties inboxProperties;

    public InboxIntegrationEventConfigurer(ApplicationContext applicationContext, InboxProperties inboxProperties) {
        this.applicationContext = applicationContext;
        this.inboxProperties = inboxProperties;
    }

    @Bean
    public InboxRepository inboxRepositoryCreator() {
        return new InboxRepositoryImpl(this.applicationContext, this.inboxProperties);
    }

}

