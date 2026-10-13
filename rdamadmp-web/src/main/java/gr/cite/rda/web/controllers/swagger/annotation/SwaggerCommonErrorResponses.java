package gr.cite.rda.web.controllers.swagger.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@SwaggerGeneric
public @interface SwaggerCommonErrorResponses {

}
