package gr.cite.rda.web.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Common Machine-Actionable DMP API",
                version = "1.0.0",
                description = """
                    This API provides access to Data Management Plans in a machine-readable format. You can use this API to perform
                    automated analysis or other integration. Please note that this API may not give you all information stored in the
                    system implementing it as this standard is intended as a common baseline among multiple DMP platform vendors.
                    
                    # Schema extension
                    
                    This API supports extending the standard by using content negotiation. The MIME types used for content negotiation
                    *should* follow the vendor tree naming defined in [RFC 6838 Section 3.2](https://datatracker.ietf.org/doc/html/rfc6838#section-3.2) and make use of the media type suffixes defined
                    in [RFC6839](https://www.rfc-editor.org/rfc/rfc6839). The vendor prefix should be chosen such that the vendor is clearly identifiable. For example:
                    
                    `application/vnd.com.example.dmp+json`
                    
                    Clients *should* send an `Accept` header with their preferred MIME types. The server *must* order the contents of
                    the `Accept` header correctly and select the standard with the highest client preference. (See the [MDN docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept) for a detailed
                    explanation on this header.)
                   
                    If the client sends no `Accept` header or sends an unqualified `application/json` request, the server *may* select
                    the "stock" schema represented in this document or may select an extended schema only if the extended schema fully
                    adheres to this standard with only extra added fields, but no existing fields removed or modified. In this case, the
                    server *must* respond with the `application/json` content type. Clients *should* not rely on receiving an extended
                    schema document when sending an `application/json` request and *should* prefer sending a qualified MIME type.
                
                    The correct MIME-type for the DMP standard used in this specification is:
                    
                    `application/vnd.org.rd-alliance.dmp-common.v1.3+json`

                    # Supported versions of the DMP Common Standard

                    This specification describes version 1.3 of the RDA DMP Common Standard. This server also supports version 1.2
                    via `application/vnd.org.rd-alliance.dmp-common.v1.2+json`: 1.2 request bodies are processed as 1.3, and 1.2
                    responses are converted by mapping `data_access` value `restricted` to `shared`, mapping `pid_system` values not
                    allowed in 1.2 to `other`, and, for a `host` without `url`, using a URL from `host_id` if available or otherwise
                    omitting the `host`. Any other version is rejected with `406 Not Acceptable` (for `Accept`) or
                    `415 Unsupported Media Type` (for `Content-Type`).

                    Client and server implementations *may* agree on a different serialization to JSON, such as XML, by specifying the
                    corresponding suffix. Clients and servers *must* support JSON serialization.

                    # Optimistic concurrency for updates

                    This API makes use of the `Last-Modified` header in order to detect conflicting updates. When updating a DMP,
                    consider passing the `Last-Modified` header from the response in the `If-Unmodified-Since` header in the PUT
                    request. If the DMP has been modified since you last fetched the data, the server will return a 409 Conflict
                    response (`dmp_stale`).

                    Server implementations should always update the modified field of the DMP to the server time on PUT and return it
                    in the `Last-Modified` header. On PUT, server implementations should compare the `If-Unmodified-Since` header with
                    the stored modified field.
                    """,
                license = @License(
                        name = "Creative Commons Zero v1.0 Universal",
                        identifier = "CC0-1.0"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "RDA DMP Common Standards WG",
                url = "https://github.com/RDA-DMP-Common"
        )
)
public class OpenApiInfoConfig {
}
