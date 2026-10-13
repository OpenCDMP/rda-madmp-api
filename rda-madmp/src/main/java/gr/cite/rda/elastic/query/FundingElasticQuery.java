package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.enums.FundingStatusType;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.FundingElasticEntity;
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
public class FundingElasticQuery extends ElasticNestedQuery<FundingElasticQuery, FundingElasticEntity, UUID> {

	private FunderIdElasticQuery funderIdElasticQuery;
	private GrantIdElasticQuery grantIdElasticQuery;
	private Collection<FundingStatusType> fundingStatusTypes;
	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public FundingElasticQuery fundingStatusTypes(FundingStatusType value) {
		this.fundingStatusTypes = List.of(value);
		return this;
	}

	public FundingElasticQuery fundingStatusTypes(FundingStatusType... value) {
		this.fundingStatusTypes = Arrays.asList(value);
		return this;
	}

	public FundingElasticQuery fundingStatusTypes(Collection<FundingStatusType> values) {
		this.fundingStatusTypes = values;
		return this;
	}

	public FundingElasticQuery funderIdSubQuery(FunderIdElasticQuery subQuery) {
		this.funderIdElasticQuery = subQuery;
		return this;
	}

	public FundingElasticQuery grantIdSubQuery(GrantIdElasticQuery subQuery) {
		this.grantIdElasticQuery = subQuery;
		return this;
	}

	public FundingElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public FundingElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;

	@Autowired
	public FundingElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
        this.conventionService = conventionService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.fundingStatusTypes);
	}

	@Override
	protected Class<FundingElasticEntity> entityClass() {
		return FundingElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.funderIdElasticQuery != null) {
			predicates.add(this.funderIdElasticQuery.innerPath(this.conventionService.asIndexer(this.getNestedPath(), FundingElasticEntity._funderId)).applyFilters());
		}
		if (this.grantIdElasticQuery != null) {
			predicates.add(this.grantIdElasticQuery.innerPath(this.conventionService.asIndexer(this.getNestedPath(), FundingElasticEntity._grantId)).applyFilters());
		}
		if (this.fundingStatusTypes != null) {
			predicates.add(this.containsString(this.elasticFieldOf(FundingElasticEntity._fundingStatus).disableInfer(true), this.fundingStatusTypes.stream().map(FundingStatusType::getValue).toList())._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public FundingElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		FundingElasticEntity mocDoc = new FundingElasticEntity();
		if (columns.contains(FundingElasticEntity._fundingStatus)) mocDoc.setFundingStatus(FieldBasedMapper.shallowSafeConversion(rawData.get(FundingElasticEntity._fundingStatus), FundingStatusType.class));
		mocDoc.setFunderId(this.convertInnerObject(rawData, columns, this.queryFactory.query(FunderIdElasticQuery.class), FundingElasticEntity._funderId, null));
		mocDoc.setGrantId(this.convertInnerObject(rawData, columns, this.queryFactory.query(GrantIdElasticQuery.class), FundingElasticEntity._grantId, null));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(FundingElasticEntity._fundingStatus)) return this.elasticFieldOf(FundingElasticEntity._fundingStatus);
		else if (item.prefix(FundingElasticEntity._funderId)) return this.queryFactory.query(FunderIdElasticQuery.class).innerPath(FundingElasticEntity._funderId).fieldNameOf(this.extractPrefixed(item, FundingElasticEntity._funderId));
		else if (item.prefix(FundingElasticEntity._grantId)) return this.queryFactory.query(GrantIdElasticQuery.class).innerPath(FundingElasticEntity._grantId).fieldNameOf(this.extractPrefixed(item, FundingElasticEntity._grantId));
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
