package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DmpElasticEntity;
import gr.cite.rda.elastic.data.LicenseElasticEntity;
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
import java.time.Instant;
import java.util.*;

@Component
//Like in C# make it Transient
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LicenseElasticQuery extends ElasticNestedQuery<LicenseElasticQuery, LicenseElasticEntity, UUID> {

	private Instant embargoAfter;
	private Instant embargoBefore;
	private Collection<String> licenseRefs;
	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public LicenseElasticQuery licenseRefs(String value) {
		this.licenseRefs = List.of(value);
		return this;
	}

	public LicenseElasticQuery licenseRefs(String... value) {
		this.licenseRefs = Arrays.asList(value);
		return this;
	}

	public LicenseElasticQuery licenseRefs(Collection<String> values) {
		this.licenseRefs = values;
		return this;
	}

	public LicenseElasticQuery embargoAfter(Instant value) {
		this.embargoAfter = value;
		return this;
	}

	public LicenseElasticQuery embargoBefore(Instant value) {
		this.embargoBefore = value;
		return this;
	}

	public LicenseElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public LicenseElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final ConventionService conventionService;

	@Autowired
	public LicenseElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, ConventionService conventionService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
        this.conventionService = conventionService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return this.isEmpty(this.licenseRefs);
	}

	@Override
	protected Class<LicenseElasticEntity> entityClass() {
		return LicenseElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.embargoAfter != null) {
			predicates.add(this.dateGreaterThanQuery(this.elasticFieldOf(LicenseElasticEntity._startDate).disableInfer(true), this.embargoAfter)._toQuery());
		}

		if (this.embargoBefore != null) {
			predicates.add(this.dateLessThanQuery(this.elasticFieldOf(LicenseElasticEntity._startDate).disableInfer(true), this.embargoBefore)._toQuery());
		}

		if (this.licenseRefs != null) {
			// the spec recommends normalising trailing slashes before matching, so match both variants
			Set<String> normalized = new HashSet<>();
			for (String licenseRef : this.licenseRefs) {
				if (licenseRef == null || licenseRef.isBlank()) continue;
				String stripped = licenseRef.endsWith("/") ? licenseRef.substring(0, licenseRef.length() - 1) : licenseRef;
				normalized.add(stripped);
				normalized.add(stripped + "/");
			}
			predicates.add(this.containsString(this.elasticFieldOf(LicenseElasticEntity._licenseRef).subfield(ElasticConstants.SubFields.keyword).disableInfer(true), normalized)._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public LicenseElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		LicenseElasticEntity mocDoc = new LicenseElasticEntity();
		if (columns.contains(LicenseElasticEntity._licenseRef)) mocDoc.setLicenseRef(FieldBasedMapper.shallowSafeConversion(rawData.get(LicenseElasticEntity._licenseRef), String.class));
		if (columns.contains(LicenseElasticEntity._startDate)) mocDoc.setStartDate(FieldBasedMapper.shallowSafeConversion(rawData.get(LicenseElasticEntity._startDate), Date.class));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(LicenseElasticEntity._licenseRef)) return this.elasticFieldOf(LicenseElasticEntity._licenseRef);
		else if (item.match(LicenseElasticEntity._startDate)) return this.elasticFieldOf(LicenseElasticEntity._startDate);
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
