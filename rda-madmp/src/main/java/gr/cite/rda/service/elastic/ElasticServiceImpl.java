package gr.cite.rda.service.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.*;
import gr.cite.commons.web.authz.service.AuthorizationService;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.authorization.Permission;
import gr.cite.rda.authorization.authorizationcontentresolver.AuthorizationContentResolver;
import gr.cite.rda.common.enums.ActionType;
import gr.cite.rda.common.exception.BadRequestException;
import gr.cite.rda.common.exception.DmpStaleException;
import gr.cite.rda.common.scope.user.UserScope;
import gr.cite.rda.elastic.data.*;
import gr.cite.rda.elastic.query.DmpRootElasticQuery;
import gr.cite.rda.integrationevent.outbox.dmptouch.DmpTouchedIntegrationEvent;
import gr.cite.rda.integrationevent.outbox.dmptouch.DmpTouchedIntegrationEventHandler;
import gr.cite.rda.model.builder.DmpRootBuilder;
import gr.cite.rda.service.rdadmp.RdaDmpServiceProperties;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.elastic.ElasticConstants;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.opencdmp.commonmodels.enums.PlanUserRole;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ElasticServiceImpl implements ElasticService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(ElasticServiceImpl.class));
	public final AppElasticConfiguration appElasticConfiguration;
	private final ElasticsearchClient restHighLevelClient;
	private final ElasticsearchTemplate elasticsearchTemplate;
	private final MessageSource messageSource;
	private final AuthorizationService authorizationService;
	private final AuthorizationContentResolver authorizationContentResolver;
	private final QueryFactory queryFactory;
	private final UserScope userScope;
	private final BuilderFactory builderFactory;
	private final DmpTouchedIntegrationEventHandler dmpTouchedIntegrationEventHandler;
	private final RdaDmpServiceProperties rdaDmpServiceProperties;

	public ElasticServiceImpl(AppElasticConfiguration appElasticConfiguration, ElasticsearchClient restHighLevelClient, ElasticsearchTemplate elasticsearchTemplate, MessageSource messageSource, AuthorizationService authorizationService, AuthorizationContentResolver authorizationContentResolver, QueryFactory queryFactory, UserScope userScope, BuilderFactory builderFactory, DmpTouchedIntegrationEventHandler dmpTouchedIntegrationEventHandler, RdaDmpServiceProperties rdaDmpServiceProperties) {
		this.appElasticConfiguration = appElasticConfiguration;
		this.restHighLevelClient = restHighLevelClient;
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.messageSource = messageSource;
		this.authorizationService = authorizationService;
        this.authorizationContentResolver = authorizationContentResolver;
        this.queryFactory = queryFactory;
        this.userScope = userScope;
        this.builderFactory = builderFactory;
        this.dmpTouchedIntegrationEventHandler = dmpTouchedIntegrationEventHandler;
        this.rdaDmpServiceProperties = rdaDmpServiceProperties;
    }

	@Override
	public boolean existsRdaIndex() throws IOException {
		return this.restHighLevelClient.indices().exists(new ExistsRequest.Builder().index(this.appElasticConfiguration.getAppElasticProperties().getRdaIndexName()).includeDefaults(true).build()).value();
	}

	//region ensure index

	@Override
	public void ensureRdaIndex() throws IOException {
		boolean exists = this.existsRdaIndex();
		if (exists) return ;

		this.ensureIndex(this.appElasticConfiguration.getAppElasticProperties().getRdaIndexName(), this.createDmpRootPropertyMap());
	}

	private void ensureIndex(String indexName, Map<String, Property> propertyMap) throws IOException {
		TypeMapping.Builder typeMapping = new TypeMapping.Builder();
		typeMapping.properties(propertyMap);

		IndexSettings.Builder indexSettings = new IndexSettings.Builder();
		IndexSettingsAnalysis.Builder indexSettingsAnalysis = new IndexSettingsAnalysis.Builder();
		indexSettingsAnalysis.filter("english_stemmer", ((tf) -> tf.definition(tfdb -> tfdb.stemmer(stemmerBuilder -> stemmerBuilder.language("english")))))
				.filter("english_stop", tf -> tf.definition(tfdb -> tfdb.stop(stopTokenBuilder -> stopTokenBuilder)));

		if (this.appElasticConfiguration.getAppElasticProperties().isEnableIcuAnalysisPlugin()){
			indexSettingsAnalysis.analyzer("icu_analyzer_text", ab -> ab.custom(x-> x.filter("icu_folding", "english_stop", "english_stemmer").tokenizer("icu_tokenizer")));
		} else {
			indexSettingsAnalysis.analyzer("icu_analyzer_text",  ab -> ab.custom(x-> x.filter("icu_folding", "english_stop", "english_stemmer").tokenizer("standard")));
		}
		indexSettings.analysis(indexSettingsAnalysis.build());
		this.restHighLevelClient.indices().create(new CreateIndexRequest.Builder().index(indexName).mappings(typeMapping.build()).settings(indexSettings.build()).build());

	}

	private Map<String, Property> createDmpRootPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(DmpRootElasticEntity._id, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DmpRootElasticEntity._dmp, new Property.Builder().object(x -> x.properties(this.createDmpTemplatePropertyMap())).build());
		propertyMap.put(DmpRootElasticEntity._accessType, this.createElastic(FieldType.Short, false));
		propertyMap.put(DmpRootElasticEntity._status, this.createElastic(FieldType.Short, false));
		propertyMap.put(DmpRootElasticEntity._tenantId, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DmpRootElasticEntity._users, new Property.Builder().nested(x -> x.properties(this.createNestedDmpUsersPropertyMap())).build());
		propertyMap.put(DmpRootElasticEntity._actionType, this.createElastic(FieldType.Short, false));

		return propertyMap;
	}

	private Map<String, Property> createNestedDmpUsersPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(DmpUserElasticEntity._userId, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DmpUserElasticEntity._role, this.createElastic(FieldType.Short, false));

		return propertyMap;
	}

	private Map<String, Property> createDmpTemplatePropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(DmpElasticEntity._affiliation, new Property.Builder().nested(x -> x.properties(this.createAffiliationPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._alternateIdentifier, new Property.Builder().nested(x -> x.properties(this.createAlternativeIdentifierPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._contact, new Property.Builder().object(x -> x.properties(this.createContactElasticEntityPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._contributor, new Property.Builder().nested(x -> x.properties(this.createContributorElasticEntityPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._cost, new Property.Builder().nested(x -> x.properties(this.createCostElasticEntityPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._created, this.createElastic(FieldType.Date, false));
		propertyMap.put(DmpElasticEntity._dataset, new Property.Builder().nested(x -> x.properties(this.createDatasetElasticEntityPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._description, this.createElastic(FieldType.Text, false));
		propertyMap.put(DmpElasticEntity._dmpId, new Property.Builder().object(x -> x.properties(this.createDmpIdElasticEntityPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._ethicalIssuesDescription, this.createElastic(FieldType.Text, false));
		propertyMap.put(DmpElasticEntity._ethicalIssuesExist, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DmpElasticEntity._ethicalIssuesReport, this.createElastic(FieldType.Text, false));
		propertyMap.put(DmpElasticEntity._language, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DmpElasticEntity._modified, this.createElastic(FieldType.Date, false));
		propertyMap.put(DmpElasticEntity._project, new Property.Builder().nested(x -> x.properties(this.createProjectElasticEntityPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._relatedIdentifier, new Property.Builder().nested(x -> x.properties(this.createRelatedIdentifierPropertyMap())).build());
		propertyMap.put(DmpElasticEntity._title, this.createElastic(FieldType.Text, true));

		return propertyMap;
	}

	private Map<String, Property> createAlternativeIdentifierPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(AlternativeIdentifierElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(AlternativeIdentifierElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createRelatedIdentifierPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(RelatedIdentifierElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(RelatedIdentifierElasticEntity._metadataScheme, this.createElastic(FieldType.Text, false));
		propertyMap.put(RelatedIdentifierElasticEntity._relationType, this.createElastic(FieldType.Text, false));
		propertyMap.put(RelatedIdentifierElasticEntity._resourceType, this.createElastic(FieldType.Text, false));
		propertyMap.put(RelatedIdentifierElasticEntity._schemeType, this.createElastic(FieldType.Text, false));
		propertyMap.put(RelatedIdentifierElasticEntity._schemeUri, this.createElastic(FieldType.Text, false));
		propertyMap.put(RelatedIdentifierElasticEntity._type, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createAffiliationPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(AffiliationElasticEntity._affiliationId, new Property.Builder().object(x -> x.properties(this.createAffiliationIdPropertyMap())).build());
		propertyMap.put(AffiliationElasticEntity._name, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createAffiliationIdPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(AffiliationIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(AffiliationIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createProjectElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(ProjectElasticEntity._description, this.createElastic(FieldType.Text, false));
		propertyMap.put(ProjectElasticEntity._end, this.createElastic(FieldType.Date, false));
		propertyMap.put(ProjectElasticEntity._funding, new Property.Builder().nested(x -> x.properties(this.createFundingElasticEntityPropertyMap())).build());
		propertyMap.put(ProjectElasticEntity._projectId, new Property.Builder().nested(x -> x.properties(this.createProjectIdPropertyMap())).build());
		propertyMap.put(ProjectElasticEntity._start, this.createElastic(FieldType.Date, false));
		propertyMap.put(ProjectElasticEntity._title, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createProjectIdPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(ProjectIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(ProjectIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createFundingElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(FundingElasticEntity._funderId, new Property.Builder().object(x -> x.properties(this.createFunderIdElasticEntityPropertyMap())).build());
		propertyMap.put(FundingElasticEntity._fundingStatus, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(FundingElasticEntity._grantId, new Property.Builder().object(x -> x.properties(this.createGrantIdElasticEntityPropertyMap())).build());

		return propertyMap;
	}

	private Map<String, Property> createGrantIdElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(GrantIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(GrantIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createFunderIdElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(FunderIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(FunderIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createDmpIdElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(DmpIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DmpIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createContactElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(ContactElasticEntity._affiliation, new Property.Builder().nested(x -> x.properties(this.createAffiliationPropertyMap())).build());
		propertyMap.put(ContactElasticEntity._contactId, new Property.Builder().nested(x -> x.properties(this.createContactIdElasticEntityPropertyMap())).build());
		propertyMap.put(ContactElasticEntity._mbox, this.createElastic(FieldType.Text, false));
		propertyMap.put(ContactElasticEntity._name, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createContactIdElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(ContactIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(ContactIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createContributorElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(ContributorElasticEntity._affiliation, new Property.Builder().nested(x -> x.properties(this.createAffiliationPropertyMap())).build());
		propertyMap.put(ContributorElasticEntity._contributorId, new Property.Builder().nested(x -> x.properties(this.createContributorIdElasticEntityPropertyMap())).build());
		propertyMap.put(ContributorElasticEntity._mbox, this.createElastic(FieldType.Text, false));
		propertyMap.put(ContributorElasticEntity._name, this.createElastic(FieldType.Text, false));
		propertyMap.put(ContributorElasticEntity._role, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createContributorIdElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(ContributorIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(ContributorIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createCostElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(CostElasticEntity._currencyCode, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(CostElasticEntity._description, this.createElastic(FieldType.Text, false));
		propertyMap.put(CostElasticEntity._title, this.createElastic(FieldType.Text, false));
		propertyMap.put(CostElasticEntity._value, this.createElastic(FieldType.Double, false));

		return propertyMap;
	}

	private Map<String, Property> createDatasetElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(DatasetElasticEntity._alternateIdentifier, new Property.Builder().nested(x -> x.properties(this.createAlternativeIdentifierPropertyMap())).build());
		propertyMap.put(DatasetElasticEntity._creator, new Property.Builder().nested(x -> x.properties(this.createCreatorMap())).build());
		propertyMap.put(DatasetElasticEntity._dataQualityAssurance, this.createElastic(FieldType.Text, false));
		propertyMap.put(DatasetElasticEntity._datasetId, new Property.Builder().object(x -> x.properties(this.createDatasetIdElasticEntityPropertyMap())).build());
		propertyMap.put(DatasetElasticEntity._description, this.createElastic(FieldType.Text, false));
		propertyMap.put(DatasetElasticEntity._distribution, new Property.Builder().nested(x -> x.properties(this.createDistributionElasticEntityPropertyMap())).build());
		propertyMap.put(DatasetElasticEntity._isReused, this.createElastic(FieldType.Boolean, false));
		propertyMap.put(DatasetElasticEntity._issued, this.createElastic(FieldType.Date, false));
		propertyMap.put(DatasetElasticEntity._keyword, this.createElastic(FieldType.Text, false));
		propertyMap.put(DatasetElasticEntity._language, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DatasetElasticEntity._metadata, new Property.Builder().nested(x -> x.properties(this.createMetadataElasticEntityPropertyMap())).build());
		propertyMap.put(DatasetElasticEntity._personalData, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DatasetElasticEntity._preservationStatement, this.createElastic(FieldType.Text, false));
		propertyMap.put(DatasetElasticEntity._rights, this.createElastic(FieldType.Text, false));
		propertyMap.put(DatasetElasticEntity._relatedIdentifier, new Property.Builder().nested(x -> x.properties(this.createRelatedIdentifierPropertyMap())).build());
		propertyMap.put(DatasetElasticEntity._securityAndPrivacy, new Property.Builder().nested(x -> x.properties(this.createSecurityAndPrivacyElasticEntityPropertyMap())).build());
		propertyMap.put(DatasetElasticEntity._sensitiveData, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DatasetElasticEntity._technicalResource, new Property.Builder().nested(x -> x.properties(this.createTechnicalResourceElasticEntityPropertyMap())).build());
		propertyMap.put(DatasetElasticEntity._title, this.createElastic(FieldType.Text, false));
		propertyMap.put(DatasetElasticEntity._type, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createCreatorMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(CreatorElasticEntity._affiliation, new Property.Builder().nested(x -> x.properties(this.createAffiliationPropertyMap())).build());
		propertyMap.put(CreatorElasticEntity._creatorId, new Property.Builder().nested(x -> x.properties(this.createCreatorIdPropertyMap())).build());
		propertyMap.put(CreatorElasticEntity._mbox, this.createElastic(FieldType.Text, false));
		propertyMap.put(CreatorElasticEntity._name, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createCreatorIdPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(CreatorIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(CreatorIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createTechnicalResourceElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(TechnicalResourceElasticEntity._description, this.createElastic(FieldType.Text, false));
		propertyMap.put(TechnicalResourceElasticEntity._name, this.createElastic(FieldType.Text, false));
		propertyMap.put(TechnicalResourceElasticEntity._technicalResourceId, new Property.Builder().nested(x -> x.properties(this.createTechnicalResourceIdPropertyMap())).build());

		return propertyMap;
	}

	private Map<String, Property> createTechnicalResourceIdPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(TechnicalResourceIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(TechnicalResourceIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createSecurityAndPrivacyElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(SecurityAndPrivacyElasticEntity._title, this.createElastic(FieldType.Text, false));
		propertyMap.put(SecurityAndPrivacyElasticEntity._description, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createMetadataElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(MetadataElasticEntity._description, this.createElastic(FieldType.Text, false));
		propertyMap.put(MetadataElasticEntity._language, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(MetadataElasticEntity._metadataStandardId, new Property.Builder().nested(x -> x.properties(this.createStandardIdElasticEntityPropertyMap())).build());

		return propertyMap;
	}

	private Map<String, Property> createStandardIdElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(MetadataStandardIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(MetadataStandardIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createDatasetIdElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(DatasetIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DatasetIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createDistributionElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(DistributionElasticEntity._accessUrl, this.createElastic(FieldType.Text, false));
		propertyMap.put(DistributionElasticEntity._availableUntil, this.createElastic(FieldType.Date, false));
		propertyMap.put(DistributionElasticEntity._byteSize, this.createElastic(FieldType.Long, false));
		propertyMap.put(DistributionElasticEntity._dataAccess, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(DistributionElasticEntity._description, this.createElastic(FieldType.Text, false));
		propertyMap.put(DistributionElasticEntity._downloadUrl, this.createElastic(FieldType.Text, false));
		propertyMap.put(DistributionElasticEntity._format, this.createElastic(FieldType.Text, true));
		propertyMap.put(DistributionElasticEntity._host, new Property.Builder().object(x -> x.properties(this.createHostElasticEntityPropertyMap())).build());
		propertyMap.put(DistributionElasticEntity._issued, this.createElastic(FieldType.Date, false));
		propertyMap.put(DistributionElasticEntity._license, new Property.Builder().nested(x -> x.properties(this.createLicenseElasticEntityPropertyMap())).build());
		propertyMap.put(DistributionElasticEntity._title, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createHostElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(HostElasticEntity._availability, this.createElastic(FieldType.Text, false));
		propertyMap.put(HostElasticEntity._backupFrequency, this.createElastic(FieldType.Text, false));
		propertyMap.put(HostElasticEntity._backupType, this.createElastic(FieldType.Text, false));
		propertyMap.put(HostElasticEntity._certifiedWith, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(HostElasticEntity._description, this.createElastic(FieldType.Text, false));
		propertyMap.put(HostElasticEntity._geoLocation, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(HostElasticEntity._hostId, new Property.Builder().nested(x -> x.properties(this.createHostIdIdPropertyMap())).build());
		propertyMap.put(HostElasticEntity._pidSystem, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(HostElasticEntity._storageType, this.createElastic(FieldType.Text, false));
		propertyMap.put(HostElasticEntity._supportsVersioning, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(HostElasticEntity._title, this.createElastic(FieldType.Text, true));
		propertyMap.put(HostElasticEntity._url, this.createElastic(FieldType.Text, false));

		return propertyMap;
	}

	private Map<String, Property> createHostIdIdPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(HostIdElasticEntity._identifier, this.createElastic(FieldType.Keyword, false));
		propertyMap.put(HostIdElasticEntity._type, this.createElastic(FieldType.Keyword, false));

		return propertyMap;
	}

	private Map<String, Property> createLicenseElasticEntityPropertyMap(){
		Map<String, Property> propertyMap = new HashMap<>();
		propertyMap.put(LicenseElasticEntity._licenseRef, this.createElastic(FieldType.Text, true));
		propertyMap.put(LicenseElasticEntity._startDate, this.createElastic(FieldType.Date, false));

		return propertyMap;
	}

	private Property createElastic(FieldType fieldType, boolean hasKeywordSubField){
		switch (fieldType){
			case Keyword -> {
				return new Property.Builder().keyword(x -> x).build();
			}
			case Text -> {
				return hasKeywordSubField ? new Property.Builder().text(x -> x.analyzer("icu_analyzer_text").fields(ElasticConstants.SubFields.keyword, y -> y.keyword(z-> z))).build() : new Property.Builder().text(x -> x).build();
			}
			case Date -> {
				return new Property.Builder().date(x -> x).build();
			}
			case Short -> {
				return new Property.Builder().short_(x -> x).build();
			}
			case Boolean -> {
				return new Property.Builder().boolean_(x -> x).build();
			}
			case Double -> {
				return new Property.Builder().double_(x -> x).build();
			}
			case Long -> {
				return new Property.Builder().long_(x -> x).build();
			}
			default -> throw new RuntimeException();
		}
	}
	
	//endregion

	@Override
	public DmpRootElasticEntity getRda(String id) throws IOException {
		this.ensureRdaIndex();

		DmpRootElasticQuery query = this.queryFactory.query(DmpRootElasticQuery.class).authorize(AuthorizationFlags.All).ids(id);
		if (this.rdaDmpServiceProperties.isUseActionType()) query.actionTypes(ActionType.None);

		DmpRootElasticEntity dmpElasticEntity = query.first();

		if(dmpElasticEntity == null) throw new MyNotFoundException(this.messageSource.getMessage("General_ItemNotFound", new Object[]{id, DmpRootElasticEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		return dmpElasticEntity;
	}
	
	//region persist delete

	@Override
	public DmpRootElasticEntity persistRda(DmpRootElasticEntity dmp, String id, boolean isFromQueue, String ifMatch, String ifModifiedMatch) throws IOException {
		this.ensureRdaIndex();

		if (!isFromQueue) {
			if (id != null && !id.isBlank()) {
				DmpRootElasticEntity dmpRootElasticEntity = this.elasticsearchTemplate.get(id, DmpRootElasticEntity.class, IndexCoordinates.of(this.appElasticConfiguration.getAppElasticProperties().getRdaIndexName()));
				if (dmpRootElasticEntity == null) throw new MyNotFoundException(this.messageSource.getMessage("General_ItemNotFound", new Object[]{id, DmpRootElasticEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));

				this.authorizationService.authorizeAtLeastOneForce(List.of(this.authorizationContentResolver.dmpAffiliation(id)), Permission.EditRdaDmp);
				this.checkModifiedConflict(dmpRootElasticEntity, ifMatch, ifModifiedMatch);

				dmp.setId(dmpRootElasticEntity.getId());
				dmp.setUsers(dmpRootElasticEntity.getUsers());
				dmp.setStatus(dmpRootElasticEntity.getStatus());
				dmp.setTenantId(dmpRootElasticEntity.getTenantId());
				if (dmpRootElasticEntity.getActionType() != null && dmpRootElasticEntity.getActionType().equals(ActionType.None)) {
					// dmp is created from queue so need update to opencdmp
					dmp.setActionType(ActionType.UpdatePlan);
				} else {
					dmp.setActionType(dmpRootElasticEntity.getActionType());
				}
			} else {
				this.authorizationService.authorizeForce(Permission.NewRdaDmp);
				dmp.setId(UUID.randomUUID().toString());
				if(dmp.getDmp() != null && dmp.getDmp().getCreated() == null) dmp.getDmp().setCreated(Date.from(Instant.now()));

				dmp.setUsers(new ArrayList<>());
				DmpUserElasticEntity user = new DmpUserElasticEntity();
				user.setUserId(this.userScope.getUserIdSafe());
				user.setRole(PlanUserRole.Owner);
				dmp.getUsers().add(user);

				// new dmp so create plan to opencdmp
				dmp.setActionType(ActionType.CreatePlan);
			}
			this.createDmpTouchedIntegrationEvent(dmp);
		} else {
			//coming from queue so none action types
			dmp.setActionType(ActionType.None);
			if(dmp.getDmp() != null && dmp.getDmp().getCreated() == null) dmp.getDmp().setCreated(Date.from(Instant.now()));
		}

		if(dmp.getDmp() != null && dmp.getDmp().getModified() == null)  dmp.getDmp().setModified(Date.from(Instant.now()));

		this.elasticsearchTemplate.save(dmp, IndexCoordinates.of(this.appElasticConfiguration.getAppElasticProperties().getRdaIndexName()));

		return dmp;
	}

	private void checkModifiedConflict(DmpRootElasticEntity entity, String ifMatch, String ifModifiedMatch) {

		// RFC 9110 13.2.2: If-Unmodified-Since is ignored when If-Match is present; no ETag is emitted, so only * matches
		if (ifMatch != null && !ifMatch.isBlank()) {
			if (!ifMatch.trim().equals("*")) throw new DmpStaleException(this.messageSource.getMessage("Precondition_Failed", new Object[]{"If-Match"}, LocaleContextHolder.getLocale()));
			return;
		}

		if (ifModifiedMatch == null || ifModifiedMatch.isBlank()) return;

		Instant instant = this.parseIfUnmodifiedSince(ifModifiedMatch.trim());

		if (entity.getDmp() != null && entity.getDmp().getModified() != null) {
			// conflict when the stored DMP has been modified after the timestamp the client last saw;
			// Last-Modified is emitted with second precision, so compare at second granularity
			Instant storedModified = entity.getDmp().getModified().toInstant().truncatedTo(ChronoUnit.SECONDS);
			if (storedModified.isAfter(instant.truncatedTo(ChronoUnit.SECONDS))) throw new DmpStaleException(this.messageSource.getMessage("Precondition_Failed", new Object[]{"If-Unmodified-Since"}, LocaleContextHolder.getLocale()));
		}
	}

	private Instant parseIfUnmodifiedSince(String value) {
		// clients echo back the Last-Modified header, which is an RFC 1123 HTTP-date; ISO 8601 is accepted as well
		try {
			return ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
		} catch (DateTimeParseException ignored) {
		}
		try {
			return Instant.parse(value);
		} catch (DateTimeParseException e) {
			this.logger.warn("invalid If-Unmodified-Since header value: " + value);
			throw new BadRequestException("For 'If-Unmodified-Since': value must be an RFC 1123 HTTP-date or an ISO 8601 date-time");
		}
	}

	private void createDmpTouchedIntegrationEvent(DmpRootElasticEntity entity) {
		DmpTouchedIntegrationEvent event = new DmpTouchedIntegrationEvent();
		event.setDmpId(UUID.fromString(entity.getId()));
        try {
            event.setSubmitterId(this.userScope.getUserId());
        } catch (InvalidApplicationException e) {
            throw new RuntimeException(e);
        }
        event.setActionType(entity.getActionType());
		event.setData(this.builderFactory.builder(DmpRootBuilder.class).build(null, entity));
		event.setSourceCode(this.rdaDmpServiceProperties.getTransformerId());

		if (entity.getDmp() != null && entity.getDmp().getDmpId() != null) {
			if (entity.getDmp().getDmpId().getIdentifier() != null && !entity.getDmp().getDmpId().getIdentifier().isBlank()) event.setExternalId(entity.getDmp().getDmpId().getIdentifier());
			if (entity.getDmp().getDmpId().getType() != null && !entity.getDmp().getDmpId().getType().isBlank()) event.setExternalIdType(entity.getDmp().getDmpId().getType());
		}

		this.dmpTouchedIntegrationEventHandler.handle(event, entity.getTenantId());
	}

	@Override
	public void deleteRda(String id, boolean isFromQueue) throws IOException {
		this.ensureRdaIndex();

		DmpRootElasticEntity dmpRootElasticEntity = this.elasticsearchTemplate.get(id, DmpRootElasticEntity.class, IndexCoordinates.of(this.appElasticConfiguration.getAppElasticProperties().getRdaIndexName()));
		if (dmpRootElasticEntity == null) {
			if (isFromQueue) return;
			else throw new MyNotFoundException(this.messageSource.getMessage("General_ItemNotFound", new Object[]{id, DmpRootElasticEntity.class.getSimpleName()}, LocaleContextHolder.getLocale()));
		}

		this.authorizationService.authorizeAtLeastOneForce(List.of(this.authorizationContentResolver.dmpAffiliation(id)), Permission.DeleteRdaDmp);

		this.elasticsearchTemplate.delete(id, IndexCoordinates.of(this.appElasticConfiguration.getAppElasticProperties().getRdaIndexName()));
	}
	
	//endregion
	
	//region manage
	
	@Override
	public void deleteRdaIndex() throws IOException {
		logger.debug(new MapLogEntry("delete rda index"));
		this.authorizationService.authorizeForce(Permission.ManageElastic);
		
		boolean exists = this.existsRdaIndex();
		if (!exists) return ;
		
		this.restHighLevelClient.indices().delete(new DeleteIndexRequest.Builder().index(this.appElasticConfiguration.getAppElasticProperties().getRdaIndexName()).build());
	}

}
