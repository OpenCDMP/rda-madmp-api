package gr.cite.rda.authorization;

import java.util.EnumSet;

public enum AuthorizationFlags {
	None, Permission, Associated, PlanAssociated, Public, Owner, DescriptionTemplateAssociated;
	public static final EnumSet<AuthorizationFlags> AllExceptPublic = EnumSet.of(PlanAssociated, Permission, Owner, DescriptionTemplateAssociated);
	public static final EnumSet<AuthorizationFlags> All = EnumSet.of(PlanAssociated, Permission, Owner, DescriptionTemplateAssociated, Public);
	public static final EnumSet<AuthorizationFlags> OwnerOrPermissionAssociated = EnumSet.of(Owner, Permission, Associated);
}
