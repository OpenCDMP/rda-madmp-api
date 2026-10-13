package gr.cite.rda.elastic.data;

import gr.cite.rda.common.enums.AccessType;
import gr.cite.rda.common.enums.ActionType;
import gr.cite.rda.common.enums.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.UUID;


@Document(indexName = "dmp")
public class DmpRootElasticEntity {

	@Id
	@Field(value = DmpRootElasticEntity._id, type = FieldType.Keyword)
	private String id;
	public static final String _id = "id";


	@Field(value = DmpRootElasticEntity._dmp, type = FieldType.Object)
	private DmpElasticEntity dmp;
	public static final String _dmp = "dmp";

	@Field(value = DmpRootElasticEntity._users, type = FieldType.Nested)
	private List<DmpUserElasticEntity> users;
	public static final String _users = "users";

	@Field(value = DmpRootElasticEntity._tenantId, type = FieldType.Keyword)
	private UUID tenantId;
	public static final String _tenantId = "tenantId";

	@Field(value = DmpRootElasticEntity._accessType, type = FieldType.Short)
	private AccessType accessType;
	public final static String _accessType = "accessType";

	@Field(value = DmpRootElasticEntity._status, type = FieldType.Short)
	private Status status;
	public final static String _status = "status";

	@Field(value = DmpRootElasticEntity._actionType, type = FieldType.Short)
	private ActionType actionType;
	public final static String _actionType = "actionType";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DmpElasticEntity getDmp() {
		return dmp;
	}

	public void setDmp(DmpElasticEntity dmp) {
		this.dmp = dmp;
	}

	public List<DmpUserElasticEntity> getUsers() {
		return users;
	}

	public void setUsers(List<DmpUserElasticEntity> users) {
		this.users = users;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
}
