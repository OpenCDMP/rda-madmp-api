package gr.cite.rda.web.controllerhandler;

import gr.cite.rda.common.enums.DataAccessType;
import gr.cite.rda.common.enums.PidSystemType;
import gr.cite.rda.model.rdadmp.DatasetModel;
import gr.cite.rda.model.rdadmp.DistributionModel;
import gr.cite.rda.model.rdadmp.DmpRootModel;
import gr.cite.rda.model.rdadmp.HostIdModel;
import gr.cite.rda.model.rdadmp.HostModel;
import gr.cite.rda.model.result.QueryResult;
import gr.cite.rda.web.controllers.RdaDmpController;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.net.URI;
import java.util.List;

// the Common maDMP API requires responses served as v1.2 to be valid DMP Common Standard 1.2 documents
@RestControllerAdvice(assignableTypes = RdaDmpController.class)
public class DmpCommonV12ResponseAdvice implements ResponseBodyAdvice<Object> {

	private static final MediaType V1_2 = MediaType.valueOf(RdaDmpController.MEDIA_TYPE_V1_2);

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		if (!V1_2.equalsTypeAndSubtype(selectedContentType)) return body;

		if (body instanceof DmpRootModel root) this.toV12(root);
		else if (body instanceof QueryResult result && result.getItems() != null) result.getItems().forEach(this::toV12);
		return body;
	}

	private void toV12(DmpRootModel root) {
		if (root.getDmp() == null || root.getDmp().getDataset() == null) return;
		for (DatasetModel dataset : root.getDmp().getDataset()) {
			if (dataset.getDistribution() == null) continue;
			for (DistributionModel distribution : dataset.getDistribution()) this.toV12(distribution);
		}
	}

	private void toV12(DistributionModel distribution) {
		if (distribution.getDataAccess() == DataAccessType.RESTRICTED) distribution.setDataAccess(DataAccessType.SHARED);

		HostModel host = distribution.getHost();
		if (host == null) return;

		if (host.getPidSystem() != null) {
			host.setPidSystem(host.getPidSystem().stream()
					.map(x -> PidSystemType.of(x) != null ? x : PidSystemType.OTHER.getValue())
					.distinct()
					.toList());
		}

		if (host.getUrl() == null) {
			URI url = this.urlFromHostId(host.getHostId());
			if (url != null) host.setUrl(url);
			else distribution.setHost(null);
		}
	}

	private URI urlFromHostId(List<HostIdModel> hostIds) {
		if (hostIds == null) return null;
		for (HostIdModel hostId : hostIds) {
			if (hostId.getIdentifier() == null) continue;
			try {
				URI uri = URI.create(hostId.getIdentifier().trim());
				if (uri.getHost() != null && ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) return uri;
			} catch (IllegalArgumentException ignored) {
			}
		}
		return null;
	}
}
