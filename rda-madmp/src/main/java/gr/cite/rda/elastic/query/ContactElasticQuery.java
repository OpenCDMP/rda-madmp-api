package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.ContactElasticEntity;
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
public class ContactElasticQuery extends ElasticInnerObjectQuery<ContactElasticQuery, ContactElasticEntity, UUID> {

	private ContactIdElasticQuery contactIdElasticQuery;
	private String innerPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public ContactElasticQuery contactIdSubQuery(ContactIdElasticQuery subQuery) {
		this.contactIdElasticQuery = subQuery;
		return this;
	}
	public ContactElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;


	@Autowired
	public ContactElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
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
	protected Class<ContactElasticEntity> entityClass() {
		return ContactElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.contactIdElasticQuery != null) {
			predicates.add(this.nestedQuery(this.contactIdElasticQuery.nestedPath(this.conventionService.asIndexer(this.getInnerPath(), ContactElasticEntity._contactId))).build()._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public ContactElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		ContactElasticEntity mocDoc = new ContactElasticEntity();
		mocDoc.setAffiliation(this.convertNested(rawData, columns, this.queryFactory.query(AffiliationElasticQuery.class), ContactElasticEntity._affiliation, null));
		if (columns.contains(ContactElasticEntity._mbox)) mocDoc.setMbox(FieldBasedMapper.shallowSafeConversion(rawData.get(ContactElasticEntity._mbox), String.class));
		if (columns.contains(ContactElasticEntity._name)) mocDoc.setName(FieldBasedMapper.shallowSafeConversion(rawData.get(ContactElasticEntity._name), String.class));
		mocDoc.setContactId(this.convertNested(rawData, columns, this.queryFactory.query(ContactIdElasticQuery.class), ContactElasticEntity._contactId, null));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.prefix(ContactElasticEntity._affiliation)) return this.queryFactory.query(AffiliationElasticQuery.class).nestedPath(ContactElasticEntity._affiliation).fieldNameOf(this.extractPrefixed(item, ContactElasticEntity._affiliation));
		else if (item.match(ContactElasticEntity._mbox)) return this.elasticFieldOf(ContactElasticEntity._mbox);
		else if (item.match(ContactElasticEntity._name)) return this.elasticFieldOf(ContactElasticEntity._name);
		else if (item.prefix(ContactElasticEntity._contactId)) return this.queryFactory.query(ContactIdElasticQuery.class).nestedPath(ContactElasticEntity._contactId).fieldNameOf(this.extractPrefixed(item, ContactElasticEntity._contactId));
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
	public ContactElasticQuery innerPath(String value) {
		this.innerPath = value;
		return this;
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
		if (item.prefix(ContactElasticEntity._affiliation)) return this.queryFactory.query(AffiliationElasticQuery.class).nestedPath(ContactElasticEntity._affiliation);
		if (item.prefix(ContactElasticEntity._contactId)) return this.queryFactory.query(ContactIdElasticQuery.class).nestedPath(ContactElasticEntity._contactId);
		return null;
	}
}
