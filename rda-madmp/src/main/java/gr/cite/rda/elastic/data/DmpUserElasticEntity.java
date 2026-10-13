package gr.cite.rda.elastic.data;

import org.opencdmp.commonmodels.enums.PlanUserRole;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;


public class DmpUserElasticEntity {

	@Field(value = DmpUserElasticEntity._userId, type = FieldType.Keyword)
	private UUID userId;
	public static final String _userId = "userId";

	@Field(value = DmpUserElasticEntity._role, type = FieldType.Short)
	private PlanUserRole role;
	public final static String _role = "role";

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public PlanUserRole getRole() {
		return role;
	}

	public void setRole(PlanUserRole role) {
		this.role = role;
	}
}
