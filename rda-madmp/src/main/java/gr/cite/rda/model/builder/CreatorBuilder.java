package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.CreatorElasticEntity;
import gr.cite.rda.model.rdadmp.CreatorModel;
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
public class CreatorBuilder extends BaseBuilder<CreatorModel, CreatorElasticEntity> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public CreatorBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(CreatorBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public CreatorBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<CreatorModel> build(FieldSet directives, List<CreatorElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<CreatorModel> models = new ArrayList<>();

        for (CreatorElasticEntity d : data) {
            CreatorModel m = new CreatorModel();

            if(d.getAffiliation() != null) m.setAffiliation(this.builderFactory.builder(AffiliationBuilder.class).authorize(this.authorize).build(null,d.getAffiliation()));
            if(d.getCreatorId() != null) m.setCreatorId(this.builderFactory.builder(CreatorIdBuilder.class).authorize(this.authorize).build(null,d.getCreatorId()));
            m.setMbox(d.getMbox());
            m.setName(d.getName());

            models.add(m);
        }

        return models;
    }
}
