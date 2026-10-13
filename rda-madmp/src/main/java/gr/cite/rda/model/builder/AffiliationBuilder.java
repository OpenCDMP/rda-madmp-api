package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.AffiliationElasticEntity;
import gr.cite.rda.model.builder.dmpentity.AffiliationIdEntityBuilder;
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
public class AffiliationBuilder extends BaseBuilder<AffiliationModel, AffiliationElasticEntity> {

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);
    private final BuilderFactory builderFactory;

    @Autowired
    public AffiliationBuilder(ConventionService conventionService, BuilderFactory builderFactory, BuilderFactory builderFactory1) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(AffiliationBuilder.class)));
        this.builderFactory = builderFactory1;
    }

    public AffiliationBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    @Override
    public List<AffiliationModel> build(FieldSet directives, List<AffiliationElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<AffiliationModel> models = new ArrayList<>();

        for (AffiliationElasticEntity d : data) {
            AffiliationModel m = new AffiliationModel();

            if(d.getAffiliationId() != null) m.setAffiliationId(this.builderFactory.builder(AffiliationIdBuilder.class).authorize(this.authorize).build(null,d.getAffiliationId()));
            m.setName(d.getName());
         
            models.add(m);
        }
        
        return models;
    }
}
