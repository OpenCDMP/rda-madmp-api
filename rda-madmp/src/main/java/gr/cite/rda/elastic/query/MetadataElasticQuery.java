package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.enums.LanguageType;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.MetadataElasticEntity;
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
public class MetadataElasticQuery extends ElasticNestedQuery<MetadataElasticQuery, MetadataElasticEntity, UUID> {

	private MetadataStandardIdElasticQuery metadataStandardIdElasticQuery;
	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public MetadataElasticQuery standardIdSubQuery(MetadataStandardIdElasticQuery subQuery) {
		this.metadataStandardIdElasticQuery = subQuery;
		return this;
	}
	public MetadataElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public MetadataElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;

	@Autowired
	public MetadataElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
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
	protected Class<MetadataElasticEntity> entityClass() {
		return MetadataElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.metadataStandardIdElasticQuery != null) {
			predicates.add(this.nestedQuery(this.metadataStandardIdElasticQuery.nestedPath(this.conventionService.asIndexer(this.getNestedPath(), MetadataElasticEntity._metadataStandardId))).build()._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public MetadataElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		MetadataElasticEntity mocDoc = new MetadataElasticEntity();
		if (columns.contains(MetadataElasticEntity._description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(MetadataElasticEntity._description), String.class));
		if (columns.contains(MetadataElasticEntity._language)) mocDoc.setLanguage(FieldBasedMapper.shallowSafeConversion(rawData.get(MetadataElasticEntity._language), LanguageType.class));
		mocDoc.setMetadataStandardId(this.convertNested(rawData, columns, this.queryFactory.query(MetadataStandardIdElasticQuery.class), MetadataElasticEntity._metadataStandardId, null));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(MetadataElasticEntity._description)) return this.elasticFieldOf(MetadataElasticEntity._description);
		else if (item.match(MetadataElasticEntity._language)) return this.elasticFieldOf(MetadataElasticEntity._language);
		else if (item.prefix(MetadataElasticEntity._metadataStandardId)) return this.queryFactory.query(MetadataStandardIdElasticQuery.class).nestedPath(MetadataElasticEntity._metadataStandardId).fieldNameOf(this.extractPrefixed(item, MetadataElasticEntity._metadataStandardId));
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
		if (item.prefix(MetadataElasticEntity._metadataStandardId)) return this.queryFactory.query(MetadataStandardIdElasticQuery.class).nestedPath(MetadataElasticEntity._metadataStandardId);
		return null;
	}
}
