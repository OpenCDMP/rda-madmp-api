package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.elastic.data.ContactIdElasticEntity;
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
public class ContactIdElasticQuery extends ElasticNestedQuery<ContactIdElasticQuery, ContactIdElasticEntity, UUID> {

	private Collection<String> identifiers;
	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public ContactIdElasticQuery identifiers(String value) {
		this.identifiers = List.of(value);
		return this;
	}

	public ContactIdElasticQuery identifiers(String... value) {
		this.identifiers = Arrays.asList(value);
		return this;
	}

	public ContactIdElasticQuery identifiers(Collection<String> values) {
		this.identifiers = values;
		return this;
	}

	public ContactIdElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;


	@Autowired
	public ContactIdElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService) {
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
	protected Class<ContactIdElasticEntity> entityClass() {
		return ContactIdElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.identifiers != null) {
			predicates.add(this.containsString(this.elasticFieldOf(ContactIdElasticEntity._identifier).disableInfer(true), this.identifiers)._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public ContactIdElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		ContactIdElasticEntity mocDoc = new ContactIdElasticEntity();
		if (columns.contains(ContactIdElasticEntity._identifier)) mocDoc.setIdentifier(FieldBasedMapper.shallowSafeConversion(rawData.get(ContactIdElasticEntity._identifier), String.class));
		if (columns.contains(ContactIdElasticEntity._type)) mocDoc.setType(FieldBasedMapper.shallowSafeConversion(rawData.get(ContactIdElasticEntity._type), String.class));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(ContactIdElasticEntity._identifier)) return this.elasticFieldOf(ContactIdElasticEntity._identifier);
		else if (item.match(ContactIdElasticEntity._type)) return this.elasticFieldOf(ContactIdElasticEntity._type);
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
	public ContactIdElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	@Override
	protected UUID toKey(String key) {
		return UUID.fromString(key);
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(ContactIdElasticEntity._identifier);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		return null;
	}
}
