package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.common.enums.FundingStatusType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class FundingElasticEntity {

    @JsonProperty("funder_id")
    @Field(value = FundingElasticEntity._funderId, type = FieldType.Object)
    private FunderIdElasticEntity funderId;
    public static final String _funderId = "funderId";

    @JsonProperty("funding_status")
    @Field(value = FundingElasticEntity._fundingStatus, type = FieldType.Keyword)
    private FundingStatusType fundingStatus;
    public static final String _fundingStatus = "fundingStatus";

    @JsonProperty("grant_id")
    @Field(value = FundingElasticEntity._grantId, type = FieldType.Object)
    private GrantIdElasticEntity grantId;
    public static final String _grantId = "grantId";

    public FunderIdElasticEntity getFunderId() {
        return funderId;
    }

    public void setFunderId(FunderIdElasticEntity funderId) {
        this.funderId = funderId;
    }

    public FundingStatusType getFundingStatus() {
        return fundingStatus;
    }

    public void setFundingStatus(FundingStatusType fundingStatus) {
        this.fundingStatus = fundingStatus;
    }

    public GrantIdElasticEntity getGrantId() {
        return grantId;
    }

    public void setGrantId(GrantIdElasticEntity grantId) {
        this.grantId = grantId;
    }
}