package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.MetadataElasticEntity;
import gr.cite.rda.model.rdadmp.MetadataModel;
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
public class MetadataBuilder extends BaseBuilder<MetadataModel, MetadataElasticEntity> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public MetadataBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(MetadataBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public MetadataBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    private boolean useSharedStorage;
    public MetadataBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }



    @Override
    public List<MetadataModel> build(FieldSet directives, List<MetadataElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<MetadataModel> models = new ArrayList<>();

        for (MetadataElasticEntity d : data) {
            MetadataModel m = new MetadataModel();

            m.setDescription(d.getDescription());
            m.setLanguage(d.getLanguage());
            if(d.getMetadataStandardId() != null) m.setMetadataStandardId(this.builderFactory.builder(MetadataStandardIdBuilder.class).authorize(this.authorize).build(null,d.getMetadataStandardId()));

            models.add(m);
        }

        return models;
    }
}
