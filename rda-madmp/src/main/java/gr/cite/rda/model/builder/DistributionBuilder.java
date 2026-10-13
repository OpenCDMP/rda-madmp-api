package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.DistributionElasticEntity;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DistributionBuilder extends BaseBuilder<DistributionModel, DistributionElasticEntity> {
    

    private final BuilderFactory builderFactory;
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public DistributionBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(DistributionBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public DistributionBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public DistributionBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public DistributionBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<DistributionModel> build(FieldSet directives, List<DistributionElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<DistributionModel> models = new ArrayList<>();

        for (DistributionElasticEntity d : data) {
            if (d == null) continue;
            DistributionModel m = new DistributionModel();

            if(d.getAccessUrl() != null){
                try {
                    m.setAccessUrl(URI.create(d.getAccessUrl()));
                }catch (Exception e){}
            }
            m.setAvailableUntil(d.getAvailableUntil());
            m.setByteSize(d.getByteSize());
            m.setDataAccess(d.getDataAccess());
            m.setDescription(d.getDescription());
            if(d.getDownloadUrl() != null){
                try {
                    m.setDownloadUrl(URI.create(d.getDownloadUrl()));
                }catch (Exception e){}
            }
            m.setFormat(d.getFormat());
            if(d.getHost() != null )  m.setHost(this.builderFactory.builder(HostBuilder.class).authorize(this.authorize).build(null,d.getHost()));
            m.setIssued(d.getIssued());
            if(d.getLicense() != null) m.setLicense(this.builderFactory.builder(LicenseBuilder.class).authorize(this.authorize).build(null,d.getLicense()));
            m.setTitle(d.getTitle());

            models.add(m);
        }

        return models;
    }
}
