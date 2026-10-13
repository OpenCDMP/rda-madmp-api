package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.elastic.data.CreatorElasticEntity;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
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
public class CreatorElasticQuery extends ElasticNestedQuery<CreatorElasticQuery, CreatorElasticEntity, UUID> {

	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public CreatorElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public CreatorElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;

	@Autowired
	public CreatorElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService) {
		super(elasticsearchTemplate, elasticProperties);
        this.queryFactory = queryFactory;
        this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return false;
	}

	@Override
	protected Class<CreatorElasticEntity> entityClass() {
		return CreatorElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}

	@Override
	protected Query applyFilters() {
		return null;
	}

	@Override
	public CreatorElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		CreatorElasticEntity mocDoc = new CreatorElasticEntity();

		mocDoc.setAffiliation(this.convertNested(rawData, columns, this.queryFactory.query(AffiliationElasticQuery.class), CreatorElasticEntity._affiliation, null));
		if (columns.contains(CreatorElasticEntity._mbox)) mocDoc.setMbox(FieldBasedMapper.shallowSafeConversion(rawData.get(CreatorElasticEntity._mbox), String.class));
		if (columns.contains(CreatorElasticEntity._name)) mocDoc.setName(FieldBasedMapper.shallowSafeConversion(rawData.get(CreatorElasticEntity._name), String.class));
		mocDoc.setCreatorId(this.convertNested(rawData, columns, this.queryFactory.query(CreatorIdElasticQuery.class), CreatorElasticEntity._creatorId, null));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(CreatorElasticEntity._name)) return this.elasticFieldOf(CreatorElasticEntity._name);
		else if (item.match(CreatorElasticEntity._mbox)) return this.elasticFieldOf(CreatorElasticEntity._mbox);
		else if (item.prefix(CreatorElasticEntity._creatorId)) return this.queryFactory.query(CreatorIdElasticQuery.class).nestedPath(CreatorElasticEntity._creatorId).fieldNameOf(this.extractPrefixed(item, CreatorElasticEntity._creatorId));
		else if (item.prefix(CreatorElasticEntity._affiliation)) return this.queryFactory.query(CreatorIdElasticQuery.class).nestedPath(CreatorElasticEntity._affiliation).fieldNameOf(this.extractPrefixed(item, CreatorElasticEntity._affiliation));

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
	protected String getNestedPath()  {
		return this.nestedPath;
	}

	@Override
	protected UUID toKey(String key) {
		return UUID.fromString(key);
	}

	@Override
	protected ElasticField getKeyField() {
		return null;
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		if (item.prefix(CreatorElasticEntity._affiliation)) return this.queryFactory.query(AffiliationElasticQuery.class).nestedPath(CreatorElasticEntity._affiliation);
		if (item.prefix(CreatorElasticEntity._creatorId)) return this.queryFactory.query(CreatorIdElasticQuery.class).nestedPath(CreatorElasticEntity._creatorId);
		return null;
	}
}
