package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.fasterxml.jackson.databind.type.TypeFactory;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.enums.LanguageType;
import gr.cite.rda.common.enums.PersonalDataType;
import gr.cite.rda.common.enums.SensitiveDataType;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.*;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.ElasticField;
import gr.cite.tools.elastic.query.ElasticFields;
import gr.cite.tools.elastic.query.ElasticNestedQuery;
import gr.cite.tools.elastic.query.LogicalOperator;
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
public class DatasetElasticQuery extends ElasticNestedQuery<DatasetElasticQuery, DatasetElasticEntity, UUID> {

	private DatasetIdElasticQuery datasetIdElasticQuery;
	private MetadataElasticQuery metadataElasticQuery;
	private DistributionElasticQuery distributionElasticQuery;
	private Collection<PersonalDataType> personalDataTypes;
	private Collection<SensitiveDataType> sensitiveDataTypes;
	private String like;
	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public DatasetElasticQuery personalDataTypes(PersonalDataType value) {
		this.personalDataTypes = List.of(value);
		return this;
	}

	public DatasetElasticQuery personalDataTypes(PersonalDataType... value) {
		this.personalDataTypes = Arrays.asList(value);
		return this;
	}

	public DatasetElasticQuery personalDataTypes(Collection<PersonalDataType> values) {
		this.personalDataTypes = values;
		return this;
	}

	public DatasetElasticQuery sensitiveDataTypes(SensitiveDataType value) {
		this.sensitiveDataTypes = List.of(value);
		return this;
	}

	public DatasetElasticQuery sensitiveDataTypes(SensitiveDataType... value) {
		this.sensitiveDataTypes = Arrays.asList(value);
		return this;
	}

	public DatasetElasticQuery sensitiveDataTypes(Collection<SensitiveDataType> values) {
		this.sensitiveDataTypes = values;
		return this;
	}

	public DatasetElasticQuery like(String value) {
		this.like = value;
		return this;
	}

	public DatasetElasticQuery datasetIdElasticQuery(DatasetIdElasticQuery subQuery) {
		this.datasetIdElasticQuery = subQuery;
		return this;
	}

	public DatasetElasticQuery metadataElasticQuery(MetadataElasticQuery subQuery) {
		this.metadataElasticQuery = subQuery;
		return this;
	}

	public DatasetElasticQuery distributionElasticQuery(DistributionElasticQuery subQuery) {
		this.distributionElasticQuery = subQuery;
		return this;
	}

	public DatasetElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public DatasetElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;

