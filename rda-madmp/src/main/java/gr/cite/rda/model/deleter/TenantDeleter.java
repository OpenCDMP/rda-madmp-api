package gr.cite.rda.model.deleter;

import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.data.TenantEntity;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.event.EventBroker;
import gr.cite.rda.event.TenantTouchedEvent;
import gr.cite.rda.query.TenantQuery;
import gr.cite.tools.data.deleter.Deleter;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.management.InvalidApplicationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TenantDeleter implements Deleter {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(TenantDeleter.class));

	private final TenantEntityManager entityManager;
	protected final QueryFactory queryFactory;
	private final EventBroker eventBroker;

	@Autowired
	public TenantDeleter(
			TenantEntityManager entityManager,
			QueryFactory queryFactory, EventBroker eventBroker
	) {
		this.entityManager = entityManager;
		this.queryFactory = queryFactory;
		this.eventBroker = eventBroker;
	}

	public void deleteAndSaveByIds(List<UUID> ids) throws InvalidApplicationException {
		logger.debug(new MapLogEntry("collecting to delete").And("count", Optional.ofNullable(ids).map(List::size).orElse(0)).And("ids", ids));
		List<TenantEntity> data = this.queryFactory.query(TenantQuery.class).ids(ids).collect();
		logger.trace("received {} items", Optional.of(data).map(List::size).orElse(0));
		this.deleteAndSave(data);
	}

	public void deleteAndSave(List<TenantEntity> data) throws InvalidApplicationException {
		logger.debug("will delete {} items", Optional.ofNullable(data).map(List::size).orElse(0));
		this.delete(data);
		logger.trace("saving changes");
		this.entityManager.flush();
		logger.trace("changes saved");
	}

	public void delete(List<TenantEntity> datas) throws InvalidApplicationException {
		logger.debug("will delete {}  items", Optional.ofNullable(datas).map(List::size).orElse(0));
		if (datas == null || datas.isEmpty()) return;

		Instant now = Instant.now();

		for (TenantEntity item : datas) {
			logger.trace("deleting item {}", item.getId());
			item.setIsActive(IsActive.Inactive);
			item.setUpdatedAt(now);
			logger.trace("updating item");
			this.entityManager.merge(item);
			logger.trace("updated item");
			this.eventBroker.emit(new TenantTouchedEvent(item.getId(), item.getCode()));
		}

	}
}
