package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DatasetIdElasticEntity;
import gr.cite.rda.model.rdadmp.DatasetIdModel;
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
public class DatasetIdBuilder extends BaseBuilder<DatasetIdModel, DatasetIdElasticEntity> {

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DatasetIdBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DatasetIdBuilder.class)));
    }

    public DatasetIdBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    @Override
    public List<DatasetIdModel> build(FieldSet directives, List<DatasetIdElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<DatasetIdModel> models = new ArrayList<>();

        for (DatasetIdElasticEntity d : data) {
            DatasetIdModel m = new DatasetIdModel();
            
            m.setIdentifier(d.getIdentifier());
            m.setType(d.getType());
         
            models.add(m);
        }

        return models;
    }
}
