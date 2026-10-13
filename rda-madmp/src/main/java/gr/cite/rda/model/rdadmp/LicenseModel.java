package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.Date;

@JsonPropertyOrder({ LicenseModel._licenseRef, LicenseModel._startDate})
@Schema(name = "The Dataset Distribution License Items")
public class LicenseModel {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("license_ref")
    private URI licenseRef;
    public static final String _licenseRef = "licenseRef";

    @JsonProperty("start_date")
    @Schema(
            type = "string",
            format = "date",
            example = "2019-06-30",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Date startDate;
    public static final String _startDate = "startDate";

    public URI getLicenseRef() {
        return licenseRef;
    }

    public void setLicenseRef(URI licenseRef) {
        this.licenseRef = licenseRef;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}