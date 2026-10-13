package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.fasterxml.jackson.databind.type.TypeFactory;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.ContributorElasticEntity;
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
public class ContributorElasticQuery  extends ElasticNestedQuery<ContributorElasticQuery, ContributorElasticEntity, UUID> {

	private ContributorIdElasticQuery contributorIdElasticQuery;
	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public ContributorElasticQuery contributorIdElasticQuery(ContributorIdElasticQuery subQuery) {
		this.contributorIdElasticQuery = subQuery;
		return this;
	}
	public ContributorElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public ContributorElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;


	@Autowired
	public ContributorElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
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
	protected Class<ContributorElasticEntity> entityClass() {
		return ContributorElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.contributorIdElasticQuery != null) {
			predicates.add(this.nestedQuery(this.contributorIdElasticQuery.nestedPath(this.conventionService.asIndexer(this.getNestedPath(),ContributorElasticEntity._contributorId))).build()._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public ContributorElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		ContributorElasticEntity mocDoc = new ContributorElasticEntity();

		mocDoc.setAffiliation(this.convertNested(rawData, columns, this.queryFactory.query(AffiliationElasticQuery.class), ContributorElasticEntity._affiliation, null));

		if (columns.contains(ContributorElasticEntity._mbox)) mocDoc.setMbox(FieldBasedMapper.shallowSafeConversion(rawData.get(ContributorElasticEntity._mbox), String.class));
		if (columns.contains(ContributorElasticEntity._name)) mocDoc.setName(FieldBasedMapper.shallowSafeConversion(rawData.get(ContributorElasticEntity._name), String.class));
		if (columns.contains(ContributorElasticEntity._role)) mocDoc.setRole(FieldBasedMapper.shallowSafeConversion(rawData.get(ContributorElasticEntity._role), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class)));

		mocDoc.setContributorId(this.convertNested(rawData, columns, this.queryFactory.query(ContributorIdElasticQuery.class), ContributorElasticEntity._contributorId, null));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.prefix(ContributorElasticEntity._affiliation)) return this.queryFactory.query(AffiliationElasticQuery.class).nestedPath(ContributorElasticEntity._affiliation).fieldNameOf(this.extractPrefixed(item, ContributorElasticEntity._affiliation));
		else if (item.match(ContributorElasticEntity._mbox)) return this.elasticFieldOf(ContributorElasticEntity._mbox);
		else if (item.match(ContributorElasticEntity._name)) return this.elasticFieldOf(ContributorElasticEntity._name);
		else if (item.match(ContributorElasticEntity._role)) return this.elasticFieldOf(ContributorElasticEntity._role);
		else if (item.prefix(ContributorElasticEntity._contributorId)) return this.queryFactory.query(ContributorIdElasticQuery.class).nestedPath(ContributorElasticEntity._contributorId).fieldNameOf(this.extractPrefixed(item, ContributorElasticEntity._contributorId));
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
		if (item.prefix(ContributorElasticEntity._affiliation)) return this.queryFactory.query(AffiliationElasticQuery.class).nestedPath(ContributorElasticEntity._affiliation);
		if (item.prefix(ContributorElasticEntity._contributorId)) return this.queryFactory.query(AffiliationElasticQuery.class).nestedPath(ContributorElasticEntity._contributorId);
		return null;
	}
}
