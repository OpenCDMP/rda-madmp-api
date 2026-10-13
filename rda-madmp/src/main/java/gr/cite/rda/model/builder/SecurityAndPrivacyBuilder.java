package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.SecurityAndPrivacyElasticEntity;
import gr.cite.rda.model.rdadmp.SecurityAndPrivacyModel;
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
public class SecurityAndPrivacyBuilder extends BaseBuilder<SecurityAndPrivacyModel, SecurityAndPrivacyElasticEntity> {
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public SecurityAndPrivacyBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(SecurityAndPrivacyBuilder.class)));
    }

    public SecurityAndPrivacyBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public SecurityAndPrivacyBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public SecurityAndPrivacyBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<SecurityAndPrivacyModel> build(FieldSet directives, List<SecurityAndPrivacyElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<SecurityAndPrivacyModel> models = new ArrayList<>();

        for (SecurityAndPrivacyElasticEntity d : data) {
            SecurityAndPrivacyModel m = new SecurityAndPrivacyModel();

            m.setTitle(d.getTitle());
            m.setDescription(d.getDescription());

            models.add(m);
        }

        return models;
    }
}
