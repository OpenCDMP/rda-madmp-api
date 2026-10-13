package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.common.scope.tenant.TenantScope;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.rda.elastic.data.DmpIdElasticEntity;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import gr.cite.tools.elastic.mapper.FieldBasedMapper;
import gr.cite.tools.elastic.query.*;
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
public class DmpIdElasticQuery extends ElasticInnerObjectQuery<DmpIdElasticQuery, DmpIdElasticEntity, String> {

	private Collection<String> identifiers;
	private Collection<String> dmpIdTypes;
	private String innerPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public DmpIdElasticQuery identifiers(String value) {
		this.identifiers = List.of(value);
		return this;
	}

	public DmpIdElasticQuery identifiers(String... value) {
		this.identifiers = Arrays.asList(value);
		return this;
	}

	public DmpIdElasticQuery identifiers(Collection<String> values) {
		this.identifiers = values;
		return this;
	}

	public DmpIdElasticQuery accessTypes(String value) {
		this.dmpIdTypes = List.of(value);
		return this;
	}

	public DmpIdElasticQuery accessTypes(String... value) {
		this.dmpIdTypes = Arrays.asList(value);
		return this;
	}

	public DmpIdElasticQuery accessTypes(Collection<String> values) {
		this.dmpIdTypes = values;
		return this;
	}

	public DmpIdElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
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
	public DmpIdElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, QueryFactory queryFactory, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService, UserScope userScope, TenantScope tenantScope, AuthorizationService authService) {
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
		return this.isEmpty(this.dmpIdTypes);
	}

	@Override
	protected Class<DmpIdElasticEntity> entityClass() {
		return DmpIdElasticEntity.class;
	}


	@Override
	protected Query applyAuthZ() {
		return null;
	}
	
	@Override
	protected Query applyFilters() {
		List<Query> predicates = new ArrayList<>();

		if (this.identifiers != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DmpIdElasticEntity._identifier).disableInfer(true), this.identifiers)._toQuery());
		}

		if (this.dmpIdTypes != null) {
			predicates.add(this.containsString(this.elasticFieldOf(DmpIdElasticEntity._type).disableInfer(true), this.dmpIdTypes)._toQuery());
		}

		if (!predicates.isEmpty()) {
			return this.and(predicates);
		} else {
			return null;
		}
	}

	@Override
	public DmpIdElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		DmpIdElasticEntity mocDoc = new DmpIdElasticEntity();
		if (columns.contains(DmpIdElasticEntity._identifier)) mocDoc.setIdentifier(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpIdElasticEntity._identifier), String.class));
		if (columns.contains(DmpIdElasticEntity._type)) mocDoc.setType(FieldBasedMapper.shallowSafeConversion(rawData.get(DmpIdElasticEntity._type), String.class));


		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(DmpIdElasticEntity._identifier)) return this.elasticFieldOf(DmpIdElasticEntity._identifier);
		else if (item.match(DmpIdElasticEntity._type)) return this.elasticFieldOf(DmpIdElasticEntity._type);
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
	public DmpIdElasticQuery innerPath(String value) {
		this.innerPath = value;
		return this;
	}

	@Override
	protected String toKey(String key) {
		return key;
	}

	@Override
	protected ElasticField getKeyField() {
		return this.elasticFieldOf(DmpIdElasticEntity._identifier);
	}

	@Override
	protected ElasticNestedQuery<?, ?, ?> nestedQueryOf(FieldResolver item) {
		return null;
	}
}
