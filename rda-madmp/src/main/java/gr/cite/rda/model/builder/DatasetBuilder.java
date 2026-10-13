package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DatasetElasticEntity;
import gr.cite.rda.model.rdadmp.DatasetModel;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DatasetBuilder extends BaseBuilder<DatasetModel, DatasetElasticEntity> {
    

    private final BuilderFactory builderFactory;
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DatasetBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DatasetBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public DatasetBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }


    @Override
    public List<DatasetModel> build(FieldSet directives, List<DatasetElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<DatasetModel> models = new ArrayList<>();

        for (DatasetElasticEntity d : data) {
            if (d == null) continue;
            DatasetModel m = new DatasetModel();

            m.setAlternateIdentifier(this.builderFactory.builder(AlternateIdentifierBuilder.class).authorize(this.authorize).build(null,d.getAlternateIdentifier()));
            m.setCreator(this.builderFactory.builder(CreatorBuilder.class).authorize(this.authorize).build(null,d.getCreator()));
            m.setDataQualityAssurance(d.getDataQualityAssurance());
            if(d.getDatasetId() != null)  m.setDatasetId(this.builderFactory.builder(DatasetIdBuilder.class).authorize(this.authorize).build(null,d.getDatasetId()));
            m.setDescription(d.getDescription());
            if(d.getDistribution() != null) m.setDistribution(this.builderFactory.builder(DistributionBuilder.class).authorize(this.authorize).build(null,d.getDistribution()));
            m.setIsReused(d.getIsReused());
            m.setIssued(d.getIssued());
            m.setKeyword(d.getKeyword());
            m.setLanguage(d.getLanguage());
            if(d.getMetadata() != null)  m.setMetadata(this.builderFactory.builder(MetadataBuilder.class).authorize(this.authorize).build(null,d.getMetadata()));
            m.setPersonalData(d.getPersonalData());
            m.setPreservationStatement(d.getPreservationStatement());
            m.setRights(d.getRights());
            if(d.getRelatedIdentifier() != null) m.setRelatedIdentifier(this.builderFactory.builder(RelatedIdentifierBuilder.class).authorize(this.authorize).build(null,d.getRelatedIdentifier()));
            if(d.getSecurityAndPrivacy() != null)  m.setSecurityAndPrivacy(this.builderFactory.builder(SecurityAndPrivacyBuilder.class).authorize(this.authorize).build(null,d.getSecurityAndPrivacy()));
            m.setSensitiveData(d.getSensitiveData());
            if(d.getTechnicalResource() != null)  m.setTechnicalResource(this.builderFactory.builder(TechnicalResourceBuilder.class).authorize(this.authorize).build(null,d.getTechnicalResource()));
            m.setTitle(d.getTitle());
            m.setType(d.getType());

            models.add(m);
        }

        return models;
    }
}
