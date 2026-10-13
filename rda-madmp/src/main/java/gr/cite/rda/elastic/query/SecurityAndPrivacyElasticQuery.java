package gr.cite.rda.elastic.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.elastic.data.SecurityAndPrivacyElasticEntity;
import gr.cite.rda.service.elastic.AppElasticConfiguration;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.tools.data.query.FieldResolver;
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
public class SecurityAndPrivacyElasticQuery extends ElasticNestedQuery<SecurityAndPrivacyElasticQuery, SecurityAndPrivacyElasticEntity, UUID> {

	private String nestedPath;

	private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

	public SecurityAndPrivacyElasticQuery authorize(EnumSet<AuthorizationFlags> values) {
		this.authorize = values;
		return this;
	}

	@Override
	public SecurityAndPrivacyElasticQuery nestedPath(String value) {
		this.nestedPath = value;
		return this;
	}

	private final AppElasticConfiguration appElasticConfiguration;
	private final ElasticService elasticService;

	@Autowired
	public SecurityAndPrivacyElasticQuery(ElasticsearchTemplate elasticsearchTemplate, ElasticProperties elasticProperties, AppElasticConfiguration appElasticConfiguration, ElasticService elasticService) {
		super(elasticsearchTemplate, elasticProperties);
		this.appElasticConfiguration = appElasticConfiguration;
		this.elasticService = elasticService;
    }

	@Override
	protected Boolean isFalseQuery() {
		return false;
	}

	@Override
	protected Class<SecurityAndPrivacyElasticEntity> entityClass() {
		return SecurityAndPrivacyElasticEntity.class;
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
	public SecurityAndPrivacyElasticEntity convert(Map<String, Object> rawData, Set<String> columns) {
		SecurityAndPrivacyElasticEntity mocDoc = new SecurityAndPrivacyElasticEntity();
		if (columns.contains(SecurityAndPrivacyElasticEntity._title)) mocDoc.setTitle(FieldBasedMapper.shallowSafeConversion(rawData.get(SecurityAndPrivacyElasticEntity._title), String.class));
		if (columns.contains(SecurityAndPrivacyElasticEntity._description)) mocDoc.setDescription(FieldBasedMapper.shallowSafeConversion(rawData.get(SecurityAndPrivacyElasticEntity._description), String.class));

		return mocDoc;
	}

	@Override
	protected ElasticField fieldNameOf(FieldResolver item) {
		if (item.match(SecurityAndPrivacyElasticEntity._title)) return this.elasticFieldOf(SecurityAndPrivacyElasticEntity._title);
		else if (item.match(SecurityAndPrivacyElasticEntity._description)) return this.elasticFieldOf(SecurityAndPrivacyElasticEntity._description);
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
