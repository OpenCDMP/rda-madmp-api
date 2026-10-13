package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.FundingElasticEntity;
import gr.cite.rda.model.builder.BaseBuilder;
import gr.cite.rda.model.rdadmp.FundingModel;
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
public class FundingEntityBuilder extends BaseBuilder<FundingElasticEntity, FundingModel> {
    

    private final BuilderFactory builderFactory;
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public FundingEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(FundingEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public FundingEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public FundingEntityBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public FundingEntityBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<FundingElasticEntity> build(FieldSet directives, List<FundingModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<FundingElasticEntity> models = new ArrayList<>();

        for (FundingModel d : data) {
            if (d == null) continue;
            FundingElasticEntity m = new FundingElasticEntity();

            if(d.getFunderId() != null) m.setFunderId(this.builderFactory.builder(FunderIdEntityBuilder.class).authorize(this.authorize).build(null,d.getFunderId()));
            m.setFundingStatus(d.getFundingStatus());
            if(d.getGrantId() != null) m.setGrantId(this.builderFactory.builder(GrantIdEntityBuilder.class).authorize(this.authorize).build(null,d.getGrantId()));

            models.add(m);
        }

        return models;
    }
}
