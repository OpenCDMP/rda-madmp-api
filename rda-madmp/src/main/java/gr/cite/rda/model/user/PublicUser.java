package gr.cite.rda.model.user;


import java.util.UUID;

public class PublicUser {

	public static final String _id = "id";
	private UUID id;

	public static final String _name = "name";
	private String name = null;

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
}
