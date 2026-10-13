package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.elastic.data.DmpUserElasticEntity;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticNestedQuery;

import org.opencdmp.commonmodels.enums.PlanUserRole;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DmpUserElasticQuery extends ElasticNestedQuery<DmpUserElasticQuery, DmpUserElasticEntity, UUID> {

	private Collection<UUID> userIds;
	private Collection<PlanUserRole> userRoles;

	public DmpUserElasticQuery userIds(UUID value) {
		this.userIds = List.of(value);
		return this;
	}

	public DmpUserElasticQuery userIds(UUID... value) {
		this.userIds = Arrays.asList(value);
		return this;
	}

	public DmpUserElasticQuery userIds(Collection<UUID> values) {
		this.userIds = values;
		return this;
	}

	public DmpUserElasticQuery userRoles(PlanUserRole value) {
		this.userRoles = List.of(value);
		return this;
	}

	public DmpUserElasticQuery userRoles(PlanUserRole... value) {
		this.userRoles = Arrays.asList(value);
		return this;
	}

	public DmpUserElasticQuery userRoles(Collection<PlanUserRole> values) {
		this.userRoles = values;
		return this;
	}
	private String nestedPath;

	@Override
	public DmpUserElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}


	public DmpUserElasticQuery(
			ElasticsearchTemplate elasticsearchRestTemplate,
			ElasticProperties elasticProperties
	) {
		super(elasticsearchRestTemplate, elasticProperties);
	}

	@Override
	protected Class<DmpUserElasticEntity> entityClass() {
		return DmpUserElasticEntity.class;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.userIds) ||this.isEmpty(this.userRoles);
	}

	@Override
	protected Query applyAuthZ() {
		return null;
	}

	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.userIds != null) {
			predicates.add(this.containsUUID(this.elasticFieldOf(DmpUserElasticEntity._userId), this.userIds)._toQuery());
		}

		if (this.userRoles != null) {
			predicates.add(this.contains(this.elasticFieldOf(DmpUserElasticEntity._role), this.userRoles.stream().map(PlanUserRole::getValue).toList().toArray(new Short[this.userRoles.size()]))._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public DmpUserElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		DmpUserElasticEntity mocDoc = new DmpUserElasticEntity();
		if (columns.contains(DmpUserElasticEntity._userId)) mocDoc.setUserId(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpUserElasticEntity._userId), UUID.class));
		if (columns.contains(DmpUserElasticEntity._role)) mocDoc.setRole(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpUserElasticEntity._role), PlanUserRole.class));
		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(DmpUserElasticEntity._userId)) return this.elasticFieldOf(DmpUserElasticEntity._userId).disableInfer(true);
		else if (item.match(DmpUserElasticEntity._role)) return this.elasticFieldOf(DmpUserElasticEntity._role).disableInfer(true);
		else return null;
	}

	@Override
	protected String getNestedPath() {
		return this.nestedPath;
	}

	@Override
	protected UUID toKey(String key) {
		return UUID.fromString(key);
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(DmpUserElasticEntity._userId);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		return null;
	}
}
