package gr.cite.rda.model.rdadmp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gr.cite.rda.common.enums.CertifiedWithType;
import gr.cite.rda.common.enums.GeoLocationType;
import gr.cite.rda.common.enums.SupportVersioningType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.List;

@JsonPropertyOrder({ HostModel._availability, HostModel._backupFrequency, HostModel._backupType, HostModel._certifiedWith, HostModel._description,
        HostModel._geoLocation, HostModel._hostId, HostModel._pidSystem, HostModel._storageType, HostModel._supportVersioning, HostModel._title, HostModel._url})
@Schema(name = "The Dataset Distribution Host Schema")
public class HostModel {

    private String availability;
    public static final String _availability = "availability";

    @JsonProperty("backup_frequency")
    private String backupFrequency;
    public static final String _backupFrequency = "backupFrequency";

    @JsonProperty("backup_type")
    private String backupType;
    public static final String _backupType = "backupType";

    @JsonProperty("certified_with")
    private CertifiedWithType certifiedWith;
    public static final String _certifiedWith = "certifiedWith";

    private String description;
    public static final String _description = "description";

    @JsonProperty("geo_location")
    private GeoLocationType geoLocation;
    public static final String _geoLocation = "geoLocation";

    @JsonProperty("host_id")
    private List<HostIdModel> hostId;
    public static final String _hostId = "hostId";

    @JsonProperty("pid_system")
    private List<String> pidSystem;
    public static final String _pidSystem = "pidSystem";

    @JsonProperty("storage_type")
    private String storageType;
    public static final String _storageType = "storageType";

    @JsonProperty("support_versioning")
    private SupportVersioningType supportVersioning;
    public static final String _supportVersioning = "supportVersioning";

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    public static final String _title = "title";

    private URI url;
    public static final String _url = "url";

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getBackupFrequency() {
        return backupFrequency;
    }

    public void setBackupFrequency(String backupFrequency) {
        this.backupFrequency = backupFrequency;
    }

    public String getBackupType() {
        return backupType;
    }

    public void setBackupType(String backupType) {
        this.backupType = backupType;
    }

    public CertifiedWithType getCertifiedWith() {
        return certifiedWith;
    }

    public void setCertifiedWith(CertifiedWithType certifiedWith) {
        this.certifiedWith = certifiedWith;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GeoLocationType getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocationType geoLocation) {
        this.geoLocation = geoLocation;
    }

    public List<String> getPidSystem() {
        return pidSystem;
    }

    public List<HostIdModel> getHostId() {
        return hostId;
    }

    public void setHostId(List<HostIdModel> hostId) {
        this.hostId = hostId;
    }

    public void setPidSystem(List<String> pidSystem) {
        this.pidSystem = pidSystem;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public SupportVersioningType getSupportVersioning() {
        return supportVersioning;
    }

    public void setSupportVersioning(SupportVersioningType supportVersioning) {
        this.supportVersioning = supportVersioning;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }
}