package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.elastic.data.GrantIdElasticEntity;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticInnerObjectQuery;
import gr.cite.tools.elastic.query.ElasticNestedQuery;
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
public class GrantIdElasticQuery extends ElasticInnerObjectQuery<GrantIdElasticQuery, GrantIdElasticEntity, UUID> {

	private Collection<String> identifiers;
	private String innerPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public GrantIdElasticQuery identifiers(String value) {
		this.identifiers = List.of(value);
		return this;
	}

	public GrantIdElasticQuery identifiers(String... value) {
		this.identifiers = Arrays.asList(value);
		return this;
	}

	public GrantIdElasticQuery identifiers(Collection<String> values) {
		this.identifiers = values;
		return this;
	}

	public GrantIdElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;


	@Autowired
	public GrantIdElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.identifiers);
	}

	@Override
	protected Class<GrantIdElasticEntity> entityClass() {
		return GrantIdElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.identifiers != null) {
			predicates.add(this.containsString(this.elasticFieldOf(GrantIdElasticEntity._identifier).disableInfer(true), this.identifiers)._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public GrantIdElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		GrantIdElasticEntity mocDoc = new GrantIdElasticEntity();
		if (columns.contains(GrantIdElasticEntity._identifier)) mocDoc.setIdentifier(FieldBasedMapper.shallowSafeConversion(rawData.get(GrantIdElasticEntity._identifier), String.class));
		if (columns.contains(GrantIdElasticEntity._type)) mocDoc.setType(FieldBasedMapper.shallowSafeConversion(rawData.get(GrantIdElasticEntity._type), String.class));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(GrantIdElasticEntity._identifier)) return this.elasticFieldOf(GrantIdElasticEntity._identifier);
		else if (item.match(GrantIdElasticEntity._type)) return this.elasticFieldOf(GrantIdElasticEntity._type);
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
	protected String getInnerPath() {
		return this.innerPath;
	}

	@Override
	public GrantIdElasticQuery innerPath(String value) {
		this.innerPath = value;
		return this;
	}

	@Override
	protected UUID toKey(String key) {
		return UUID.fromString(key);
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(GrantIdElasticEntity._identifier);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		return null;
	}
}
