package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.TechnicalResourceElasticEntity;
import gr.cite.rda.model.builder.BaseBuilder;
import gr.cite.rda.model.builder.TechnicalResourceIdBuilder;
import gr.cite.rda.model.rdadmp.TechnicalResourceModel;
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
public class TechnicalResourceEntityBuilder extends BaseBuilder<TechnicalResourceElasticEntity, TechnicalResourceModel> {

    private final BuilderFactory builderFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public TechnicalResourceEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(TechnicalResourceEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public TechnicalResourceEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public TechnicalResourceEntityBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public TechnicalResourceEntityBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<TechnicalResourceElasticEntity> build(FieldSet directives, List<TechnicalResourceModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<TechnicalResourceElasticEntity> models = new ArrayList<>();

        for (TechnicalResourceModel d : data) {
            TechnicalResourceElasticEntity m = new TechnicalResourceElasticEntity();

            m.setDescription(d.getDescription());
            m.setName(d.getName());
            if(d.getTechnicalResourceId() != null) m.setTechnicalResourceId(this.builderFactory.builder(TechnicalResourceIdEntityBuilder.class).authorize(this.authorize).build(null,d.getTechnicalResourceId()));

            models.add(m);
        }

        return models;
    }
}
