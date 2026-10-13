package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gr.cite.rda.common.enums.FundingStatusType;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({ FundingModel._funderId, FundingModel._fundingStatus, FundingModel._grantId})
@Schema(name = "The DMP Project Funding Schema")
public class FundingModel {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("funder_id")
    private FunderIdModel funderId;
    public static final String _funderId = "funderId";

    @JsonProperty("funding_status")
    private FundingStatusType fundingStatus;
    public static final String _fundingStatus = "fundingStatus";

    @JsonProperty("grant_id")
    private GrantIdModel grantId;
    public static final String _grantId = "grantId";

    public FunderIdModel getFunderId() {
        return funderId;
    }

    public void setFunderId(FunderIdModel funderId) {
        this.funderId = funderId;
    }

    public FundingStatusType getFundingStatus() {
        return fundingStatus;
    }

    public void setFundingStatus(FundingStatusType fundingStatus) {
        this.fundingStatus = fundingStatus;
    }

    public GrantIdModel getGrantId() {
        return grantId;
    }

    public void setGrantId(GrantIdModel grantId) {
        this.grantId = grantId;
    }
}