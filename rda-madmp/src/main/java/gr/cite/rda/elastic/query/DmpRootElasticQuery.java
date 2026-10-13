package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.authorization.Permission;
import gr.cite.rda.common.enums.AccessType;
import gr.cite.rda.common.enums.ActionType;
import gr.cite.rda.common.enums.Status;
import gr.cite.rda.common.scope.tenant.TenantScope;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.rda.elastic.data.DmpRootElasticEntity;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticNestedQuery;
import gr.cite.tools.elastic.query.ElasticQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
//Like in C# make it Transient
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DmpRootElasticQuery extends ElasticQuery<DmpRootElasticEntity, String> {

	private DmpElasticQuery dmpElasticQuery;
	private Collection<String> ids;
	private Collection<ActionType> actionTypes;
	private DmpUserElasticQuery dmpUserElasticQuery;
	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public DmpRootElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	public DmpRootElasticQuery dmpElasticQuery(DmpElasticQuery subQuery) {
		this.dmpElasticQuery = subQuery;
		return this;
	}

	public DmpRootElasticQuery ids(String value) {
		this.ids = List.of(value);
		return this;
	}

	public DmpRootElasticQuery ids(String... value) {
		this.ids = Arrays.asList(value);
		return this;
	}

	public DmpRootElasticQuery ids(Collection<String> values) {
		this.ids = values;
		return this;
	}

	public DmpRootElasticQuery actionTypes(ActionType value) {
		this.actionTypes = List.of(value);
		return this;
	}

	public DmpRootElasticQuery actionTypes(ActionType... value) {
		this.actionTypes = Arrays.asList(value);
		return this;
	}

	public DmpRootElasticQuery actionTypes(Collection<ActionType> values) {
		this.actionTypes = values;
		return this;
	}

	public DmpRootElasticQuery planStatusElasticQuery(DmpUserElasticQuery subQuery){
		this.dmpUserElasticQuery = subQuery;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final UserScope userScope;
	private final TenantScope tenantScope;
	private final AuthorizationService authService;

	@Autowired
	public DmpRootElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, UserScope userScope, TenantScope tenantScope, AuthorizationService authService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
        this.appElasticConfiguration = appElasticConfiguration;
        this.elasticService = elasticService;
        this.userScope = userScope;
        this.tenantScope = tenantScope;
        this.authService = authService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ids);
	}

	@Override
	protected Class<DmpRootElasticEntity> entityClass() {
		return DmpRootElasticEntity.class;
	}

	@Override
	protected Query applyAuthZ() {
		if (this.authorize.contains(AuthorizationFlags.None)) return null;
		if (this.authorize.contains(AuthorizationFlags.Permission) && this.authService.authorize(Permission.BrowseRdaDmp)) return null;
		UUID userId = null;
		boolean usePublic = this.authorize.contains(AuthorizationFlags.Public);
		if (this.authorize.contains(AuthorizationFlags.PlanAssociated)) userId = this.userScope.getUserIdSafe();

		List<Query> predicates = new ArrayList<>();
		if (usePublic) {
			predicates.add(this.and(
					this.equals(this.elasticFieldOf(DmpRootElasticEntity._accessType), AccessType.Public.getValue()),
					this.equals(this.elasticFieldOf(DmpRootElasticEntity._status), Status.Finalized.getValue())
			));
		}
		if (userId != null) {
			DmpUserElasticQuery query = this.queryFactory.query(DmpUserElasticQuery.class).nestedPath(DmpRootElasticEntity._users);
			query.userIds(userId);
			predicates.add(this.nestedQuery(query).build()._toQuery());
		}
		if (!predicates.isEmpty()) {;
			return this.or(predicates)._toQuery();
		} else {
			return this.equals(this.elasticFieldOf(DmpRootElasticEntity._id), UUID.randomUUID());
		}
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.dmpElasticQuery != null) {
			predicates.add(this.dmpElasticQuery.innerPath(DmpRootElasticEntity._dmp).applyFilters());
		}

		if (this.ids != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DmpRootElasticEntity._id), this.ids)._toQuery());
		}

		if (this.actionTypes != null) {
			predicates.add(this.contains(this.elasticFieldOf(DmpRootElasticEntity._actionType), this.actionTypes.stream().map(ActionType::getValue).toList().toArray(new Short[this.actionTypes.size()]))._toQuery());
		}

		if (this.dmpUserElasticQuery != null) {
			predicates.add(this.nestedQuery( this.dmpUserElasticQuery.nestedPath(DmpRootElasticEntity._users)).build()._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public DmpRootElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		DmpRootElasticEntity mocDoc = new DmpRootElasticEntity();
		if (columns.contains(DmpRootElasticEntity._id)) mocDoc.setId(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpRootElasticEntity._id), String.class));
		mocDoc.setDmp(this.convertInnerObject(rawData, columns, this.queryFactory.query(DmpElasticQuery.class), DmpRootElasticEntity._dmp, null));
		if (columns.contains(DmpRootElasticEntity._accessType)) mocDoc.setAccessType(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpRootElasticEntity._accessType), AccessType.class));
		if (columns.contains(DmpRootElasticEntity._status)) mocDoc.setStatus(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpRootElasticEntity._status), Status.class));
		if (columns.contains(DmpRootElasticEntity._tenantId)) mocDoc.setTenantId(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpRootElasticEntity._tenantId), UUID.class));
		if (columns.contains(DmpRootElasticEntity._actionType)) mocDoc.setActionType(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpRootElasticEntity._actionType), ActionType.class));
		mocDoc.setUsers(this.convertNested(rawData, columns, this.queryFactory.query(DmpUserElasticQuery.class), DmpRootElasticEntity._users, null));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(DmpRootElasticEntity._id)) return this.elasticFieldOf(DmpRootElasticEntity._id);
		else if (item.prefix(DmpRootElasticEntity._dmp)) return this.queryFactory.query(DmpElasticQuery.class).innerPath(DmpRootElasticEntity._dmp).fieldNameOf(this.extractPrefixed(item, DmpRootElasticEntity._dmp));
		else if (item.match(DmpRootElasticEntity._accessType)) return this.elasticFieldOf(DmpRootElasticEntity._accessType);
		else if (item.match(DmpRootElasticEntity._status)) return this.elasticFieldOf(DmpRootElasticEntity._status);
		else if (item.match(DmpRootElasticEntity._tenantId)) return this.elasticFieldOf(DmpRootElasticEntity._tenantId);
		else if (item.prefix(DmpRootElasticEntity._users)) return this.queryFactory.query(DmpUserElasticQuery.class).nestedPath(DmpRootElasticEntity._dmp).fieldNameOf(this.extractPrefixed(item, DmpRootElasticEntity._users));
		else if (item.match(DmpRootElasticEntity._actionType)) return this.elasticFieldOf(DmpRootElasticEntity._actionType);

		else return null;
	}

	@Override
	protected String[] getIndex() {
		List<String> indexNames = new ArrayList<>();
		indexNames.add(this.appElasticConfiguration.getAppElasticProperties().getRdaIndexName());
		try {
			this.elasticService.ensureRdaIndex();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return indexNames.toArray(new String[indexNames.size()]);
	}

	@Override
	protected String toKey(String key) {
		return key;
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(DmpRootElasticEntity._id);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		if (item.prefix(DmpRootElasticEntity._users)) return this.queryFactory.query(DmpUserElasticQuery.class).nestedPath(DmpRootElasticEntity._users);
		else return null;
	}
}
