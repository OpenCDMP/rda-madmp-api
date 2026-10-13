package gr.cite.rda.integrationevent.inbox.tenantremoval;

import gr.cite.rda.integrationevent.inbox.ConsistencyHandler;
import gr.cite.rda.query.TenantQuery;
import gr.cite.tools.data.query.QueryFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TenantRemovalConsistencyHandler implements ConsistencyHandler<TenantRemovalConsistencyPredicates> {

    private final QueryFactory queryFactory;

    public TenantRemovalConsistencyHandler(QueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean isConsistent(TenantRemovalConsistencyPredicates consistencyPredicates) {
        long count = this.queryFactory.query(TenantQuery.class).disableTracking().ids(consistencyPredicates.getTenantId()).count();
        return count > 0;
    }

}
