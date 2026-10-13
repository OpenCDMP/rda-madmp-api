package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.HostElasticEntity;
import gr.cite.rda.model.builder.BaseBuilder;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HostEntityBuilder extends BaseBuilder<HostElasticEntity, HostModel> {

    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public HostEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(HostEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public HostEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public HostEntityBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public HostEntityBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<HostElasticEntity> build(FieldSet directives, List<HostModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<HostElasticEntity> models = new ArrayList<>();

        for (HostModel d : data) {
            if (d == null) continue;
            HostElasticEntity m = new HostElasticEntity();

            m.setAvailability(d.getAvailability());
            m.setBackupFrequency(d.getBackupFrequency());
            m.setBackupType(d.getBackupType());
            m.setCertifiedWith(d.getCertifiedWith());
            m.setDescription(d.getDescription());
            m.setGeoLocation(d.getGeoLocation());
            if(d.getHostId() != null ) m.setHostId(this.builderFactory.builder(HostIdEntityBuilder.class).authorize(this.authorize).build(null,d.getHostId()));
            m.setPidSystem(d.getPidSystem());
            m.setStorageType(d.getStorageType());
            m.setSupportsVersioning(d.getSupportVersioning());
            m.setTitle(d.getTitle());
            if(d.getUrl() != null){
                try {
                    m.setUrl(d.getUrl().toString());
                }catch (Exception e){}
            }
            models.add(m);
        }

        return models;
    }
}
