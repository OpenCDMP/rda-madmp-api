package gr.cite.rda.query.utils;

import jakarta.persistence.criteria.Subquery;

public interface QueryUtilsService {
    <Key, D> Subquery<Key> buildSubQuery(BuildSubQueryInput<D, Key> parameters);
}
