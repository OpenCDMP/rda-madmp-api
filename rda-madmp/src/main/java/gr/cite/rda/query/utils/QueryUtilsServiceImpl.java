package gr.cite.rda.query.utils;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.stereotype.Component;

@Component
public class QueryUtilsServiceImpl implements QueryUtilsService {
    @Override
    public <Key, D> Subquery<Key> buildSubQuery(BuildSubQueryInput<D, Key> parameters){
        Subquery<Key> subQuery = parameters.getQuery().subquery(parameters.getKeyType());
        Root<D> subQueryRoot = subQuery.from(parameters.getEntityType());
        subQuery.select(parameters.getKeyPathFunc().apply(subQueryRoot)).distinct(true);
        subQuery.where(parameters.getFilterFunc().apply(subQueryRoot, parameters.getCriteriaBuilder()));
        return subQuery;
    }
}

