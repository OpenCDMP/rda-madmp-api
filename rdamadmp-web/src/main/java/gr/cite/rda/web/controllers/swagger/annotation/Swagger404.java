package gr.cite.rda.web.controllers.swagger.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

import static gr.cite.rda.web.controllers.swagger.SwaggerHelpers.Errors.message_404;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@ApiResponse(
        description = "DMP not found or not enough permissions to delete the specified DMP. Security note: implementations may return this error code even if the DMP exists but the authenticated user has no permissions to view it in order to avoid leaking information about the existence of a DMP. This may especially be important if the DMP tool uses numeric IDs to avoid enabling an enumeration of possible DMP IDs.",
        responseCode = "404",
        content = {
                @Content(
                        mediaType = "application/vnd.org.rd-alliance.dmp-common.v1.3+json",
                        examples = @ExampleObject(
                                value = message_404
                        )
                ),
                @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = message_404
                        )
                )
        }
)
public @interface Swagger404 {

}
