package gr.cite.rda.web.controllers.swagger;

public final class SwaggerHelpers {

    public static final class Commons {

        public static final String fieldset_description =
                """
                        A list of the properties you wish to include in the response, similar to the 'project' attribute on queries.
                        """;

        public static final String pagination_example =
                """
                        Pagination and projection
                        """;

        public static final String pagination_example_description =
                """
                        Simple paginated request using a property projection list and pagination info
                        """;

        public static final String pagination_response_example =
                """
                        Pagination and projection
                        """;

        public static final String pagination_response_example_description =
                """
                        Simple paginated request using a property projection list and pagination info
                        """;
    }

    public static final class Errors {

        public static final String message_400 =
                """
                        {
                          "error_message": "string"
                        }
                        """;



        public static final String message_401 =
                """
                        {
                          "error_message": "string",
                          "error_code": "authentication_required"
                        }
                        """;

        public static final String message_403 =
                """
                        {
                          "error_message": "string",
                          "error_code": "insufficient_permissions"
                        }
                        """;

        public static final String message_404 =
                """
                        {
                          "error_message": "string",
                          "error_code": "dmp_not_found"
                        }
                        """;

        public static final String message_406 =
                """
                        {
                          "error_message": "string",
                          "error_code": "not_acceptable"
                        }
                        """;

        public static final String message_409 =
                """
                        {
                          "error_message": "string",
                          "error_code": "dmp_stale"
                        }
                        """;

        public static final String message_415 =
                """
                        {
                          "error_message": "string",
                          "error_code": "unsupported_media_type"
                        }
                        """;


        public static final String message_generic =
                """
                        {
                          "error_message": "string",
                          "error_code": "generic_error"
                        }
                        """;

    }

    public static final class DMP {

        public static final String endpoint_post_query =
                """
                        Create or import a DMP from a JSON-formed DMP object.<br/>
                        The implementation may opt to allow the creation of draft DMPs with formally valid, but incomplete fields.<br/>
                        Alternatively, the implementation may reject incomplete DMPs if it cannot store them.<br/>
                        """;

        public static final String endpoint_get_query =
                """
                        This endpoint lists all DMPs or allows for creating a filtered list. When filters are provided, all filter are applied (AND relationship). For filters supporting lists, the individual values are applied as an OR relationship.<br/>
                        For items accepting more than one value you may pass multiple values by repeating the parameter in the query string for each item.
                        """;

        public static final String endpoint_getId_query =
                """
                        Fetch a DMP based on its ID.
                        """;

        public static final String endpoint_put_query =
                """
                        Completely overwrite a DMP with the specified data. The server MUST honor the If-Unmodified-Since and IF-Match headers if present.
                        """;

        public static final String endpoint_delete_query =
                """
                        Delete a DMP based on its ID.
                        """;
    }
}
