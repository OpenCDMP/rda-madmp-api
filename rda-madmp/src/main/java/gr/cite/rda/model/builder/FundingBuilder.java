package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.FundingElasticEntity;
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
public class FundingBuilder extends BaseBuilder<FundingModel, FundingElasticEntity> {
    

    private final BuilderFactory builderFactory;
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public FundingBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(FundingBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public FundingBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public FundingBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public FundingBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<FundingModel> build(FieldSet directives, List<FundingElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<FundingModel> models = new ArrayList<>();

        for (FundingElasticEntity d : data) {
            if (d == null) continue;
            FundingModel m = new FundingModel();

            if(d.getFunderId() != null) m.setFunderId(this.builderFactory.builder(FunderIdBuilder.class).authorize(this.authorize).build(null,d.getFunderId()));
            m.setFundingStatus(d.getFundingStatus());
            if(d.getGrantId() != null) m.setGrantId(this.builderFactory.builder(GrantIdBuilder.class).authorize(this.authorize).build(null,d.getGrantId()));

            models.add(m);
        }

        return models;
    }
}
