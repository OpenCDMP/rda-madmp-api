package gr.cite.rda.service.elastic;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.model.result.QueryResult;
import gr.cite.rda.query.lookup.DmpLookup;


import java.util.EnumSet;


public interface ElasticQueryHelperService {
    QueryResult collect(DmpLookup lookup, EnumSet<AuthorizationFlags> flags);

//    QueryResult collectId(String id, EnumSet<AuthorizationFlags> flags);

}