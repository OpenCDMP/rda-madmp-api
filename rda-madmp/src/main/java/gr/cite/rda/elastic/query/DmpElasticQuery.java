package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.enums.EthicalIssuesExistType;
import gr.cite.rda.common.enums.LanguageType;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DmpElasticEntity;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.ElasticConstants;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Component
//Like in C# make it Transient
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DmpElasticQuery extends ElasticInnerObjectQuery<DmpElasticQuery, DmpElasticEntity, String> {

	private Collection<String> likes;
	private Collection<EthicalIssuesExistType> ethicalIssuesExistTypes;
	private Collection<LanguageType> languageTypes;
	private Instant createdAfter;
	private Instant createdBefore;
	private Instant modifiedAfter;
	private Instant modifiedBefore;
	private DmpIdElasticQuery dmpIdElasticQuery;
	private ContactElasticQuery contactElasticQuery;
	private ContributorElasticQuery contributorElasticQuery;
	private final List<DatasetElasticQuery> datasetElasticQueries = new ArrayList<>();
	private final List<ProjectElasticQuery> projectElasticQueries = new ArrayList<>();
	private AlternativeIdentifierElasticQuery alternativeIdentifierElasticQuery;
	private String innerPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);


	public DmpElasticQuery like(String value) {
		this.likes = List.of(value);
		return this;
	}

	public DmpElasticQuery like(Collection<String> values) {
		this.likes = values;
		return this;
	}

	public DmpElasticQuery ethicalIssuesExistTypes(EthicalIssuesExistType value) {
		this.ethicalIssuesExistTypes = List.of(value);
		return this;
	}

	public DmpElasticQuery ethicalIssuesExistTypes(EthicalIssuesExistType... value) {
		this.ethicalIssuesExistTypes = Arrays.asList(value);
		return this;
	}

	public DmpElasticQuery ethicalIssuesExistTypes(Collection<EthicalIssuesExistType> values) {
		this.ethicalIssuesExistTypes = values;
		return this;
	}

	public DmpElasticQuery languageTypes(LanguageType value) {
		this.languageTypes = List.of(value);
		return this;
	}

	public DmpElasticQuery languageTypes(LanguageType... value) {
		this.languageTypes = Arrays.asList(value);
		return this;
	}

	public DmpElasticQuery languageTypes(Collection<LanguageType> values) {
		this.languageTypes = values;
		return this;
	}

	public DmpElasticQuery createdAfter(Instant value) {
		this.createdAfter = value;
		return this;
	}

	public DmpElasticQuery createdBefore(Instant value) {
		this.createdBefore = value;
		return this;
	}

	public DmpElasticQuery modifiedAfter(Instant value) {
		this.modifiedAfter = value;
		return this;
	}

	public DmpElasticQuery modifiedBefore(Instant value) {
		this.modifiedBefore = value;
		return this;
	}

	public DmpElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	public DmpElasticQuery dmpIdSubQuery(DmpIdElasticQuery subQuery) {
		this.dmpIdElasticQuery = subQuery;
		return this;
	}

	public DmpElasticQuery contactSubQuery(ContactElasticQuery subQuery) {
		this.contactElasticQuery = subQuery;
		return this;
	}

	public DmpElasticQuery contributorSubQuery(ContributorElasticQuery subQuery) {
		this.contributorElasticQuery = subQuery;
		return this;
	}

	public DmpElasticQuery datasetSubQuery(DatasetElasticQuery subQuery) {
		// each filter traversing datasets is applied as an independent nested predicate
		this.datasetElasticQueries.add(subQuery);
		return this;
	}

	public DmpElasticQuery projectSubQuery(ProjectElasticQuery subQuery) {
		// each filter traversing projects is applied as an independent nested predicate
		this.projectElasticQueries.add(subQuery);
		return this;
	}

	public DmpElasticQuery alternativeIdentifierSubQuery(AlternativeIdentifierElasticQuery subQuery) {
		this.alternativeIdentifierElasticQuery = subQuery;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;

	@Autowired
	public DmpElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
        this.appElasticConfiguration = appElasticConfiguration;
        this.elasticService = elasticService;
        this.conventionService = conventionService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.ethicalIssuesExistTypes);
	}

	@Override
	protected Class<DmpElasticEntity> entityClass() {
		return DmpElasticEntity.class;
	}

	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();
		
		if (this.likes != null && !this.likes.isEmpty()) {
			// the spec requires free-text matching against at minimum dmp title/description and dataset title/description;
			// multiple query values are combined with OR
			List<Query> likePredicates = new ArrayList<>();
			for (String likeItem : this.likes) {
				if (likeItem == null || likeItem.isBlank()) continue;
				String likeValue = likeItem;
				if (!likeValue.startsWith("*")) likeValue = "*" + likeValue;
				if (!likeValue.endsWith("*")) likeValue = likeValue + "*";
				ElasticFields elasticFields = this.elasticFieldsOf();
				elasticFields.add(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._title), null, true);
				elasticFields.add(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._description), null, true);
				likePredicates.add(this.like(elasticFields, List.of(likeValue), true, LogicalOperator.OR)._toQuery());
				likePredicates.add(this.nestedQuery(this.queryFactory.query(DatasetElasticQuery.class).like(likeItem).nestedPath(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._dataset))).build()._toQuery());
			}
			if (!likePredicates.isEmpty()) predicates.add(this.or(likePredicates)._toQuery());
		///Todo: Do we need like for an inner object as well

