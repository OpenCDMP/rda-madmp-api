package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.RelatedIdentifierElasticEntity;
import gr.cite.rda.model.rdadmp.RelatedIdentifierModel;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RelatedIdentifierBuilder extends BaseBuilder<RelatedIdentifierModel, RelatedIdentifierElasticEntity> {

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public RelatedIdentifierBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(RelatedIdentifierBuilder.class)));
    }

    public RelatedIdentifierBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    @Override
    public List<RelatedIdentifierModel> build(FieldSet directives, List<RelatedIdentifierElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<RelatedIdentifierModel> models = new ArrayList<>();

        for (RelatedIdentifierElasticEntity d : data) {
            RelatedIdentifierModel m = new RelatedIdentifierModel();
            
            m.setIdentifier(d.getIdentifier());
            m.setMetadataScheme(d.getMetadataScheme());
            m.setRelationType(d.getRelationType());
            m.setResourceType(d.getResourceType());
            m.setSchemeType(d.getSchemeType());
            if(d.getSchemeUri() != null){
                try {
                    m.setSchemeUri(URI.create(d.getSchemeUri()));
                }catch (Exception e){}
            }
            m.setType(d.getType());
         
            models.add(m);
        }
        
        return models;
    }
}
