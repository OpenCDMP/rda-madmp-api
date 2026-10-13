package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({ SecurityAndPrivacyModel._description, SecurityAndPrivacyModel._title})
@Schema(name = "The Dataset Security & Policy Items Schema")
public class SecurityAndPrivacyModel {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    public static final String _title = "title";

    private String description;
    public static final String _description = "description";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}