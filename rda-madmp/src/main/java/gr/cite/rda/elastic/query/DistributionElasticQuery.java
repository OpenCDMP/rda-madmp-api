package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.fasterxml.jackson.databind.type.TypeFactory;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.enums.DataAccessType;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.*;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.ElasticConstants;
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
public class DistributionElasticQuery extends ElasticNestedQuery<DistributionElasticQuery, DistributionElasticEntity, UUID> {

	private LicenseElasticQuery licenseElasticQuery;
	private HostElasticQuery hostElasticQuery;
	private Collection<String> formats;
	private Collection<DataAccessType> dataAccessTypes;
	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public DistributionElasticQuery licenseElasticQuery(LicenseElasticQuery subQuery) {
		this.licenseElasticQuery = subQuery;
		return this;
	}

	public DistributionElasticQuery hostElasticQuery(HostElasticQuery subQuery) {
		this.hostElasticQuery = subQuery;
		return this;
	}

	public DistributionElasticQuery formats(String value) {
		this.formats = List.of(value);
		return this;
	}

	public DistributionElasticQuery formats(String... value) {
		this.formats = Arrays.asList(value);
		return this;
	}

	public DistributionElasticQuery formats(Collection<String> values) {
		this.formats = values;
		return this;
	}

	public DistributionElasticQuery dataAccessTypes(DataAccessType value) {
		this.dataAccessTypes = List.of(value);
		return this;
	}

	public DistributionElasticQuery dataAccessTypes(DataAccessType... value) {
		this.dataAccessTypes = Arrays.asList(value);
		return this;
	}

	public DistributionElasticQuery dataAccessTypes(Collection<DataAccessType> values) {
		this.dataAccessTypes = values;
		return this;
	}

	public DistributionElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public DistributionElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;

	@Autowired
	public DistributionElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
        this.conventionService = conventionService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.formats) || this.isEmpty(this.dataAccessTypes);
	}

	@Override
	protected Class<DistributionElasticEntity> entityClass() {
		return DistributionElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}

	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();


		if (this.licenseElasticQuery != null) {
			predicates.add(this.nestedQuery(this.licenseElasticQuery.nestedPath(this.conventionService.asIndexer(this.getNestedPath(), DistributionElasticEntity._license))).build()._toQuery());
		}
		if (this.hostElasticQuery != null) {
			predicates.add(this.hostElasticQuery.innerPath(this.conventionService.asIndexer(this.getNestedPath(), DistributionElasticEntity._host)).applyFilters());
		}
		if (this.formats != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DistributionElasticEntity._format).subfield(ElasticConstants.SubFields.keyword).disableInfer(true), this.formats)._toQuery());
		}
		if (this.dataAccessTypes != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DistributionElasticEntity._dataAccess).disableInfer(true), this.dataAccessTypes.stream().map(DataAccessType::getValue).toList())._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public DistributionElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		DistributionElasticEntity mocDoc = new DistributionElasticEntity();
		if (columns.contains(DistributionElasticEntity._accessUrl)) mocDoc.setAccessUrl(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._accessUrl), String.class));
		if (columns.contains(DistributionElasticEntity._availableUntil)) mocDoc.setAvailableUntil(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._availableUntil), Date.class));
		if (columns.contains(DistributionElasticEntity._byteSize)) mocDoc.setByteSize(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._byteSize), Long.class));
		if (columns.contains(DistributionElasticEntity._dataAccess)) mocDoc.setDataAccess(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._dataAccess), DataAccessType.class));
		if (columns.contains(DistributionElasticEntity._description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._description), String.class));
		if (columns.contains(DistributionElasticEntity._downloadUrl)) mocDoc.setDownloadUrl(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._downloadUrl), String.class));
		if (columns.contains(DistributionElasticEntity._format)) mocDoc.setFormat(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._format), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class)));
		if (columns.contains(DistributionElasticEntity._title)) mocDoc.setTitle(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._title), String.class));
		mocDoc.setLicense(this.convertNested(rawData, columns, this.queryFactory.query(LicenseElasticQuery.class), DistributionElasticEntity._license, null));
		mocDoc.setHost(this.convertInnerObject(rawData, columns, this.queryFactory.query(HostElasticQuery.class), DistributionElasticEntity._host, null));
		if (columns.contains(DistributionElasticEntity._issued)) mocDoc.setIssued(FieldBasedMapper.shallowSafeConversion(rawData.get(DistributionElasticEntity._issued), Date.class));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(DistributionElasticEntity._accessUrl)) return this.elasticFieldOf(DistributionElasticEntity._accessUrl);
		else if (item.match(DistributionElasticEntity._availableUntil)) return this.elasticFieldOf(DistributionElasticEntity._availableUntil);
		else if (item.match(DistributionElasticEntity._byteSize)) return this.elasticFieldOf(DistributionElasticEntity._byteSize);
		else if (item.match(DistributionElasticEntity._dataAccess)) return this.elasticFieldOf(DistributionElasticEntity._dataAccess);
		else if (item.match(DistributionElasticEntity._description)) return this.elasticFieldOf(DistributionElasticEntity._description);
		else if (item.match(DistributionElasticEntity._downloadUrl)) return this.elasticFieldOf(DistributionElasticEntity._downloadUrl);
		else if (item.match(DistributionElasticEntity._format)) return this.elasticFieldOf(DistributionElasticEntity._format);
		else if (item.match(DistributionElasticEntity._title)) return this.elasticFieldOf(DistributionElasticEntity._title);
		else if (item.prefix(DistributionElasticEntity._license)) return this.queryFactory.query(LicenseElasticQuery.class).nestedPath(DistributionElasticEntity._license).fieldNameOf(this.extractPrefixed(item, DistributionElasticEntity._license));
		else if (item.prefix(DistributionElasticEntity._host)) return this.queryFactory.query(HostElasticQuery.class).innerPath(DistributionElasticEntity._host).fieldNameOf(this.extractPrefixed(item, DistributionElasticEntity._host));
		else if (item.match(DistributionElasticEntity._issued)) return this.elasticFieldOf(DistributionElasticEntity._issued);
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
		if (item.prefix(DistributionElasticEntity._license)) return this.queryFactory.query(DistributionElasticQuery.class).nestedPath(DistributionElasticEntity._license);
		return null;
	}
}
