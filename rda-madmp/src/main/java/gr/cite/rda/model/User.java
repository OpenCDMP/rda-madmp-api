package gr.cite.rda.model;

import gr.cite.rda.common.enums.IsActive;

import java.time.Instant;
import java.util.UUID;

public class User {

	private UUID id;
	public static final String _id = "id";

	private String name;
	public static final String _name = "name";

	private Instant createdAt;

	public static final String _createdAt = "createdAt";

	private Instant updatedAt;

	public static final String _updatedAt = "updatedAt";

	private IsActive isActive;

	public static final String _isActive = "isActive";

	public static final String _hash = "hash";
	private String hash;

	public static final String _tenantUsers = "tenantUsers";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public IsActive getIsActive() {
		return isActive;
	}

	public void setIsActive(IsActive isActive) {
		this.isActive = isActive;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
