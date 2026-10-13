package gr.cite.rda.integrationevent.inbox.userremoval;

import gr.cite.rda.integrationevent.inbox.ConsistencyHandler;
import gr.cite.rda.query.UserQuery;
import gr.cite.tools.data.query.QueryFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserRemovalConsistencyHandler implements ConsistencyHandler<UserRemovalConsistencyPredicates> {

    private final QueryFactory queryFactory;

    public UserRemovalConsistencyHandler(QueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean isConsistent(UserRemovalConsistencyPredicates consistencyPredicates) {
        long count = this.queryFactory.query(UserQuery.class).disableTracking().ids(consistencyPredicates.getUserId()).count();
        return count != 0;
    }
}
