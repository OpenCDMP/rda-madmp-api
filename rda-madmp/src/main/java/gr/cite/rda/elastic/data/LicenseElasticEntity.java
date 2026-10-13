package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

public class LicenseElasticEntity {


    ///TODO: Because of error its String but it needs to be a URI
    @JsonProperty("license_ref")
    @Field(value = LicenseElasticEntity._licenseRef, type = FieldType.Text)
    private String licenseRef;
    public static final String _licenseRef = "licenseRef";

    @JsonProperty("start_date")
    @Field(value = LicenseElasticEntity._startDate, type = FieldType.Date)
    private Date startDate;
    public static final String _startDate = "startDate";

    public String getLicenseRef() {
        return licenseRef;
    }

    public void setLicenseRef(String licenseRef) {
        this.licenseRef = licenseRef;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}