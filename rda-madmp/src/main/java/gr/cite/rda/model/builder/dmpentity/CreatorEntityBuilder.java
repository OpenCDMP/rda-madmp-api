package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.CreatorElasticEntity;
import gr.cite.rda.model.builder.BaseBuilder;
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
public class CreatorEntityBuilder extends BaseBuilder<CreatorElasticEntity, CreatorModel> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public CreatorEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(CreatorEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public CreatorEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<CreatorElasticEntity> build(FieldSet directives, List<CreatorModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<CreatorElasticEntity> models = new ArrayList<>();

        for (CreatorModel d : data) {
            CreatorElasticEntity m = new CreatorElasticEntity();

            if(d.getAffiliation() != null) m.setAffiliation(this.builderFactory.builder(AffiliationEntityBuilder.class).authorize(this.authorize).build(null,d.getAffiliation()));
            if(d.getCreatorId() != null) m.setCreatorId(this.builderFactory.builder(CreatorIdEntityBuilder.class).authorize(this.authorize).build(null,d.getCreatorId()));
            m.setMbox(d.getMbox());
            m.setName(d.getName());

            models.add(m);
        }

        return models;
    }
}
