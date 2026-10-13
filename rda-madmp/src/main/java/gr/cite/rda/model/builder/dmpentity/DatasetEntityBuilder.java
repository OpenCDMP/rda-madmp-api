package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DatasetElasticEntity;
import gr.cite.rda.model.builder.*;
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
public class DatasetEntityBuilder extends BaseBuilder<DatasetElasticEntity, DatasetModel> {
    

    private final BuilderFactory builderFactory;
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DatasetEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DatasetEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public DatasetEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public DatasetEntityBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public DatasetEntityBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<DatasetElasticEntity> build(FieldSet directives, List<DatasetModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<DatasetElasticEntity> models = new ArrayList<>();

        for (DatasetModel d : data) {
            if (d == null) continue;
            DatasetElasticEntity m = new DatasetElasticEntity();

            m.setAlternateIdentifier(this.builderFactory.builder(AlternateIdentifierEntityBuilder.class).authorize(this.authorize).build(null,d.getAlternateIdentifier()));
            m.setCreator(this.builderFactory.builder(CreatorEntityBuilder.class).authorize(this.authorize).build(null,d.getCreator()));
            m.setDataQualityAssurance(d.getDataQualityAssurance());
            if(d.getDatasetId() != null)  m.setDatasetId(this.builderFactory.builder(DatasetIdEntityBuilder.class).authorize(this.authorize).build(null,d.getDatasetId()));
            m.setDescription(d.getDescription());
            if(d.getDistribution() != null) m.setDistribution(this.builderFactory.builder(DistributionEntityBuilder.class).authorize(this.authorize).build(null,d.getDistribution()));
            m.setIsReused(d.getIsReused());
            m.setIssued(d.getIssued());
            m.setKeyword(d.getKeyword());
            m.setLanguage(d.getLanguage());
            if(d.getMetadata() != null)  m.setMetadata(this.builderFactory.builder(MetadataEntityBuilder.class).authorize(this.authorize).build(null,d.getMetadata()));
            m.setPersonalData(d.getPersonalData());
            m.setPreservationStatement(d.getPreservationStatement());
            m.setRights(d.getRights());
            if(d.getRelatedIdentifier() != null) m.setRelatedIdentifier(this.builderFactory.builder(RelatedIdentifierEntityBuilder.class).authorize(this.authorize).build(null,d.getRelatedIdentifier()));
            if(d.getSecurityAndPrivacy() != null)  m.setSecurityAndPrivacy(this.builderFactory.builder(SecurityAndPrivacyEntityBuilder.class).authorize(this.authorize).build(null,d.getSecurityAndPrivacy()));
            m.setSensitiveData(d.getSensitiveData());
            if(d.getTechnicalResource() != null)  m.setTechnicalResource(this.builderFactory.builder(TechnicalResourceEntityBuilder.class).authorize(this.authorize).build(null,d.getTechnicalResource()));
            m.setTitle(d.getTitle());
            m.setType(d.getType());

            models.add(m);
        }

        return models;
    }
}
