package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.ContributorElasticEntity;
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
public class ContributorBuilder extends BaseBuilder<ContributorModel, ContributorElasticEntity> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public ContributorBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(ContributorBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public ContributorBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    private boolean useSharedStorage;
    public ContributorBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }



    @Override
    public List<ContributorModel> build(FieldSet directives, List<ContributorElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<ContributorModel> models = new ArrayList<>();

        for (ContributorElasticEntity d : data) {
            ContributorModel m = new ContributorModel();

            if(d.getAffiliation() != null) m.setAffiliation(this.builderFactory.builder(AffiliationBuilder.class).authorize(this.authorize).build(null,d.getAffiliation()));
            if(d.getContributorId() != null) m.setContributorId(this.builderFactory.builder(ContributorIdBuilder.class).authorize(this.authorize).build(null,d.getContributorId()));
            m.setMbox(d.getMbox());
            m.setName(d.getName());
            m.setRole(d.getRole());
            models.add(m);
        }

        return models;
    }
}
