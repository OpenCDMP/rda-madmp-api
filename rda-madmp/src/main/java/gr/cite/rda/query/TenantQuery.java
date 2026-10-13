package gr.cite.rda.query;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.data.TenantEntity;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.model.Tenant;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TenantQuery extends QueryBase<TenantEntity> {

    private String like;

    private Collection<UUID> ids;

    private Collection<IsActive> isActives;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    private final TenantEntityManager tenantEntityManager;

	public TenantQuery(TenantEntityManager tenantEntityManager) {
		this.tenantEntityManager = tenantEntityManager;
	}

	public TenantQuery like(String value) {
        this.like = value;
        return this;
    }

    public TenantQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public TenantQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public TenantQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public TenantQuery isActive(IsActive value) {
        this.isActives = List.of(value);
        return this;
    }

    public TenantQuery isActive(IsActive... value) {
        this.isActives = Arrays.asList(value);
        return this;
    }

    public TenantQuery isActive(Collection<IsActive> values) {
        this.isActives = values;
        return this;
    }

    public TenantQuery authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    public TenantQuery enableTracking() {
        this.noTracking = false;
        return this;
    }

    public TenantQuery disableTracking() {
        this.noTracking = true;
        return this;
    }

    @Override
    protected EntityManager entityManager(){
        return this.tenantEntityManager.getEntityManager();
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.isActives);
    }

    @Override
    protected Class<TenantEntity> entityClass() {
        return TenantEntity.class;
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.ids != null) {
            predicates.add(queryContext.Root.get(TenantEntity._id).in(ids));
        }

        if (this.like != null && !this.like.isEmpty()) {
            predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(TenantEntity._isActive), this.like));
        }

        if (this.isActives != null) {
            predicates.add(queryContext.Root.get(TenantEntity._isActive).in(isActives));
        }
        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }

    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(Tenant._id))
            return TenantEntity._id;
        else if (item.match(Tenant._code))
            return TenantEntity._code;
        else if (item.match(Tenant._createdAt))
            return TenantEntity._createdAt;
        else if (item.match(Tenant._updatedAt))
            return TenantEntity._updatedAt;
        else if (item.match(Tenant._isActive))
            return TenantEntity._isActive;
        else
            return null;
    }

    @Override
    protected TenantEntity convert(Tuple tuple, Set<String> columns) {
        TenantEntity item = new TenantEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, TenantEntity._id, UUID.class));
        item.setCode(QueryBase.convertSafe(tuple, columns, TenantEntity._code, String.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, TenantEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, TenantEntity._updatedAt, Instant.class));
        item.setIsActive(QueryBase.convertSafe(tuple, columns, TenantEntity._isActive, IsActive.class));
        return item;
    }

}
