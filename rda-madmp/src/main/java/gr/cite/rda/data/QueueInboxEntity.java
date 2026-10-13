package gr.cite.rda.data;

import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.data.conventers.IsActiveConverter;
import gr.cite.rda.data.conventers.QueueInboxStatusConverter;
import gr.cite.rda.data.types.JsonSQLType;
import gr.cite.queueinbox.entity.QueueInbox;
import gr.cite.queueinbox.entity.QueueInboxStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "\"QueueInbox\"")
public class QueueInboxEntity implements QueueInbox {

    @Id
    @Column(name = "\"id\"", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    public static final String _id = "id";

    @Column(name = "\"queue\"", nullable = false, length = 200)
    private String queue;

    public static final String _queue = "queue";

    @Column(name = "\"exchange\"", nullable = false, length = 200)
    private String exchange;

    public static final String _exchange = "exchange";

    @Column(name = "\"route\"", nullable = false, length = 200)
    private String route;

    public static final String _route = "route";

    @Column(name = "\"application_id\"", nullable = false, length = 100)
    private String applicationId;

    public static final String _applicationId = "applicationId";

    @Column(name = "\"message_id\"", columnDefinition = "uuid", nullable = false)
    private UUID messageId;

    public static final String _messageId = "messageId";

    @Type(JsonSQLType.class)
    @Column(name = "\"message\"", columnDefinition = "json", nullable = false)
    private String message;

    public static final String _message = "message";

    @Column(name = "\"retry_count\"")
    private Integer retryCount;

    public static final String _retryCount = "retryCount";

    @Column(name = "\"tenant\"", columnDefinition = "uuid")
    private UUID tenantId;

    public static final String _tenantId = "tenantId";

    @Column(name = "\"status\"", nullable = false)
    @Convert(converter = QueueInboxStatusConverter.class)
    private QueueInboxStatus status;
    public final static String _status = "status";


    @Column(name = "\"created_at\"", nullable = false)
    private Instant createdAt;

    public static final String _createdAt = "createdAt";

    @Column(name = "\"updated_at\"", nullable = false)
    @Version
    private Instant updatedAt;

    public static final String _updatedAt = "updatedAt";

    @Column(name = "\"is_active\"", nullable = false)
    @Convert(converter = IsActiveConverter.class)
    private IsActive isActive;

    public static final String _isActive = "isActive";

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public QueueInboxStatus getStatus() {
        return status;
    }

    public void setStatus(QueueInboxStatus status) {
        this.status = status;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public IsActive getIsActive() {
        return isActive;
    }

    public void setIsActive(IsActive isActive) {
        this.isActive = isActive;
    }
}

