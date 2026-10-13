package gr.cite.rda.query;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.authorization.Permission;
import gr.cite.rda.common.enums.IsActive;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.rda.data.TenantEntityManager;
import gr.cite.rda.data.UserEntity;
import gr.cite.rda.model.User;
import gr.cite.rda.model.user.PublicUser;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryBase;
import gr.cite.tools.data.query.QueryContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.Instant;
import java.util.*;

@Component
@RequestScope
public class UserQuery extends QueryBase<UserEntity> {

    private String like;

    private Collection<UUID> ids;

    private Collection<IsActive> isActives;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    private final UserScope userScope;

    private final AuthorizationService authService;

    private final TenantEntityManager tenantEntityManager;
    public UserQuery(
		    UserScope userScope,
		    AuthorizationService authService, TenantEntityManager tenantEntityManager
    ) {
        this.userScope = userScope;
        this.authService = authService;
	    this.tenantEntityManager = tenantEntityManager;
    }

    public UserQuery like(String value) {
        this.like = value;
        return this;
    }

    public UserQuery ids(UUID value) {
        this.ids = List.of(value);
        return this;
    }

    public UserQuery ids(UUID... value) {
        this.ids = Arrays.asList(value);
        return this;
    }

    public UserQuery ids(Collection<UUID> values) {
        this.ids = values;
        return this;
    }

    public UserQuery isActive(IsActive value) {
        this.isActives = List.of(value);
        return this;
    }

    public UserQuery isActive(IsActive... value) {
        this.isActives = Arrays.asList(value);
        return this;
    }

    public UserQuery isActive(Collection<IsActive> values) {
        this.isActives = values;
        return this;
    }

    public UserQuery authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    public UserQuery enableTracking() {
        this.noTracking = false;
        return this;
    }

    public UserQuery disableTracking() {
        this.noTracking = true;
        return this;
    }

    @Override
    protected EntityManager entityManager(){
        return this.tenantEntityManager.getEntityManager();
    }

    @Override
    protected Class<UserEntity> entityClass() {
        return UserEntity.class;
    }

    @Override
    protected Boolean isFalseQuery() {
        return this.isEmpty(this.ids) || this.isEmpty(this.isActives);
    }

    @Override
    protected <X, Y> Predicate applyAuthZ(QueryContext<X, Y> queryContext) {
        if (this.authorize.contains(AuthorizationFlags.None))
            return null;
        if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseUser))
            return null;
        UUID ownerId = null;
        if (this.authorize.contains(AuthorizationFlags.Owner))
            ownerId = this.userScope.getUserIdSafe();

        List<Predicate> predicates = new ArrayList<>();
        if (ownerId != null) {
            predicates.add(queryContext.CriteriaBuilder.equal(queryContext.Root.get(User._id), ownerId));
        }
        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return queryContext.CriteriaBuilder.or(); //Creates a false query
        }
    }

    @Override
    protected <X, Y> Predicate applyFilters(QueryContext<X, Y> queryContext) {
        List<Predicate> predicates = new ArrayList<>();
        if (this.ids != null) {
            CriteriaBuilder.In<UUID> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserEntity._id));
            for (UUID item : this.ids)
                inClause.value(item);
            predicates.add(inClause);
        }
        if (this.like != null && !this.like.isEmpty()) {
            predicates.add(queryContext.CriteriaBuilder.like(queryContext.Root.get(UserEntity._name), this.like));
        }
        if (this.isActives != null) {
            CriteriaBuilder.In<IsActive> inClause = queryContext.CriteriaBuilder.in(queryContext.Root.get(UserEntity._isActive));
            for (IsActive item : this.isActives)
                inClause.value(item);
            predicates.add(inClause);
        }
        if (!predicates.isEmpty()) {
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            return queryContext.CriteriaBuilder.and(predicatesArray);
        } else {
            return null;
        }
    }

    @Override
    protected UserEntity convert(Tuple tuple, Set<String> columns) {
        UserEntity item = new UserEntity();
        item.setId(QueryBase.convertSafe(tuple, columns, UserEntity._id, UUID.class));
        item.setName(QueryBase.convertSafe(tuple, columns, UserEntity._name, String.class));
        item.setAdditionalInfo(QueryBase.convertSafe(tuple, columns, UserEntity._additionalInfo, String.class));
        item.setCreatedAt(QueryBase.convertSafe(tuple, columns, UserEntity._createdAt, Instant.class));
        item.setUpdatedAt(QueryBase.convertSafe(tuple, columns, UserEntity._updatedAt, Instant.class));
        item.setIsActive(QueryBase.convertSafe(tuple, columns, UserEntity._isActive, IsActive.class));
        return item;
    }

    @Override
    protected String fieldNameOf(FieldResolver item) {
        if (item.match(User._id) || item.match(PublicUser._id))
            return UserEntity._id;
        else if (item.match(User._name) || item.match(PublicUser._name))
            return UserEntity._name;
        else if (item.match(User._createdAt))
            return UserEntity._createdAt;
        else if (item.match(User._updatedAt))
            return UserEntity._updatedAt;
        else if (item.match(User._hash))
            return UserEntity._updatedAt;
        else if (item.match(User._isActive))
            return UserEntity._isActive;
        else
            return null;
    }
}
