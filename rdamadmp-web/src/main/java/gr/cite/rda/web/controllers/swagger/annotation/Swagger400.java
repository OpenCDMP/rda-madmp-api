package gr.cite.rda.web.controllers.swagger.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

import static gr.cite.rda.web.controllers.swagger.SwaggerHelpers.Errors.message_400;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@ApiResponse(
        description = "Bad request",
        responseCode = "400",
        content = {
                @Content(
                        mediaType = "application/vnd.org.rd-alliance.dmp-common.v1.3+json",
                        examples = @ExampleObject(
                                value = message_400
                        )
                ),
                @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                value = message_400
                        )
                )
        }
)
public @interface Swagger400 {

}
