package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.TechnicalResourceIdElasticEntity;
import gr.cite.rda.model.rdadmp.TechnicalResourceIdModel;
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
public class TechnicalResourceIdBuilder extends BaseBuilder<TechnicalResourceIdModel, TechnicalResourceIdElasticEntity> {

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public TechnicalResourceIdBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(TechnicalResourceIdBuilder.class)));
    }

    public TechnicalResourceIdBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    @Override
    public List<TechnicalResourceIdModel> build(FieldSet directives, List<TechnicalResourceIdElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<TechnicalResourceIdModel> models = new ArrayList<>();

        for (TechnicalResourceIdElasticEntity d : data) {
            TechnicalResourceIdModel m = new TechnicalResourceIdModel();
            
            m.setIdentifier(d.getIdentifier());
            m.setType(d.getType());
         
            models.add(m);
        }
        
        return models;
    }
}
