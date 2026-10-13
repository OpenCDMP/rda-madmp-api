package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gr.cite.rda.common.enums.DataAccessType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.Date;
import java.util.List;

@JsonPropertyOrder({ DistributionModel._accessUrl, DistributionModel._availableUntil, DistributionModel._byteSize, DistributionModel._dataAccess, DistributionModel._description,
        DistributionModel._downloadUrl, DistributionModel._format, DistributionModel._host, DistributionModel._issued, DistributionModel._license, DistributionModel._title})
@Schema(name = "The Dataset Distribution Items Schema")
public class DistributionModel {

    @JsonProperty("access_url")
    private URI accessUrl;
    public static final String _accessUrl = "accessUrl";

    @JsonProperty("available_until")
    private Date availableUntil;
    public static final String _availableUntil = "availableUntil";

    @JsonProperty("byte_size")
    private Long byteSize;
    public static final String _byteSize = "byteSize";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("data_access")
    private DataAccessType dataAccess;
    public static final String _dataAccess = "dataAccess";

    private String description;
    public static final String _description = "description";

    @JsonProperty("download_url")
    private URI downloadUrl;
    public static final String _downloadUrl = "downloadUrl";

    private List<String> format;
    public static final String _format = "format";

    private HostModel host;
    public static final String _host = "host";

    private Date issued;
    public static final String _issued = "issued";

    private List<LicenseModel> license;
    public static final String _license = "license";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    public static final String _title = "title";

    public URI getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(URI accessUrl) {
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

    public URI getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(URI downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public List<String> getFormat() {
        return format;
    }

    public void setFormat(List<String> format) {
        this.format = format;
    }

    public HostModel getHost() {
        return host;
    }

    public void setHost(HostModel host) {
        this.host = host;
    }

    public Date getIssued() {
        return issued;
    }

    public void setIssued(Date issued) {
        this.issued = issued;
    }

    public List<LicenseModel> getLicense() {
        return license;
    }

    public void setLicense(List<LicenseModel> license) {
        this.license = license;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}