package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.ContributorElasticEntity;
import gr.cite.rda.model.builder.AffiliationBuilder;
import gr.cite.rda.model.builder.BaseBuilder;
import gr.cite.rda.model.rdadmp.ContributorModel;
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
public class ContributorEntityBuilder extends BaseBuilder<ContributorElasticEntity, ContributorModel> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public ContributorEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(ContributorEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public ContributorEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    private boolean useSharedStorage;
    public ContributorEntityBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }



    @Override
    public List<ContributorElasticEntity> build(FieldSet directives, List<ContributorModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<ContributorElasticEntity> models = new ArrayList<>();

        for (ContributorModel d : data) {
            ContributorElasticEntity m = new ContributorElasticEntity();

            if(d.getAffiliation() != null) m.setAffiliation(this.builderFactory.builder(AffiliationEntityBuilder.class).authorize(this.authorize).build(null,d.getAffiliation()));
            if(d.getContributorId() != null) m.setContributorId(this.builderFactory.builder(ContributorIdEntityBuilder.class).authorize(this.authorize).build(null,d.getContributorId()));
            m.setMbox(d.getMbox());
            m.setName(d.getName());
            m.setRole(d.getRole());
            models.add(m);
        }

        return models;
    }
}
