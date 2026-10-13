package gr.cite.rda.model.builder.dmpentity;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.ContactElasticEntity;
import gr.cite.rda.model.builder.BaseBuilder;
import gr.cite.rda.model.rdadmp.ContactModel;
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
public class ContactEntityBuilder extends BaseBuilder<ContactElasticEntity, ContactModel> {


    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public ContactEntityBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(ContactEntityBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public ContactEntityBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<ContactElasticEntity> build(FieldSet directives, List<ContactModel> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<ContactElasticEntity> models = new ArrayList<>();

        for (ContactModel d : data) {
            if (d == null) continue;
            ContactElasticEntity m = new ContactElasticEntity();

            if(d.getAffiliation() != null) m.setAffiliation(this.builderFactory.builder(AffiliationEntityBuilder.class).authorize(this.authorize).build(null,d.getAffiliation()));
            if(d.getContactId() != null) m.setContactId(this.builderFactory.builder(ContactIdEntityBuilder.class).authorize(this.authorize).build(null,d.getContactId()));
            m.setMbox(d.getMbox());
            m.setName(d.getName());

            models.add(m);
        }

        return models;
    }
}
