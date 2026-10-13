package gr.cite.rda.elastic.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.cite.rda.common.enums.CertifiedWithType;
import gr.cite.rda.common.enums.GeoLocationType;
import gr.cite.rda.common.enums.SupportVersioningType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class HostElasticEntity {

    @Field(value = HostElasticEntity._availability, type = FieldType.Text)
    private String availability;
    public static final String _availability = "availability";

    @JsonProperty("backup_frequency")
    @Field(value = HostElasticEntity._backupFrequency, type = FieldType.Text)
    private String backupFrequency;
    public static final String _backupFrequency = "backupFrequency";

    @JsonProperty("backup_type")
    @Field(value = HostElasticEntity._backupType, type = FieldType.Text)
    private String backupType;
    public static final String _backupType = "backupType";

    @JsonProperty("certified_with")
    @Field(value = HostElasticEntity._certifiedWith, type = FieldType.Keyword)
    private CertifiedWithType certifiedWith;
    public static final String _certifiedWith = "certifiedWith";

    @Field(value = HostElasticEntity._description, type = FieldType.Text)
    private String description;
    public static final String _description = "description";

    @JsonProperty("geo_location")
    @Field(value = HostElasticEntity._geoLocation, type = FieldType.Keyword)
    private GeoLocationType geoLocation;
    public static final String _geoLocation = "geoLocation";

    @JsonProperty("host_id")
    @Field(value = HostElasticEntity._hostId, type = FieldType.Nested)
    private List<HostIdElasticEntity> hostId;
    public static final String _hostId = "host_id";

    @JsonProperty("pid_system")
    @Field(value = HostElasticEntity._pidSystem, type = FieldType.Keyword)
    private List<String> pidSystem;
    public static final String _pidSystem = "pidSystem";

    @JsonProperty("storage_type")
    @Field(value = HostElasticEntity._storageType, type = FieldType.Text)
    private String storageType;
    public static final String _storageType = "storageType";

    @JsonProperty("support_versioning")
    @Field(value = HostElasticEntity._supportsVersioning, type = FieldType.Keyword)
    private SupportVersioningType supportVersioning;
    public static final String _supportsVersioning = "supportsVersioning";

    @Field(value = HostElasticEntity._title, type = FieldType.Text)
    private String title;
    public static final String _title = "title";

    ///TODO: Because of error its String but it needs to be a URI
    @Field(value = HostElasticEntity._url, type = FieldType.Text)
    private String url;
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

    public void setPidSystem(List<String> pidSystem) {
        this.pidSystem = pidSystem;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public SupportVersioningType getSupportsVersioning() {
        return supportVersioning;
    }

    public void setSupportsVersioning(SupportVersioningType supportsVersioning) {
        this.supportVersioning = supportsVersioning;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<HostIdElasticEntity> getHostId() {
        return hostId;
    }

    public void setHostId(List<HostIdElasticEntity> hostId) {
        this.hostId = hostId;
    }

    public SupportVersioningType getSupportVersioning() {
        return supportVersioning;
    }

    public void setSupportVersioning(SupportVersioningType supportVersioning) {
        this.supportVersioning = supportVersioning;
    }
}