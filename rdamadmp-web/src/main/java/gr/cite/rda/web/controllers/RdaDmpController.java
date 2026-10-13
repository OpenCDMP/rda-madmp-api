package gr.cite.rda.web.controllers;

import gr.cite.rda.audit.AuditableAction;
import gr.cite.rda.authorization.AuthorizationFlags;
import gr.cite.rda.elastic.data.DmpElasticEntity;
import gr.cite.rda.elastic.data.DmpRootElasticEntity;
import gr.cite.rda.elastic.persist.DmpPersist;
import gr.cite.rda.elastic.persist.DmpRootPersist;
import gr.cite.rda.model.builder.DmpRootBuilder;
import gr.cite.rda.model.builder.dmpentity.DmpEntityBuilder;
import gr.cite.rda.model.censorship.RdaDmpCensor;
import gr.cite.rda.model.rdadmp.DmpRootModel;
import gr.cite.rda.model.result.QueryResult;
import gr.cite.rda.query.lookup.DmpLookup;
import gr.cite.rda.service.elastic.ElasticQueryHelperService;
import gr.cite.rda.service.elastic.ElasticService;
import gr.cite.rda.web.controllers.swagger.SwaggerHelpers;
import gr.cite.rda.web.controllers.swagger.annotation.*;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import gr.cite.tools.validation.ValidationFilterAnnotation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidApplicationException;
import java.io.IOException;

@RestController
@RequestMapping(path = "dmps", produces = { MediaType.APPLICATION_JSON_VALUE, RdaDmpController.MEDIA_TYPE_V1_3, RdaDmpController.MEDIA_TYPE_V1_2 })
@Tag(name = "DMP", description = "DMP-related endpoints")
@SwaggerCommonErrorResponses
public class RdaDmpController {

