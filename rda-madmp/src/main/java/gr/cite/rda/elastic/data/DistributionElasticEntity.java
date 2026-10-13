package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.common.enums.DataAccessType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

public class DistributionElasticEntity {

    ///TODO: Because of error its String but it needs to be a URI
    @JsonProperty("access_url")
    @Field(value = DistributionElasticEntity._accessUrl, type = FieldType.Text)
    private String accessUrl;
    public static final String _accessUrl = "accessUrl";

    @JsonProperty("available_until")
    @Field(value = DistributionElasticEntity._availableUntil, type = FieldType.Date)
    private Date availableUntil;
    public static final String _availableUntil = "availableUntil";

    @JsonProperty("byte_size")
    @Field(value = DistributionElasticEntity._byteSize, type = FieldType.Long)
    private Long byteSize;
    public static final String _byteSize = "byteSize";

    @JsonProperty("data_access")
    @Field(value = DistributionElasticEntity._dataAccess, type = FieldType.Keyword)
    private DataAccessType dataAccess;
    public static final String _dataAccess = "dataAccess";

    @Field(value = DistributionElasticEntity._description, type = FieldType.Text)
    private String description;
    public static final String _description = "description";

    ///TODO: Because of error its String but it needs to be a URI
    @JsonProperty("download_url")
    @Field(value = DistributionElasticEntity._downloadUrl, type = FieldType.Text)
    private String downloadUrl;
    public static final String _downloadUrl = "downloadUrl";

    @Field(value = DistributionElasticEntity._format, type = FieldType.Text)
    private List<String> format;
    public static final String _format = "format";

    @Field(value = DistributionElasticEntity._host, type = FieldType.Object)
    private HostElasticEntity host;
    public static final String _host = "host";

    @Field(value = DistributionElasticEntity._issued, type = FieldType.Date)
    private Date issued;
    public static final String _issued = "issued";

    @Field(value = DistributionElasticEntity._license, type = FieldType.Nested)
    private List<LicenseElasticEntity> license;
    public static final String _license = "license";

    @Field(value = DistributionElasticEntity._title, type = FieldType.Text)
    private String title;
    public static final String _title = "title";

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public Date getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(Date availableUntil) {
        this.availableUntil = availableUntil;
    }

    public Long getByteSize() {
        return byteSize;
    }

    public void setByteSize(Long byteSize) {
        this.byteSize = byteSize;
    }

    public DataAccessType getDataAccess() {
        return dataAccess;
    }

    public void setDataAccess(DataAccessType dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public List<String> getFormat() {
        return format;
    }

    public void setFormat(List<String> format) {
        this.format = format;
    }

    public HostElasticEntity getHost() {
        return host;
    }

    public void setHost(HostElasticEntity host) {
        this.host = host;
    }

    public List<LicenseElasticEntity> getLicense() {
        return license;
    }

    public void setLicense(List<LicenseElasticEntity> license) {
        this.license = license;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getIssued() {
        return issued;
    }

    public void setIssued(Date issued) {
        this.issued = issued;
    }
}