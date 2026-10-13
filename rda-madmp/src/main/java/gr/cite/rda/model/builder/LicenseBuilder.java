package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.LicenseElasticEntity;
import gr.cite.rda.model.rdadmp.LicenseModel;
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
public class LicenseBuilder extends BaseBuilder<LicenseModel, LicenseElasticEntity> {
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public LicenseBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(LicenseBuilder.class)));
    }

    public LicenseBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public LicenseBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public LicenseBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<LicenseModel> build(FieldSet directives, List<LicenseElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<LicenseModel> models = new ArrayList<>();

        for (LicenseElasticEntity d : data) {
            LicenseModel m = new LicenseModel();

            if(d.getLicenseRef() != null){
                try {
                    m.setLicenseRef(URI.create(d.getLicenseRef()));
                }catch (Exception e){}
            }
            m.setStartDate(d.getStartDate());

            models.add(m);
        }

        return models;
    }
}
