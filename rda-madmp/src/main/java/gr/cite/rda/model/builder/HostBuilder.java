package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.HostElasticEntity;
import gr.cite.rda.model.builder.dmpentity.HostIdEntityBuilder;
import gr.cite.rda.model.rdadmp.HostModel;
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
public class HostBuilder extends BaseBuilder<HostModel, HostElasticEntity> {

    private final BuilderFactory builderFactory;
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public HostBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(HostBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public HostBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public HostBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public HostBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<HostModel> build(FieldSet directives, List<HostElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<HostModel> models = new ArrayList<>();

        for (HostElasticEntity d : data) {
            if (d == null) continue;
            HostModel m = new HostModel();

            m.setAvailability(d.getAvailability());
            m.setBackupFrequency(d.getBackupFrequency());
            m.setBackupType(d.getBackupType());
            m.setCertifiedWith(d.getCertifiedWith());
            m.setDescription(d.getDescription());
            m.setGeoLocation(d.getGeoLocation());
            if(d.getHostId() != null ) m.setHostId(this.builderFactory.builder(HostIdBuilder.class).authorize(this.authorize).build(null,d.getHostId()));
            m.setPidSystem(d.getPidSystem());
            m.setStorageType(d.getStorageType());
            m.setSupportVersioning(d.getSupportsVersioning());
            m.setTitle(d.getTitle());
            if(d.getUrl() != null){
                try {
                    m.setUrl(URI.create(d.getUrl()));
                }catch (Exception e){}
            }
            models.add(m);
        }

        return models;
    }
}
