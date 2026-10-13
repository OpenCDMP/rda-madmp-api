package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DistributionElasticEntity;
import gr.cite.rda.model.builder.BaseBuilder;
import gr.cite.rda.model.rdadmp.DistributionModel;
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
public class DistributionEntityBuilder extends BaseBuilder<DistributionElasticEntity, DistributionModel> {
    

    private final BuilderFactory builderFactory;
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DistributionEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DistributionEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public DistributionEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public DistributionEntityBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public DistributionEntityBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<DistributionElasticEntity> build(FieldSet directives, List<DistributionModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<DistributionElasticEntity> models = new ArrayList<>();

        for (DistributionModel d : data) {
            if (d == null) continue;
            DistributionElasticEntity m = new DistributionElasticEntity();

            if(d.getAccessUrl() != null){
                try {
                    m.setAccessUrl(d.getAccessUrl().toString());
                }catch (Exception e){}
            }
            m.setAvailableUntil(d.getAvailableUntil());
            m.setByteSize(d.getByteSize());
            m.setDataAccess(d.getDataAccess());
            m.setDescription(d.getDescription());
            if(d.getDownloadUrl() != null){
                try {
                    m.setDownloadUrl(d.getDownloadUrl().toString());
                }catch (Exception e){}
            }
            m.setFormat(d.getFormat());
            if(d.getHost() != null )  m.setHost(this.builderFactory.builder(HostEntityBuilder.class).authorize(this.authorize).build(null,d.getHost()));
            m.setIssued(d.getIssued());
            if(d.getLicense() != null) m.setLicense(this.builderFactory.builder(LicenseEntityBuilder.class).authorize(this.authorize).build(null,d.getLicense()));
            m.setTitle(d.getTitle());

            models.add(m);
        }

        return models;
    }
}
