package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.AffiliationElasticEntity;
import gr.cite.rda.model.builder.BaseBuilder;
import gr.cite.rda.model.rdadmp.AffiliationModel;
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
public class AffiliationEntityBuilder extends BaseBuilder<AffiliationElasticEntity, AffiliationModel> {

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
    private final BuilderFactory builderFactory;

    @Autowired
    public AffiliationEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory, BuilderFactory builderFactory1) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(AffiliationEntityBuilder.class)));
        this.builderFactory = builderFactory1;
    }

    public AffiliationEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    @Override
    public List<AffiliationElasticEntity> build(FieldSet directives, List<AffiliationModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<AffiliationElasticEntity> models = new ArrayList<>();

        for (AffiliationModel d : data) {
            AffiliationElasticEntity m = new AffiliationElasticEntity();

            if(d.getAffiliationId() != null) m.setAffiliationId(this.builderFactory.builder(AffiliationIdEntityBuilder.class).authorize(this.authorize).build(null,d.getAffiliationId()));
            m.setName(d.getName());
         
            models.add(m);
        }
        
        return models;
    }
}
