package gr.cite.rda.web.controllers.swagger.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

import static gr.cite.rda.web.controllers.swagger.SwaggerHelpers.Errors.message_406;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@ApiResponse(
        description = "The server cannot produce a response that the client requested in its `Accept` header.",
        responseCode = "406",
        content = {
                @Content(
                        mediaType = "application/vnd.org.rd-alliance.dmp-common.v1.3+json",
                        examples = @ExampleObject(
                                value = message_406
                        )
                ),
                @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = message_406
                        )
                )
        }
)
public @interface Swagger406 {

}
