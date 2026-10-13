package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.HostIdElasticEntity;
import gr.cite.rda.model.builder.BaseBuilder;
import gr.cite.rda.model.rdadmp.HostIdModel;
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
public class HostIdEntityBuilder extends BaseBuilder<HostIdElasticEntity, HostIdModel> {

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public HostIdEntityBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(HostIdEntityBuilder.class)));
    }

    public HostIdEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    @Override
    public List<HostIdElasticEntity> build(FieldSet directives, List<HostIdModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<HostIdElasticEntity> models = new ArrayList<>();

        for (HostIdModel d : data) {
            HostIdElasticEntity m = new HostIdElasticEntity();
            
            m.setIdentifier(d.getIdentifier());
            m.setType(d.getType());
         
            models.add(m);
        }

        return models;
    }
}
