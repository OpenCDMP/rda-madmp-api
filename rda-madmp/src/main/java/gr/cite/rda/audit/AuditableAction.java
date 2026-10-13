package gr.cite.rda.audit;

import gr.cite.tools.logging.EventId;

public class AuditableAction {

    public static final EventId Principal_Lookup = new EventId(6000, "Principal_Lookup");

    public static final EventId User_Query = new EventId(11000, "User_Query");
    public static final EventId User_Lookup = new EventId(11001, "User_Lookup");
    public static final EventId User_Persist = new EventId(11002, "User_Persist");
    public static final EventId User_Delete = new EventId(11003, "User_Delete");

    public static final EventId Tenant_Query = new EventId(12000, "Tenant_Query");
    public static final EventId Tenant_Lookup = new EventId(12001, "Tenant_Lookup");
    public static final EventId Tenant_Persist = new EventId(12002, "Tenant_Persist");
    public static final EventId Tenant_Delete = new EventId(12003, "Tenant_Delete");

    public static final EventId Dmp_Query = new EventId(13000, "Dmp_Query");
    public static final EventId Dmp_Lookup = new EventId(13001, "Dmp_Lookup");
    public static final EventId Dmp_Persist = new EventId(13002, "Dmp_Persist");
    public static final EventId Dmp_Delete = new EventId(13002, "Dmp_Persist");





}
