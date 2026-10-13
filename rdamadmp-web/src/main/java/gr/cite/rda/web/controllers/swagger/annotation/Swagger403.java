package gr.cite.rda.web.controllers.swagger.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

import static gr.cite.rda.web.controllers.swagger.SwaggerHelpers.Errors.message_401;
import static gr.cite.rda.web.controllers.swagger.SwaggerHelpers.Errors.message_403;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@ApiResponse(
        description = "Insufficient permissions to delete the specified a DMP. Note: the implementation may opt to return a 404 instead to avoid leaking information about the existence of a DMP with the specified ID if the user has no permissions to at least view this DMP. The implementation SHOULD return this error code if the user has at least read permissions for this DMP.",
        responseCode = "403",
        content = {
                @Content(
                        mediaType = "application/vnd.org.rd-alliance.dmp-common.v1.3+json",
                        examples = @ExampleObject(
                                value = message_403
                        )
                ),
                @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = message_403
                        )
                )
        }
)
public @interface Swagger403 {

}
