package gr.cite.rda.authorization;

public final class Permission {
	public static String DeferredAffiliation = "DeferredAffiliation";
	//User
	public static String BrowseUser = "BrowseUser";
	public static String EditUser = "EditUser";
	public static String DeleteUser = "DeleteUser";

	//Tenant
	public static String BrowseTenant = "BrowseTenant";
	public static String EditTenant = "EditTenant";
	public static String DeleteTenant = "DeleteTenant";
	public static String AllowNoTenant = "AllowNoTenant";

	//RDA-DMP
	public static String BrowseRdaDmp = "BrowseRdaDmp";
	public static String EditRdaDmp = "EditRdaDmp";
	public static String NewRdaDmp = "NewRdaDmp";
	public static String DeleteRdaDmp = "DeleteRdaDmp";

	//Elastic
	public static String ManageElastic = "ManageElastic";
}
