package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.elastic.data.TechnicalResourceElasticEntity;
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
public class TechnicalResourceElasticQuery extends ElasticNestedQuery<TechnicalResourceElasticQuery, TechnicalResourceElasticEntity, UUID> {

	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public TechnicalResourceElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public TechnicalResourceElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;

	@Autowired
	public TechnicalResourceElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService) {
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
	protected Class<TechnicalResourceElasticEntity> entityClass() {
		return TechnicalResourceElasticEntity.class;
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
	public TechnicalResourceElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		TechnicalResourceElasticEntity mocDoc = new TechnicalResourceElasticEntity();
		if (columns.contains(TechnicalResourceElasticEntity._name)) mocDoc.setName(FieldBasedMapper.shallowSafeConversion(rawData.get(TechnicalResourceElasticEntity._name), String.class));
		if (columns.contains(TechnicalResourceElasticEntity._description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(TechnicalResourceElasticEntity._description), String.class));
		mocDoc.setTechnicalResourceId(this.convertNested(rawData, columns, this.queryFactory.query(TechnicalResourceIdElasticQuery.class), TechnicalResourceElasticEntity._technicalResourceId, null));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(TechnicalResourceElasticEntity._name)) return this.elasticFieldOf(TechnicalResourceElasticEntity._name);
		else if (item.match(TechnicalResourceElasticEntity._description)) return this.elasticFieldOf(TechnicalResourceElasticEntity._description);
		else if (item.prefix(TechnicalResourceElasticEntity._technicalResourceId)) return this.queryFactory.query(TechnicalResourceIdElasticQuery.class).nestedPath(TechnicalResourceElasticEntity._technicalResourceId).fieldNameOf(this.extractPrefixed(item, TechnicalResourceElasticEntity._technicalResourceId));
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
		if (item.prefix(TechnicalResourceElasticEntity._technicalResourceId)) return this.queryFactory.query(TechnicalResourceIdElasticQuery.class).nestedPath(TechnicalResourceElasticEntity._technicalResourceId);

		return null;
	}
}