    public static final String MEDIA_TYPE_V1_3 = "application/vnd.org.rd-alliance.dmp-common.v1.3+json";
    public static final String MEDIA_TYPE_V1_2 = "application/vnd.org.rd-alliance.dmp-common.v1.2+json";

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(RdaDmpController.class));

    private final AuditService auditService;
    private final CensorFactory censorFactory;
    private final ElasticQueryHelperService elasticQueryHelperService;
    private final ElasticService elasticService;
    private final BuilderFactory builderFactory;

    public RdaDmpController(
            AuditService auditService,
            CensorFactory censorFactory,
            ElasticQueryHelperService elasticQueryHelperService,
            ElasticService elasticService, BuilderFactory builderFactory) {
        this.auditService = auditService;
        this.censorFactory = censorFactory;
        this.elasticQueryHelperService = elasticQueryHelperService;
        this.elasticService = elasticService;
        this.builderFactory = builderFactory;
    }

    @GetMapping
    @Swagger400
    @Swagger401
    @Swagger406
    @Operation(summary = "List/search DMPs", description = SwaggerHelpers.DMP.endpoint_get_query,
            responses = @ApiResponse(description = "A list of DMPs", responseCode = "200", content = @Content(
                    schema = @Schema(
                            implementation = QueryResult.class
                    ))
            ))
    public QueryResult query(@ParameterObject DmpLookup lookup) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException {
        logger.debug("querying {}", DmpElasticEntity.class.getSimpleName());

        this.censorFactory.censor(RdaDmpCensor.class).censor(null);

        QueryResult queryResult = this.elasticQueryHelperService.collect(lookup, AuthorizationFlags.All);
        this.auditService.track(AuditableAction.Dmp_Query, "q", lookup);

        return queryResult;
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MEDIA_TYPE_V1_3, MEDIA_TYPE_V1_2 })
    @Swagger400
    @Swagger401
    @Swagger403
    @Swagger406
    @Swagger409
    @Transactional
    @Operation(
            summary = "Create a DMP",
            description = SwaggerHelpers.DMP.endpoint_post_query,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "DMP successfully created",
                            headers = {
                                    @Header(
                                            name = "Last-Modified",
                                            description = """
                                            The server should expose the time the DMP was last modified to allow for a conditional PUT request.
                                            This is especially important if the server does not support PATCH requests.
                                            """,
                                            schema = @Schema(type = "string", format = "date-time")
                                    )
                            },
                            content = {
                                    @Content(
                                            mediaType = MEDIA_TYPE_V1_3,
                                            schema = @Schema(implementation = DmpRootModel.class)
                                    ),
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = DmpRootModel.class)
                                    )
                            }
                    )
            }
    )
    @ValidationFilterAnnotation(validator = DmpRootPersist.DmpRootPersistValidator.ValidatorName, argumentName = "persist")
    public ResponseEntity<DmpRootModel> post(@RequestBody DmpRootPersist persist) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, IOException {

        logger.debug(new MapLogEntry("persisting" + DmpPersist.class.getSimpleName()).And("model", persist));


        DmpRootElasticEntity model = new DmpRootElasticEntity();
        DmpElasticEntity dmp = this.builderFactory.builder(DmpEntityBuilder.class).build(null, persist.getDmp());
        model.setDmp(dmp);


        DmpRootElasticEntity entity = this.elasticService.persistRda(model, null, false, null, null);

        this.auditService.track(AuditableAction.Dmp_Persist, "model", model);

        DmpRootModel rootModel = this.builderFactory.builder(DmpRootBuilder.class).build(null, entity);

        HttpHeaders headers = new HttpHeaders();
        if (rootModel.getDmp() != null && rootModel.getDmp().getModified() != null) {
            headers.setLastModified(rootModel.getDmp().getModified().toInstant().toEpochMilli());
        }

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(rootModel);
    }

    @GetMapping("{id}")
    @Swagger401
    @Swagger403
    @Swagger404
    @Swagger406
    @Operation(
            summary = "Get a DMP",
            description = SwaggerHelpers.DMP.endpoint_getId_query,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "DMP retrieved",
                            headers = {
                                    @Header(
                                            name = "Last-Modified",
                                            description = """
                                            The server should expose the time the DMP was last modified to allow for a conditional PUT request.
                                            """,
                                            schema = @Schema(type = "string", format = "date-time")
                                    )
                            },
                            content = {
                                    @Content(
                                            mediaType = MEDIA_TYPE_V1_3,
                                            schema = @Schema(implementation = DmpRootModel.class)
                                    ),
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = DmpRootModel.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<DmpRootModel> get(@Parameter(name = "id", required = true) @PathVariable("id") String id) throws MyApplicationException, MyForbiddenException, MyNotFoundException, IOException {
        logger.debug(new MapLogEntry("retrieving" + DmpElasticEntity.class.getSimpleName()).And("id", id));

        this.censorFactory.censor(RdaDmpCensor.class).censor(null);

        DmpRootElasticEntity entity = this.elasticService.getRda(id);

        DmpRootModel rootModel = this.builderFactory.builder(DmpRootBuilder.class).build(null, entity);

        HttpHeaders headers = new HttpHeaders();
        if (rootModel.getDmp() != null && rootModel.getDmp().getModified() != null) {
            headers.setLastModified(rootModel.getDmp().getModified().toInstant().toEpochMilli());
        }

        this.auditService.track(AuditableAction.Dmp_Query, "id", id);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(rootModel);

    }

    @PutMapping(path = "{id}", consumes = { MediaType.APPLICATION_JSON_VALUE, MEDIA_TYPE_V1_3, MEDIA_TYPE_V1_2 })
    @Swagger400
    @Swagger401
    @Swagger403
    @Swagger404
    @Swagger406
    @Swagger409
    @Swagger415
    @Operation(
            summary = "Overwrite a DMP",
            description = SwaggerHelpers.DMP.endpoint_put_query,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "DMP modified",
                            headers = {
                                    @Header(
                                            name = "Last-Modified",
                                            description = """
                                            The server should expose the time the DMP was last modified to allow for a conditional PUT request. This is especially important if the server does not support PATCH requests.
                                            """,
                                            schema = @Schema(type = "string", format = "date-time")
                                    )
                            },
                            content = {
                                    @Content(
                                            mediaType = MEDIA_TYPE_V1_3,
                                            schema = @Schema(implementation = DmpRootModel.class)
                                    ),
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = DmpRootModel.class)
                                    )
                            }
                    )
            }
    )
    @Transactional
    @ValidationFilterAnnotation(validator = DmpRootPersist.DmpRootPersistValidator.ValidatorName, argumentName = "persist")
    public ResponseEntity<DmpRootModel> persist(@PathVariable("id") String id, @RequestBody DmpRootPersist persist,
                                            @RequestHeader(value = "If-Match", required = false) String ifMatch,
                                            @RequestHeader(value = "If-Unmodified-Since", required = false) String ifModifiedMatch) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, IOException {

        logger.debug(new MapLogEntry("updating" + DmpPersist.class.getSimpleName()).And("id", id).And("model", persist));

        DmpRootElasticEntity model = new DmpRootElasticEntity();
        DmpElasticEntity dmp = this.builderFactory.builder(DmpEntityBuilder.class).build(null, persist.getDmp());
        model.setDmp(dmp);

        DmpRootElasticEntity entity = this.elasticService.persistRda(model, id, false, ifMatch, ifModifiedMatch);

        DmpRootModel rootModel = this.builderFactory.builder(DmpRootBuilder.class).build(null, entity);

        this.auditService.track(AuditableAction.Dmp_Persist, "model", model);

        HttpHeaders headers = new HttpHeaders();
        if (rootModel.getDmp() != null && rootModel.getDmp().getModified() != null) {
            headers.setLastModified(rootModel.getDmp().getModified().toInstant().toEpochMilli());
        }

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(rootModel);
    }

    @DeleteMapping("{id}")
    @Swagger401
    @Swagger403
    @Swagger404
    @Swagger406
    @Swagger409
    @Operation(summary = "Delete a DMP", description = SwaggerHelpers.DMP.endpoint_delete_query,
            responses = @ApiResponse(description = "DMP Deleted", responseCode = "204"
            ))
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable("id") String id) throws MyApplicationException, MyForbiddenException, MyNotFoundException, InvalidApplicationException, IOException {
        logger.debug(new MapLogEntry("deleting" + DmpElasticEntity.class.getSimpleName()).And("id", id));

        this.elasticService.deleteRda(id, false);

        this.auditService.track(AuditableAction.Dmp_Delete, "id", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

