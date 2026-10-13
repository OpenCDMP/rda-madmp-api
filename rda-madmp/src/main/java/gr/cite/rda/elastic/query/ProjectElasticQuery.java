package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.*;
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
public class ProjectElasticQuery extends ElasticNestedQuery<ProjectElasticQuery, ProjectElasticEntity, UUID> {

	private FundingElasticQuery fundingElasticQuery;
	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public ProjectElasticQuery fundingElasticQuery(FundingElasticQuery subQuery) {
		this.fundingElasticQuery = subQuery;
		return this;
	}
	
	public ProjectElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public ProjectElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;

	@Autowired
	public ProjectElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
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
	protected Class<ProjectElasticEntity> entityClass() {
		return ProjectElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.fundingElasticQuery != null) {
			predicates.add(this.nestedQuery(this.fundingElasticQuery.nestedPath(this.conventionService.asIndexer(this.getNestedPath(), ProjectElasticEntity._funding))).build()._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public ProjectElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		ProjectElasticEntity mocDoc = new ProjectElasticEntity();
		if (columns.contains(ProjectElasticEntity._title)) mocDoc.setTitle(FieldBasedMapper.shallowSafeConversion(rawData.get(ProjectElasticEntity._title), String.class));
		if (columns.contains(ProjectElasticEntity._description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(ProjectElasticEntity._description), String.class));
		if (columns.contains(ProjectElasticEntity._start)) mocDoc.setStart(FieldBasedMapper.shallowSafeConversion(rawData.get(ProjectElasticEntity._start), Date.class));
		if (columns.contains(ProjectElasticEntity._end)) mocDoc.setEnd(FieldBasedMapper.shallowSafeConversion(rawData.get(ProjectElasticEntity._end), Date.class));
		mocDoc.setFunding(this.convertNested(rawData, columns, this.queryFactory.query(FundingElasticQuery.class), ProjectElasticEntity._funding, null));
		mocDoc.setProjectId(this.convertNested(rawData, columns, this.queryFactory.query(ProjectIdElasticQuery.class), ProjectElasticEntity._projectId, null));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(ProjectElasticEntity._title)) return this.elasticFieldOf(ProjectElasticEntity._title);
		else if (item.match(ProjectElasticEntity._description)) return this.elasticFieldOf(ProjectElasticEntity._description);
		else if (item.match(ProjectElasticEntity._start)) return this.elasticFieldOf(ProjectElasticEntity._start);
		else if (item.match(ProjectElasticEntity._end)) return this.elasticFieldOf(ProjectElasticEntity._end);
		else if (item.prefix(ProjectElasticEntity._funding)) return this.queryFactory.query(FundingElasticQuery.class).nestedPath(ProjectElasticEntity._funding).fieldNameOf(this.extractPrefixed(item, ProjectElasticEntity._funding));
		else if (item.prefix(ProjectElasticEntity._projectId)) return this.queryFactory.query(ProjectIdElasticQuery.class).nestedPath(ProjectElasticEntity._projectId).fieldNameOf(this.extractPrefixed(item, ProjectElasticEntity._projectId));

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
		if (item.prefix(ProjectElasticEntity._funding)) return this.queryFactory.query(FundingElasticQuery.class).nestedPath(ProjectElasticEntity._funding);
		if (item.prefix(ProjectElasticEntity._projectId)) return this.queryFactory.query(ProjectIdElasticQuery.class).nestedPath(ProjectElasticEntity._projectId);

		return null;
	}
}
