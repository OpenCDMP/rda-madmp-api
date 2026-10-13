package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DmpElasticEntity;
import gr.cite.rda.model.rdadmp.DmpModel;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DmpBuilder extends BaseBuilder<DmpModel, DmpElasticEntity> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DmpBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DmpBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public DmpBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    private boolean useSharedStorage;
    public DmpBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public DmpBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }
    

    @Override
    public List<DmpModel> build(FieldSet directives, List<DmpElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<DmpModel> models = new ArrayList<>();

        for (DmpElasticEntity d : data) {
            if (d == null) continue;
            DmpModel m = new DmpModel();

            if(d.getAffiliation() != null) m.setAffiliation(this.builderFactory.builder(AffiliationBuilder.class).authorize(this.authorize).build(null,d.getAffiliation()));
            if(d.getAlternateIdentifier() != null) m.setAlternateIdentifier(this.builderFactory.builder(AlternateIdentifierBuilder.class).authorize(this.authorize).build(null,d.getAlternateIdentifier()));
            if(d.getContact() != null) m.setContact(this.builderFactory.builder(ContactBuilder.class).authorize(this.authorize).build(null,d.getContact()));
            if(d.getContributor() != null) m.setContributor(this.builderFactory.builder(ContributorBuilder.class).authorize(this.authorize).build(null,d.getContributor()));
            if(d.getCost() != null) m.setCost(this.builderFactory.builder(CostBuilder.class).authorize(this.authorize).build(null,d.getCost()));
            m.setCreated(d.getCreated());
            if(d.getDataset() != null) m.setDataset(this.builderFactory.builder(DatasetBuilder.class).authorize(this.authorize).build(null,d.getDataset()));
            m.setDescription(d.getDescription());
            if(d.getDmpId() != null) m.setDmpId(this.builderFactory.builder(DmpIdBuilder.class).authorize(this.authorize).build(null,d.getDmpId()));
            m.setEthicalIssuesDescription(d.getEthicalIssuesDescription());
            m.setEthicalIssuesExist(d.getEthicalIssuesExist());
            m.setEthicalIssuesReport(d.getEthicalIssuesReport());
            m.setLanguage(d.getLanguage());
            m.setModified(d.getModified());
            if(d.getProject() != null) m.setProject(this.builderFactory.builder(ProjectBuilder.class).authorize(this.authorize).build(null,d.getProject()));
            if(d.getRelatedIdentifier() != null) m.setRelatedIdentifier(this.builderFactory.builder(RelatedIdentifierBuilder.class).authorize(this.authorize).build(null,d.getRelatedIdentifier()));
            m.setTitle(d.getTitle());

            models.add(m);
        }

        return models;
    }
}
