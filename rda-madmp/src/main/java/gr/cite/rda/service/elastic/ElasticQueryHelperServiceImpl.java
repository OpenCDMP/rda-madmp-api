package gr.cite.rda.service.elastic;

import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.elastic.data.DmpElasticEntity;
import gr.cite.rda.elastic.data.DmpRootElasticEntity;
import gr.cite.rda.elastic.query.DmpRootElasticQuery;
import gr.cite.rda.model.builder.DmpBuilder;
import gr.cite.rda.model.builder.DmpRootBuilder;
import gr.cite.rda.model.result.QueryResult;
import gr.cite.rda.query.lookup.DmpLookup;
import gr.cite.rda.service.rdadmp.RdaDmpServiceProperties;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
public class ElasticQueryHelperServiceImpl implements ElasticQueryHelperService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(ElasticQueryHelperServiceImpl.class));

	private final QueryFactory queryFactory;
	private final BuilderFactory builderFactory;
	private final RdaDmpServiceProperties rdaDmpServiceProperties;

	public ElasticQueryHelperServiceImpl(QueryFactory queryFactory, BuilderFactory builderFactory, RdaDmpServiceProperties rdaDmpServiceProperties) {
		this.queryFactory = queryFactory;
        this.builderFactory = builderFactory;
        this.rdaDmpServiceProperties = rdaDmpServiceProperties;
    }


	@Override
	public QueryResult collect(DmpLookup lookup, EnumSet<AuthorizationFlags> flags) {
		QueryResult result = new QueryResult();

		DmpRootElasticQuery query = lookup.enrich(this.queryFactory, this.rdaDmpServiceProperties.isUseActionType()).authorize(flags);
		List<DmpRootElasticEntity> elasticResponse = query.collect();

		result.setItems(this.builderFactory.builder(DmpRootBuilder.class).authorize(flags).build(null,elasticResponse));
		// total_count must reflect the total number of matching DMPs, not the page size, so clients can paginate with offset
		Long totalCount = query.count();
		result.setCount(totalCount == null ? elasticResponse.size() : totalCount.intValue());
		return result;
	}

//	@Override
//	public QueryResult collectId(String id, EnumSet<AuthorizationFlags> flags) {
//		QueryResult result = new QueryResult();
//
//		DmpLookup lookup = new DmpLookup();
//		lookup.setDmp_ids(List.of(id));
//
//		List<DmpElasticEntity> elasticResponse = lookup.enrich(this.queryFactory).authorize(flags).collect();
//
//		result.setItems(elasticResponse);
//		result.setCount(elasticResponse.size());
//		return result;
//	}

}
