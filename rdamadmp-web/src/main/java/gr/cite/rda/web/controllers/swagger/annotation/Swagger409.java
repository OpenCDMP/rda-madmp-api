package gr.cite.rda.web.controllers.swagger.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

import static gr.cite.rda.web.controllers.swagger.SwaggerHelpers.Errors.message_409;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@ApiResponse(
        description = "The request conflicts with the current state of the resource. `dmp_stale`: a precondition specified in the If-Unmodified-Since or If-Match header cannot be fulfilled; the client should apply the modifications again and resubmit the request. `dmp_already_exists`: the DMP is already present in the system. `conflict`: any other conflict.",
        responseCode = "409",
        content = {
                @Content(
                        mediaType = "application/vnd.org.rd-alliance.dmp-common.v1.3+json",
                        examples = @ExampleObject(
                                value = message_409
                        )
                ),
                @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = message_409
                        )
                )
        }
)
public @interface Swagger409 {

}