	@Autowired
	public DatasetElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
        this.conventionService = conventionService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.personalDataTypes) || this.isEmpty(this.sensitiveDataTypes);
	}

	@Override
	protected Class<DatasetElasticEntity> entityClass() {
		return DatasetElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.datasetIdElasticQuery != null) {
			predicates.add(this.datasetIdElasticQuery.innerPath(this.conventionService.asIndexer(this.getNestedPath(), DatasetElasticEntity._datasetId)).applyFilters());
		}
		if (this.metadataElasticQuery != null) {
			predicates.add(this.nestedQuery(this.metadataElasticQuery.nestedPath(this.conventionService.asIndexer(this.getNestedPath(), DatasetElasticEntity._metadata))).build()._toQuery());
		}
		if (this.distributionElasticQuery != null) {
			predicates.add(this.nestedQuery(this.distributionElasticQuery.nestedPath(this.conventionService.asIndexer(this.getNestedPath(), DatasetElasticEntity._distribution))).build()._toQuery());
		}
		if (this.personalDataTypes != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DatasetElasticEntity._personalData).disableInfer(true), this.personalDataTypes.stream().map(PersonalDataType::getValue).toList())._toQuery());
		}
		if (this.sensitiveDataTypes != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DatasetElasticEntity._sensitiveData).disableInfer(true), this.sensitiveDataTypes.stream().map(SensitiveDataType::getValue).toList())._toQuery());
		}
		if (this.like != null && !this.like.isBlank()) {
			String likeValue = this.like;
			if (!likeValue.startsWith("*")) likeValue = "*" + likeValue;
			if (!likeValue.endsWith("*")) likeValue = likeValue + "*";
			ElasticFields elasticFields = this.elasticFieldsOf();
			elasticFields.add(this.conventionService.asIndexer(this.getNestedPath(), DatasetElasticEntity._title), null, true);
			elasticFields.add(this.conventionService.asIndexer(this.getNestedPath(), DatasetElasticEntity._description), null, true);
			predicates.add(this.like(elasticFields, List.of(likeValue), true, LogicalOperator.OR)._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public DatasetElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		DatasetElasticEntity mocDoc = new DatasetElasticEntity();
		mocDoc.setAlternateIdentifier(this.convertNested(rawData, columns, this.queryFactory.query(AlternativeIdentifierElasticQuery.class), DatasetElasticEntity._alternateIdentifier, null));
		mocDoc.setCreator(this.convertNested(rawData, columns, this.queryFactory.query(CreatorElasticQuery.class), DatasetElasticEntity._creator, null));
		if (columns.contains(DatasetElasticEntity._dataQualityAssurance)) mocDoc.setDataQualityAssurance(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._dataQualityAssurance), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class)));
		if (columns.contains(DatasetElasticEntity._description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._description), String.class));
		if (columns.contains(DatasetElasticEntity._isReused)) mocDoc.setIsReused(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._isReused), Boolean.class));
		if (columns.contains(DatasetElasticEntity._issued)) mocDoc.setIssued(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._issued), Date.class));
		if (columns.contains(DatasetElasticEntity._keyword)) mocDoc.setKeyword(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._keyword), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class)));
		if (columns.contains(DatasetElasticEntity._language)) mocDoc.setLanguage(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._language), LanguageType.class));
		if (columns.contains(DatasetElasticEntity._personalData)) mocDoc.setPersonalData(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._personalData), PersonalDataType.class));
		if (columns.contains(DatasetElasticEntity._preservationStatement)) mocDoc.setPreservationStatement(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._preservationStatement), String.class));
		if (columns.contains(DatasetElasticEntity._rights)) mocDoc.setRights(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._rights), String.class));
		mocDoc.setRelatedIdentifier(this.convertNested(rawData, columns, this.queryFactory.query(RelatedIdentifierElasticQuery.class), DatasetElasticEntity._relatedIdentifier, null));
		if (columns.contains(DatasetElasticEntity._sensitiveData)) mocDoc.setSensitiveData(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._sensitiveData), SensitiveDataType.class));
		if (columns.contains(DatasetElasticEntity._title)) mocDoc.setTitle(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._title), String.class));
		if (columns.contains(DatasetElasticEntity._type)) mocDoc.setType(FieldBasedMapper.shallowSafeConversion(rawData.get(DatasetElasticEntity._type), String.class));
		mocDoc.setDatasetId(this.convertInnerObject(rawData, columns, this.queryFactory.query(DatasetIdElasticQuery.class), DatasetElasticEntity._datasetId, null));
		mocDoc.setMetadata(this.convertNested(rawData, columns, this.queryFactory.query(MetadataElasticQuery.class), DatasetElasticEntity._metadata, null));
		mocDoc.setDistribution(this.convertNested(rawData, columns, this.queryFactory.query(DistributionElasticQuery.class), DatasetElasticEntity._distribution, null));
		mocDoc.setSecurityAndPrivacy(this.convertNested(rawData, columns, this.queryFactory.query(SecurityAndPrivacyElasticQuery.class), DatasetElasticEntity._securityAndPrivacy, null));
		mocDoc.setTechnicalResource(this.convertNested(rawData, columns, this.queryFactory.query(TechnicalResourceElasticQuery.class), DatasetElasticEntity._technicalResource, null));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.prefix(DatasetElasticEntity._alternateIdentifier)) return this.queryFactory.query(AlternativeIdentifierElasticQuery.class).nestedPath(DatasetElasticEntity._alternateIdentifier).fieldNameOf(this.extractPrefixed(item, DatasetElasticEntity._alternateIdentifier));
		else if (item.prefix(DatasetElasticEntity._creator)) return this.queryFactory.query(CreatorElasticQuery.class).nestedPath(DatasetElasticEntity._creator).fieldNameOf(this.extractPrefixed(item, DatasetElasticEntity._creator));
		else if (item.match(DatasetElasticEntity._dataQualityAssurance)) return this.elasticFieldOf(DatasetElasticEntity._dataQualityAssurance);
		else if (item.match(DatasetElasticEntity._description)) return this.elasticFieldOf(DatasetElasticEntity._description);
		else if (item.match(DatasetElasticEntity._isReused)) return this.elasticFieldOf(DatasetElasticEntity._isReused);
		else if (item.match(DatasetElasticEntity._issued)) return this.elasticFieldOf(DatasetElasticEntity._issued);
		else if (item.match(DatasetElasticEntity._keyword)) return this.elasticFieldOf(DatasetElasticEntity._keyword);
		else if (item.match(DatasetElasticEntity._language)) return this.elasticFieldOf(DatasetElasticEntity._language);
		else if (item.match(DatasetElasticEntity._personalData)) return this.elasticFieldOf(DatasetElasticEntity._personalData);
		else if (item.match(DatasetElasticEntity._preservationStatement)) return this.elasticFieldOf(DatasetElasticEntity._preservationStatement);
		else if (item.match(DatasetElasticEntity._rights)) return this.elasticFieldOf(DatasetElasticEntity._rights);
		else if (item.prefix(DatasetElasticEntity._relatedIdentifier)) return this.queryFactory.query(RelatedIdentifierElasticQuery.class).nestedPath(DatasetElasticEntity._relatedIdentifier).fieldNameOf(this.extractPrefixed(item, DatasetElasticEntity._relatedIdentifier));
		else if (item.match(DatasetElasticEntity._sensitiveData)) return this.elasticFieldOf(DatasetElasticEntity._sensitiveData);
		else if (item.match(DatasetElasticEntity._title)) return this.elasticFieldOf(DatasetElasticEntity._title);
		else if (item.match(DatasetElasticEntity._type)) return this.elasticFieldOf(DatasetElasticEntity._type);
		else if (item.prefix(DatasetElasticEntity._datasetId)) return this.queryFactory.query(DatasetIdElasticQuery.class).innerPath(DatasetElasticEntity._datasetId).fieldNameOf(this.extractPrefixed(item, DatasetElasticEntity._datasetId));
		else if (item.prefix(DatasetElasticEntity._metadata)) return this.queryFactory.query(MetadataElasticQuery.class).nestedPath(DatasetElasticEntity._metadata).fieldNameOf(this.extractPrefixed(item, DatasetElasticEntity._metadata));
		else if (item.prefix(DatasetElasticEntity._distribution)) return this.queryFactory.query(DistributionElasticQuery.class).nestedPath(DatasetElasticEntity._distribution).fieldNameOf(this.extractPrefixed(item, DatasetElasticEntity._distribution));
		else if (item.match(DatasetElasticEntity._securityAndPrivacy)) return this.queryFactory.query(SecurityAndPrivacyElasticQuery.class).nestedPath(DatasetElasticEntity._securityAndPrivacy).fieldNameOf(this.extractPrefixed(item, DatasetElasticEntity._securityAndPrivacy));
		else if (item.match(DatasetElasticEntity._technicalResource)) return this.queryFactory.query(TechnicalResourceElasticQuery.class).nestedPath(DatasetElasticEntity._technicalResource).fieldNameOf(this.extractPrefixed(item, DatasetElasticEntity._technicalResource));
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
		if (item.prefix(DatasetElasticEntity._alternateIdentifier)) return this.queryFactory.query(AlternativeIdentifierElasticQuery.class).nestedPath(DatasetElasticEntity._alternateIdentifier);
		else if (item.prefix(DatasetElasticEntity._creator)) return this.queryFactory.query(CreatorElasticQuery.class).nestedPath(DatasetElasticEntity._creator);
		else if (item.prefix(DatasetElasticEntity._relatedIdentifier)) return this.queryFactory.query(RelatedIdentifierElasticQuery.class).nestedPath(DatasetElasticEntity._relatedIdentifier);
		else if (item.prefix(DatasetElasticEntity._distribution)) return this.queryFactory.query(DistributionElasticQuery.class).nestedPath(DatasetElasticEntity._distribution);
		else if (item.prefix(DatasetElasticEntity._securityAndPrivacy)) return this.queryFactory.query(SecurityAndPrivacyElasticQuery.class).nestedPath(DatasetElasticEntity._securityAndPrivacy);
		else if (item.prefix(DatasetElasticEntity._technicalResource)) return this.queryFactory.query(TechnicalResourceElasticQuery.class).nestedPath(DatasetElasticEntity._technicalResource);

		return null;
	}
}
