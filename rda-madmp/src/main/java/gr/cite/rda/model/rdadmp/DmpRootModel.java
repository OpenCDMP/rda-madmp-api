package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({ DmpRootModel._dmp, DmpRootModel._id })
@Schema(name = "DMPWithID")
public class DmpRootModel {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    public static final String _id = "id";


    private DmpModel dmp;
    public static final String _dmp = "dmp";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DmpModel getDmp() {
        return dmp;
    }

    public void setDmp(DmpModel dmp) {
        this.dmp = dmp;
    }
}