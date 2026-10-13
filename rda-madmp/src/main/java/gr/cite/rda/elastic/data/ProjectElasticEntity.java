package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

public class ProjectElasticEntity {

    @Field(value = ProjectElasticEntity._description, type = FieldType.Text)
    private String description;
    public static final String _description = "description";

    @Field(value = ProjectElasticEntity._end, type = FieldType.Date)
    private Date end;
    public static final String _end = "end";

    @Field(value = ProjectElasticEntity._funding, type = FieldType.Nested)
    private List<FundingElasticEntity> funding;
    public static final String _funding = "funding";

    @JsonProperty("project_id")
    @Field(value = ProjectElasticEntity._projectId, type = FieldType.Nested)
    private List<ProjectIdElasticEntity> projectId;
    public static final String _projectId = "project_id";

    @Field(value = ProjectElasticEntity._start, type = FieldType.Date)
    private Date start;
    public static final String _start = "start";

    @Field(value = ProjectElasticEntity._title, type = FieldType.Text)
    private String title;
    public static final String _title = "title";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public List<FundingElasticEntity> getFunding() {
        return funding;
    }

    public void setFunding(List<FundingElasticEntity> funding) {
        this.funding = funding;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ProjectIdElasticEntity> getProjectId() {
        return projectId;
    }

    public void setProjectId(List<ProjectIdElasticEntity> projectId) {
        this.projectId = projectId;
    }
}