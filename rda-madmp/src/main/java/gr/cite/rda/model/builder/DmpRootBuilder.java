package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DmpRootElasticEntity;
import gr.cite.rda.model.rdadmp.DmpRootModel;
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
public class DmpRootBuilder extends BaseBuilder<DmpRootModel, DmpRootElasticEntity> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DmpRootBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DmpRootBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public DmpRootBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    private boolean useSharedStorage;
    public DmpRootBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }



    @Override
    public List<DmpRootModel> build(FieldSet directives, List<DmpRootElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<DmpRootModel> models = new ArrayList<>();

        for (DmpRootElasticEntity d : data) {
            DmpRootModel m = new DmpRootModel();

            m.setDmp(this.builderFactory.builder(DmpBuilder.class).authorize(this.authorize).build(null,d.getDmp()));
            m.setId(d.getId());
            models.add(m);
        }

        return models;
    }
}
