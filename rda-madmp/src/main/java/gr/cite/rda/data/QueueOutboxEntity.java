package gr.cite.rda.data;

import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.data.conventers.IsActiveConverter;
import gr.cite.rda.data.conventers.QueueOutboxNotifyStatusConverter;
import gr.cite.queueoutbox.entity.QueueOutbox;
import gr.cite.queueoutbox.entity.QueueOutboxNotifyStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "\"QueueOutbox\"")
public class QueueOutboxEntity implements QueueOutbox {

    @Id
    @Column(name = "\"id\"", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    public static final String _id = "id";

    @Column(name = "\"exchange\"", nullable = false, length = 200)
    private String exchange;

    public static final String _exchange = "exchange";

    @Column(name = "\"route\"", length = 200)
    private String route;

    public static final String _route = "route";

    @Column(name = "\"message_id\"", columnDefinition = "uuid", nullable = false)
    private UUID messageId;

    public static final String _messageId = "messageId";

    @Column(name = "\"message\"", columnDefinition = "json", nullable = false)
    private String message;

    public static final String _message = "message";

    @Column(name = "\"notify_status\"", nullable = false)
    @Convert(converter = QueueOutboxNotifyStatusConverter.class)
    private QueueOutboxNotifyStatus notifyStatus;
    public final static String _notifyStatus = "notifyStatus";

    @Column(name = "\"retry_count\"", nullable = false)
    private Integer retryCount;

    public static final String _retryCount = "retryCount";

    @Column(name = "\"published_at\"")
    private Instant publishedAt;

    public static final String _publishedAt = "publishedAt";

    @Column(name = "\"confirmed_at\"")
    private Instant confirmedAt;

    public static final String _confirmedAt = "confirmedAt";

    @Column(name = "\"tenant\"", columnDefinition = "uuid")
    private UUID tenantId;

    public static final String _tenantId = "tenantId";

    @Column(name = "\"created_at\"", nullable = false)
    private Instant createdAt;

    public static final String _createdAt = "createdAt";

    @Column(name = "\"updated_at\"", nullable = false)
    @Version
    private Instant updatedAt;

    public static final String _updatedAt = "updatedAt";

    @Column(name = "\"is_active\"", length = 20, nullable = false)
    @Convert(converter = IsActiveConverter.class)
    private IsActive isActive;

    public static final String _isActive = "isActive";

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String getRoute() {
        return route;
    }

    @Override
    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public UUID getMessageId() {
        return messageId;
    }

    @Override
    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public QueueOutboxNotifyStatus getNotifyStatus() {
        return notifyStatus;
    }

    @Override
    public void setNotifyStatus(QueueOutboxNotifyStatus notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    @Override
    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public Instant getPublishedAt() {
        return publishedAt;
    }

    @Override
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    @Override
    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
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

