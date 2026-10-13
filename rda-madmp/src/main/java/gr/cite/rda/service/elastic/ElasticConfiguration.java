package gr.cite.rda.service.elastic;


import gr.cite.rda.elastic.conventer.reading.*;
import gr.cite.rda.elastic.conventer.writing.*;
import gr.cite.tools.elastic.configuration.AbstractElasticConfiguration;
import gr.cite.tools.elastic.configuration.ElasticCertificateProvider;
import gr.cite.tools.elastic.configuration.ElasticProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

import java.util.List;

@Configuration
@EnableConfigurationProperties(ElasticProperties.class)
@ConditionalOnProperty(prefix = "elastic", name = "enabled", matchIfMissing = false)
public class ElasticConfiguration extends AbstractElasticConfiguration {

	public ElasticConfiguration(ElasticProperties elasticProperties, ElasticCertificateProvider elasticCertificateProvider) {
		super(elasticProperties, elasticCertificateProvider);
	}

	@Bean
	@Override
	public ElasticsearchCustomConversions elasticsearchCustomConversions() {
		return new ElasticsearchCustomConversions(
				List.of(
						new CertifiedWithTypeToStringConverter(),
						new CurrencyCodeTypeToStringConverter(),
						new DataAccessTypeToStringConverter(),
						new EthicalIssuesExistTypeToStringConverter(),
						new FundingStatusTypeToStringConverter(),
						new GeoLocationTypeToStringConverter(),
						new LanguageTypeToStringConverter(),
						new PersonalDataTypeToStringConverter(),
						new SensitiveDataTypeToStringConverter(),
						new SupportVersioningTypeToStringConverter(),
						new AccessTypeToShortConverter(),
						new StatusToShortConverter(),
						new ActionTypeToShortConverter(),
						new PlanUserRoleToShortConverter(),

						new StringToCertifiedWithTypeConverter(),
						new StringToCurrencyCodeTypeConverter(),
						new StringToDataAccessTypeConverter(),
						new StringToEthicalIssuesExistTypeConverter(),
						new StringToFundingStatusTypeConverter(),
						new StringToGeoLocationTypeConverter(),
						new StringToLanguageTypeConverter(),
						new StringToPersonalDataTypeConverter(),
						new StringToSensitiveDataTypeConverter(),
						new StringToSupportVersioningTypeConverter(),
						new ShortToAccessTypeConverter(),
						new ShortToStatusConverter(),
						new ShortToActionTypeConverter(),
						new ShortToPlanUserRoleConverter()
				));
	}
}
