package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@JsonPropertyOrder({ ProjectModel._description, ProjectModel._end, ProjectModel._funding, ProjectModel._projectId, ProjectModel._start, ProjectModel._title})
@Schema(name = "The DMP Project Items Schema")
public class ProjectModel {

    private String description;
    public static final String _description = "description";

    @Schema(
            type = "string",
            format = "date",
            example = "2019-06-30"
    )
    private Date end;
    public static final String _end = "end";

    private List<FundingModel> funding;
    public static final String _funding = "funding";

    @JsonProperty("project_id")
    private List<ProjectIdModel> projectId;
    public static final String _projectId = "projectId";

    @Schema(
            type = "string",
            format = "date",
            example = "2019-06-30"
    )
    private Date start;
    public static final String _start = "start";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
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

    public List<FundingModel> getFunding() {
        return funding;
    }

    public void setFunding(List<FundingModel> funding) {
        this.funding = funding;
    }

    public List<ProjectIdModel> getProjectId() {
        return projectId;
    }

    public void setProjectId(List<ProjectIdModel> projectId) {
        this.projectId = projectId;
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
}