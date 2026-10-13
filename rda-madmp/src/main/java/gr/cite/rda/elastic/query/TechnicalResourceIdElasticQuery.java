package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.TechnicalResourceIdElasticEntity;
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
public class TechnicalResourceIdElasticQuery extends ElasticNestedQuery<TechnicalResourceIdElasticQuery, TechnicalResourceIdElasticEntity, UUID> {

	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);


	public TechnicalResourceIdElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public TechnicalResourceIdElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;

	@Autowired
	public TechnicalResourceIdElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
        this.conventionService = conventionService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return false;
	}

	@Override
	protected Class<TechnicalResourceIdElasticEntity> entityClass() {
		return TechnicalResourceIdElasticEntity.class;
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
	public TechnicalResourceIdElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		TechnicalResourceIdElasticEntity mocDoc = new TechnicalResourceIdElasticEntity();
		if (columns.contains(TechnicalResourceIdElasticEntity._identifier)) mocDoc.setIdentifier(FieldBasedMapper.shallowSafeConversion(rawData.get(TechnicalResourceIdElasticEntity._identifier), String.class));
		if (columns.contains(TechnicalResourceIdElasticEntity._type)) mocDoc.setType(FieldBasedMapper.shallowSafeConversion(rawData.get(TechnicalResourceIdElasticEntity._type), String.class));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(TechnicalResourceIdElasticEntity._identifier)) return this.elasticFieldOf(TechnicalResourceIdElasticEntity._identifier);
		else if (item.match(TechnicalResourceIdElasticEntity._type)) return this.elasticFieldOf(TechnicalResourceIdElasticEntity._type);
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
		return null;
	}
}
