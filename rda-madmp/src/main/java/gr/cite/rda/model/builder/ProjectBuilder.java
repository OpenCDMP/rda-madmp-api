package gr.cite.rda.model.builder;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.convention.ConventionService;
import gr.cite.rda.elastic.data.ProjectElasticEntity;
import gr.cite.rda.model.rdadmp.ProjectModel;
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
public class ProjectBuilder extends BaseBuilder<ProjectModel, ProjectElasticEntity> {
    

    private final BuilderFactory builderFactory;
    
    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public ProjectBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(ProjectBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public ProjectBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }
    

    private boolean useSharedStorage;
    public ProjectBuilder useSharedStorage(boolean useSharedStorage) {
        this.useSharedStorage = useSharedStorage;
        return this;
    }

    private boolean isPublic;
    public ProjectBuilder isPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @Override
    public List<ProjectModel> build(FieldSet directives, List<ProjectElasticEntity> data) throws MyApplicationException {
        this.logger.debug("building for {}", Optional.ofNullable(data).map(List::size).orElse(0));
        if (data == null || data.isEmpty()) return new ArrayList<>();
        List<ProjectModel> models = new ArrayList<>();

        for (ProjectElasticEntity d : data) {
            if (d == null) continue;
            ProjectModel m = new ProjectModel();

            m.setDescription(d.getDescription());
            m.setEnd(d.getEnd());
            if(d.getFunding() != null) m.setFunding(this.builderFactory.builder(FundingBuilder.class).authorize(this.authorize).build(null,d.getFunding()));
            if(d.getProjectId() != null) m.setProjectId(this.builderFactory.builder(ProjectIdBuilder.class).authorize(this.authorize).build(null,d.getProjectId()));
            m.setStart(d.getStart());
            m.setTitle(d.getTitle());


            models.add(m);
        }

        return models;
    }
}
