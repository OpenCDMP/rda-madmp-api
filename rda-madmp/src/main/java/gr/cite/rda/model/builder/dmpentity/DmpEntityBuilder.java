package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DmpElasticEntity;
import gr.cite.rda.model.builder.*;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DmpEntityBuilder extends BaseBuilder<DmpElasticEntity, DmpModel> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DmpEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DmpEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public DmpEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    private boolean useSharedStorage;
    public DmpEntityBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public DmpEntityBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }
    

    @Override
    public List<DmpElasticEntity> build(FieldSet directives, List<DmpModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<DmpElasticEntity> models = new ArrayList<>();

        for (DmpModel d : data) {
            if (d == null) continue;
            DmpElasticEntity m = new DmpElasticEntity();

            if(d.getAffiliation() != null) m.setAffiliation(this.builderFactory.builder(AffiliationEntityBuilder.class).authorize(this.authorize).build(null,d.getAffiliation()));
            if(d.getAlternateIdentifier() != null) m.setAlternateIdentifier(this.builderFactory.builder(AlternateIdentifierEntityBuilder.class).authorize(this.authorize).build(null,d.getAlternateIdentifier()));
            if(d.getContact() != null) m.setContact(this.builderFactory.builder(ContactEntityBuilder.class).authorize(this.authorize).build(null,d.getContact()));
            if(d.getContributor() != null) m.setContributor(this.builderFactory.builder(ContributorEntityBuilder.class).authorize(this.authorize).build(null,d.getContributor()));
            if(d.getCost() != null) m.setCost(this.builderFactory.builder(CostEntityBuilder.class).authorize(this.authorize).build(null,d.getCost()));
            m.setCreated(d.getCreated());
            if(d.getDataset() != null) m.setDataset(this.builderFactory.builder(DatasetEntityBuilder.class).authorize(this.authorize).build(null,d.getDataset()));
            m.setDescription(d.getDescription());
            if(d.getDmpId() != null) m.setDmpId(this.builderFactory.builder(DmpIdEntityBuilder.class).authorize(this.authorize).build(null,d.getDmpId()));
            m.setEthicalIssuesDescription(d.getEthicalIssuesDescription());
            m.setEthicalIssuesExist(d.getEthicalIssuesExist());
            if(d.getEthicalIssuesReport() != null){
                try {
                    m.setEthicalIssuesReport(d.getEthicalIssuesReport().toString());
                }catch (Exception e){}
            }
            m.setLanguage(d.getLanguage());
            m.setModified(d.getModified());
            if(d.getProject() != null) m.setProject(this.builderFactory.builder(ProjectEntityBuilder.class).authorize(this.authorize).build(null,d.getProject()));
            if(d.getRelatedIdentifier() != null) m.setRelatedIdentifier(this.builderFactory.builder(RelatedIdentifierEntityBuilder.class).authorize(this.authorize).build(null,d.getRelatedIdentifier()));
            m.setTitle(d.getTitle());

            models.add(m);
        }

        return models;
    }
}
