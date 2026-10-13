package gr.cite.rda.service.elastic;


import gr.cite.rda.elastic.data.DmpRootElasticEntity;

import java.io.IOException;

public interface ElasticService {
    boolean existsRdaIndex() throws IOException;

    void ensureRdaIndex() throws IOException;

    DmpRootElasticEntity getRda(String id) throws IOException;

    DmpRootElasticEntity persistRda(DmpRootElasticEntity dmp, String id, boolean isFromQueue, String ifMatch, String ifModifiedMatch) throws IOException;

    void deleteRda(String id, boolean isFromQueue) throws IOException;

	void deleteRdaIndex() throws IOException;

}
