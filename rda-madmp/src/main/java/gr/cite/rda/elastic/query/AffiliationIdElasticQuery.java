package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.scope.tenant.TenantScope;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.rda.elastic.data.AffiliationIdElasticEntity;
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
public class AffiliationIdElasticQuery extends ElasticInnerObjectQuery<AffiliationIdElasticQuery, AffiliationIdElasticEntity, String> {


	private String innerPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public AffiliationIdElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	private final QueryFactory queryFactory;
	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;
	private final UserScope userScope;
	private final TenantScope tenantScope;
	private final AuthorizationService authService;


	@Autowired
	public AffiliationIdElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, UserScope userScope, TenantScope tenantScope, AuthorizationService authService) {
		super(elasticsearchTemplate, elasticProperties);
		this.queryFactory = queryFactory;
		this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
		this.userScope = userScope;
		this.tenantScope = tenantScope;
		this.authService = authService;
	}

	@Override
	protected Boolean isFalseQuery() {
		return false;
	}

	@Override
	protected Class<AffiliationIdElasticEntity> entityClass() {
		return AffiliationIdElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		return null;
	}

	@Override
	public AffiliationIdElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		AffiliationIdElasticEntity mocDoc = new AffiliationIdElasticEntity();
		if (columns.contains(AffiliationIdElasticEntity._identifier)) mocDoc.setIdentifier(FieldBasedMapper.shallowSafeConversion(rawData.get(AffiliationIdElasticEntity._identifier), String.class));
		if (columns.contains(AffiliationIdElasticEntity._type)) mocDoc.setType(FieldBasedMapper.shallowSafeConversion(rawData.get(AffiliationIdElasticEntity._type), String.class));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(AffiliationIdElasticEntity._identifier)) return this.elasticFieldOf(AffiliationIdElasticEntity._identifier);
		else if (item.match(AffiliationIdElasticEntity._type)) return this.elasticFieldOf(AffiliationIdElasticEntity._type);
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
	public AffiliationIdElasticQuery innerPath(String value) {
		this.innerPath = value;
		return this;
	}

	@Override
	protected String toKey(String key) {
		return key;
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(AffiliationIdElasticEntity._identifier);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		return null;
	}
}