//			predicates.add(this.or(
//					this.like(elasticFields, List.of(this.like))._toQuery()
//					,
//					QueryBuilders.nested().path(DmpElasticEntity._collaborators).query(
//							this.like(elasticFields, List.of(this.like))._toQuery()
//					).build()._toQuery(),
//					QueryBuilders.nested().path(DmpElasticEntity._references).query(
//							this.like(elasticFields, List.of(this.like))._toQuery()
//					).build()._toQuery(),
//					QueryBuilders.nested().path(DmpElasticEntity._descriptions + "." + NestedDescriptionElasticEntity._references).query(
//							this.like(elasticFields, List.of(this.like))._toQuery()
//					).build()._toQuery(),
//					QueryBuilders.nested().path(DmpElasticEntity._descriptions + "." + NestedDescriptionElasticEntity._tags).query(
//							this.like(elasticFields, List.of(this.like))._toQuery()
//					).build()._toQuery(),
//					QueryBuilders.nested().path(DmpElasticEntity._descriptions).query(
//							this.like(elasticFields, List.of(this.like))._toQuery()
//					).build()._toQuery()
//
//			)._toQuery());
		}


		if (this.modifiedAfter != null) {
			predicates.add(this.dateGreaterThanQuery(this.elasticFieldOf(DmpElasticEntity._modified).disableInfer(true), this.modifiedAfter)._toQuery());
		}
		if (this.modifiedBefore != null) {
			predicates.add(this.dateLessThanQuery(this.elasticFieldOf(DmpElasticEntity._modified).disableInfer(true), this.modifiedBefore)._toQuery());
		}
		if (this.createdAfter != null) {
			predicates.add(this.dateGreaterThanQuery(this.elasticFieldOf(DmpElasticEntity._created).disableInfer(true), this.createdAfter)._toQuery());
		}
		if (this.createdBefore != null) {
			predicates.add(this.dateLessThanQuery(this.elasticFieldOf(DmpElasticEntity._created).disableInfer(true), this.createdBefore)._toQuery());
		}
		if (this.ethicalIssuesExistTypes != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DmpElasticEntity._ethicalIssuesExist).disableInfer(true), this.ethicalIssuesExistTypes.stream().map(EthicalIssuesExistType::getValue).toList())._toQuery());
		}
		if (this.languageTypes != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DmpElasticEntity._language).disableInfer(true), this.languageTypes.stream().map(LanguageType::getValue).toList())._toQuery());
		}
		if (this.dmpIdElasticQuery != null) {
			predicates.add(this.dmpIdElasticQuery.innerPath(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._dmpId)).applyFilters());
		}
		if (this.contactElasticQuery != null) {
			predicates.add(this.contactElasticQuery.innerPath(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._contact)).applyFilters());
		}
		if (this.contributorElasticQuery != null) {
			predicates.add(this.nestedQuery(this.contributorElasticQuery.nestedPath(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._contributor))).build()._toQuery());
		}
		for (DatasetElasticQuery datasetSubQuery : this.datasetElasticQueries) {
			predicates.add(this.nestedQuery(datasetSubQuery.nestedPath(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._dataset))).build()._toQuery());
		}
		for (ProjectElasticQuery projectSubQuery : this.projectElasticQueries) {
			predicates.add(this.nestedQuery(projectSubQuery.nestedPath(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._project))).build()._toQuery());
		}
		if (this.alternativeIdentifierElasticQuery != null) {
			predicates.add(this.nestedQuery(this.alternativeIdentifierElasticQuery.nestedPath(this.conventionService.asIndexer(this.getInnerPath(), DmpElasticEntity._alternateIdentifier))).build()._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public DmpElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		DmpElasticEntity mocDoc = new DmpElasticEntity();
		mocDoc.setAffiliation(this.convertNested(rawData, columns, this.queryFactory.query(AffiliationElasticQuery.class), DmpElasticEntity._affiliation, null));
		mocDoc.setAlternateIdentifier(this.convertNested(rawData, columns, this.queryFactory.query(AlternativeIdentifierElasticQuery.class), DmpElasticEntity._alternateIdentifier, null));
		if (columns.contains(DmpElasticEntity._created)) mocDoc.setCreated(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpElasticEntity._created), Date.class));
		if (columns.contains(DmpElasticEntity._description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpElasticEntity._description), String.class));
		if (columns.contains(DmpElasticEntity._ethicalIssuesDescription)) mocDoc.setEthicalIssuesDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpElasticEntity._ethicalIssuesDescription), String.class));
		if (columns.contains(DmpElasticEntity._ethicalIssuesExist)) mocDoc.setEthicalIssuesExist(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpElasticEntity._ethicalIssuesExist), EthicalIssuesExistType.class));
		if (columns.contains(DmpElasticEntity._ethicalIssuesReport)) mocDoc.setEthicalIssuesReport(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpElasticEntity._ethicalIssuesReport), String.class));
		if (columns.contains(DmpElasticEntity._language)) mocDoc.setLanguage(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpElasticEntity._language), LanguageType.class));
		if (columns.contains(DmpElasticEntity._modified)) mocDoc.setModified(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpElasticEntity._modified), Date.class));
		if (columns.contains(DmpElasticEntity._title)) mocDoc.setTitle(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpElasticEntity._title), String.class));
		mocDoc.setRelatedIdentifier(this.convertNested(rawData, columns, this.queryFactory.query(RelatedIdentifierElasticQuery.class), DmpElasticEntity._relatedIdentifier, null));
		mocDoc.setDmpId(this.convertInnerObject(rawData, columns, this.queryFactory.query(DmpIdElasticQuery.class), DmpElasticEntity._dmpId, null));
		mocDoc.setContact(this.convertInnerObject(rawData, columns, this.queryFactory.query(ContactElasticQuery.class), DmpElasticEntity._contact, null));
		mocDoc.setContributor(this.convertNested(rawData, columns, this.queryFactory.query(ContributorElasticQuery.class), DmpElasticEntity._contributor, null));
		mocDoc.setDataset(this.convertNested(rawData, columns, this.queryFactory.query(DatasetElasticQuery.class), DmpElasticEntity._dataset, null));
		mocDoc.setProject(this.convertNested(rawData, columns, this.queryFactory.query(ProjectElasticQuery.class), DmpElasticEntity._project, null));
		mocDoc.setCost(this.convertNested(rawData, columns, this.queryFactory.query(CostElasticQuery.class), DmpElasticEntity._cost, null));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.prefix(DmpElasticEntity._alternateIdentifier)) return this.queryFactory.query(AlternativeIdentifierElasticQuery.class).nestedPath(DmpElasticEntity._alternateIdentifier).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._alternateIdentifier));
		else if (item.prefix(DmpElasticEntity._affiliation)) return this.queryFactory.query(AffiliationElasticQuery.class).nestedPath(DmpElasticEntity._affiliation).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._affiliation));
		else if (item.match(DmpElasticEntity._created)) return this.elasticFieldOf(DmpElasticEntity._created).disableInfer(true);
		else if (item.match(DmpElasticEntity._description)) return this.elasticFieldOf(DmpElasticEntity._description);
		else if (item.match(DmpElasticEntity._ethicalIssuesDescription)) return this.elasticFieldOf(DmpElasticEntity._ethicalIssuesDescription);
		else if (item.match(DmpElasticEntity._ethicalIssuesExist)) return this.elasticFieldOf(DmpElasticEntity._ethicalIssuesExist);
		else if (item.match(DmpElasticEntity._ethicalIssuesReport)) return this.elasticFieldOf(DmpElasticEntity._ethicalIssuesReport);
		else if (item.match(DmpElasticEntity._language)) return this.elasticFieldOf(DmpElasticEntity._language).disableInfer(true);
		else if (item.match(DmpElasticEntity._modified)) return this.elasticFieldOf(DmpElasticEntity._modified).disableInfer(true);
		else if (item.prefix(DmpElasticEntity._relatedIdentifier)) return this.queryFactory.query(RelatedIdentifierElasticQuery.class).nestedPath(DmpElasticEntity._relatedIdentifier).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._relatedIdentifier));
		else if (item.match(DmpElasticEntity._title)) return this.elasticFieldOf(DmpElasticEntity._title).subfield(ElasticConstants.SubFields.keyword).disableInfer(true);
		else if (item.prefix(DmpElasticEntity._dmpId)) return this.queryFactory.query(DmpIdElasticQuery.class).innerPath(DmpElasticEntity._dmpId).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._dmpId));
		else if (item.prefix(DmpElasticEntity._contact)) return this.queryFactory.query(ContactElasticQuery.class).innerPath(DmpElasticEntity._contact).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._contact));
		else if (item.prefix(DmpElasticEntity._contributor)) return this.queryFactory.query(ContributorElasticQuery.class).nestedPath(DmpElasticEntity._contributor).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._contributor));
		else if (item.prefix(DmpElasticEntity._dataset)) return this.queryFactory.query(DatasetElasticQuery.class).nestedPath(DmpElasticEntity._dataset).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._dataset));
		else if (item.prefix(DmpElasticEntity._project)) return this.queryFactory.query(ProjectElasticQuery.class).nestedPath(DmpElasticEntity._project).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._project));
		else if (item.prefix(DmpElasticEntity._cost)) return this.queryFactory.query(CostElasticQuery.class).nestedPath(DmpElasticEntity._cost).fieldNameOf(this.extractPrefixed(item, DmpElasticEntity._cost));

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
	public DmpElasticQuery innerPath(String value) {
		this.innerPath = value;
		return this;
	}

	@Override
	protected String toKey(String key) {
		return key;
	}

	@Override
	protected ElasticField getKeyField() {
//		return this.elasticFieldOf(DmpElasticEntity._id);
		return null;
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		if (item.prefix(DmpElasticEntity._alternateIdentifier)) return this.queryFactory.query(AlternativeIdentifierElasticQuery.class).nestedPath(DmpElasticEntity._alternateIdentifier);
		else if (item.prefix(DmpElasticEntity._relatedIdentifier)) return this.queryFactory.query(RelatedIdentifierElasticQuery.class).nestedPath(DmpElasticEntity._relatedIdentifier);
		else if (item.prefix(DmpElasticEntity._contributor)) return this.queryFactory.query(ContributorElasticQuery.class).nestedPath(DmpElasticEntity._contributor);
		else if(item.prefix(DmpElasticEntity._dataset)) return this.queryFactory.query(DatasetElasticQuery.class).nestedPath(DmpElasticEntity._dataset);
		else if (item.prefix(DmpElasticEntity._project)) return this.queryFactory.query(ProjectElasticQuery.class).nestedPath(DmpElasticEntity._project);
		else if (item.prefix(DmpElasticEntity._cost)) return this.queryFactory.query(CostElasticQuery.class).nestedPath(DmpElasticEntity._cost);

		return null;
	}
}
