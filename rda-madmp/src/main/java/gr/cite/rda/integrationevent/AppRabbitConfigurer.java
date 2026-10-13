package gr.cite.rda.integrationevent;

import gr.cite.rda.integrationevent.inbox.InboxProperties;
import gr.cite.rda.integrationevent.outbox.OutboxProperties;
import gr.cite.queueinbox.repository.InboxRepository;
import gr.cite.rabbitmq.RabbitConfigurer;
import gr.cite.rabbitmq.consumer.InboxBindings;
import gr.cite.rabbitmq.consumer.InboxCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({OutboxProperties.class, InboxProperties.class, CleanupTaskProperties.class})
@ConditionalOnProperty(prefix = "queue.rabbitmq", name = "listenerEnabled")
public class AppRabbitConfigurer extends RabbitConfigurer {

    private final ApplicationContext applicationContext;

    private final InboxProperties inboxProperties;

    public AppRabbitConfigurer(ApplicationContext applicationContext, InboxProperties inboxProperties) {
        this.applicationContext = applicationContext;
        this.inboxProperties = inboxProperties;
    }

    @Bean
    public InboxBindings inboxBindingsCreator() {
        List<String> bindingItems = new ArrayList<>();
        bindingItems.addAll(this.inboxProperties.getTenantRemovalTopic());
        bindingItems.addAll(this.inboxProperties.getTenantTouchTopic());
        bindingItems.addAll(this.inboxProperties.getUserRemovalTopic());
        bindingItems.addAll(this.inboxProperties.getUserTouchTopic());
        bindingItems.addAll(this.inboxProperties.getPlanTouchTopic());
        bindingItems.addAll(this.inboxProperties.getPlanRemovalTopic());

        return new InboxBindings(bindingItems);
    }

    @Bean(name = "InboxCreator")
    public InboxCreator inboxCreator() {
        return (params) -> {
            InboxRepository inboxRepository = this.applicationContext.getBean(InboxRepository.class);
            return inboxRepository.create(params) != null;
        };
    }

}
