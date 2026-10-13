package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.fasterxml.jackson.databind.type.TypeFactory;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.enums.*;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.HostElasticEntity;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.ElasticConstants;
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
public class HostElasticQuery extends ElasticInnerObjectQuery<HostElasticQuery, HostElasticEntity, UUID> {

	private Collection<String> titles;
	private HostIdElasticQuery hostIdElasticQuery;
	private String innerPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public HostElasticQuery titles(String value) {
		this.titles = List.of(value);
		return this;
	}

	public HostElasticQuery titles(String... value) {
		this.titles = Arrays.asList(value);
		return this;
	}

	public HostElasticQuery titles(Collection<String> values) {
		this.titles = values;
		return this;
	}

	public HostElasticQuery hostIdSubQuery(HostIdElasticQuery subQuery) {
		this.hostIdElasticQuery = subQuery;
		return this;
	}

	public HostElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;


	@Autowired
	public HostElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
		super(elasticsearchTemplate, elasticProperties);
        this.queryFactory = queryFactory;
        this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
		this.conventionService = conventionService;
	}

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.titles);
	}

	@Override
	protected Class<HostElasticEntity> entityClass() {
		return HostElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}

	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.titles != null) {
			predicates.add(this.containsString(this.elasticFieldOf(HostElasticEntity._title).subfield(ElasticConstants.SubFields.keyword).disableInfer(true), this.titles)._toQuery());
		}
		if (this.hostIdElasticQuery != null) {
			predicates.add(this.nestedQuery(this.hostIdElasticQuery.nestedPath(this.conventionService.asIndexer(this.getInnerPath(), HostElasticEntity._hostId))).build()._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public HostElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		HostElasticEntity mocDoc = new HostElasticEntity();
		if (columns.contains(HostElasticEntity._availability)) mocDoc.setAvailability(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._availability), String.class));
		if (columns.contains(HostElasticEntity._backupFrequency)) mocDoc.setBackupFrequency(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._backupFrequency), String.class));
		if (columns.contains(HostElasticEntity._backupType)) mocDoc.setBackupType(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._backupType), String.class));
		if (columns.contains(HostElasticEntity._certifiedWith)) mocDoc.setCertifiedWith(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._certifiedWith), CertifiedWithType.class));
		if (columns.contains(HostElasticEntity._description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._description), String.class));
		if (columns.contains(HostElasticEntity._geoLocation)) mocDoc.setGeoLocation(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._geoLocation), GeoLocationType.class));
		mocDoc.setHostId(this.convertNested(rawData, columns, this.queryFactory.query(HostIdElasticQuery.class), HostElasticEntity._hostId, null));
		if (columns.contains(HostElasticEntity._pidSystem)) mocDoc.setPidSystem(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._pidSystem), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class)));
		if (columns.contains(HostElasticEntity._storageType)) mocDoc.setStorageType(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._storageType), String.class));
		if (columns.contains(HostElasticEntity._supportsVersioning)) mocDoc.setSupportsVersioning(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._supportsVersioning), SupportVersioningType.class));
		if (columns.contains(HostElasticEntity._title)) mocDoc.setTitle(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._title), String.class));
		if (columns.contains(HostElasticEntity._url)) mocDoc.setUrl(FieldBasedMapper.shallowSafeConversion(rawData.get(HostElasticEntity._url), String.class));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(HostElasticEntity._availability)) return this.elasticFieldOf(HostElasticEntity._availability);
		else if (item.match(HostElasticEntity._backupFrequency)) return this.elasticFieldOf(HostElasticEntity._backupFrequency);
		else if (item.match(HostElasticEntity._backupType)) return this.elasticFieldOf(HostElasticEntity._backupType);
		else if (item.match(HostElasticEntity._certifiedWith)) return this.elasticFieldOf(HostElasticEntity._certifiedWith);
		else if (item.match(HostElasticEntity._description)) return this.elasticFieldOf(HostElasticEntity._description);
		else if (item.match(HostElasticEntity._geoLocation)) return this.elasticFieldOf(HostElasticEntity._geoLocation);
		else if (item.prefix(HostElasticEntity._hostId)) return this.queryFactory.query(HostIdElasticQuery.class).nestedPath(HostElasticEntity._hostId).fieldNameOf(this.extractPrefixed(item, HostElasticEntity._hostId));
		else if (item.match(HostElasticEntity._pidSystem)) return this.elasticFieldOf(HostElasticEntity._pidSystem);
		else if (item.match(HostElasticEntity._storageType)) return this.elasticFieldOf(HostElasticEntity._storageType);
		else if (item.match(HostElasticEntity._supportsVersioning)) return this.elasticFieldOf(HostElasticEntity._supportsVersioning);
		else if (item.match(HostElasticEntity._title)) return this.elasticFieldOf(HostElasticEntity._title);
		else if (item.match(HostElasticEntity._url)) return this.elasticFieldOf(HostElasticEntity._url);
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
	public HostElasticQuery innerPath(String value) {
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
		if (item.prefix(HostElasticEntity._hostId)) return this.queryFactory.query(HostIdElasticQuery.class).nestedPath(HostElasticEntity._hostId);
		return null;
	}
}
