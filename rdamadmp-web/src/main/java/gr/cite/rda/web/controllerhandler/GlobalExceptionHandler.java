package gr.cite.rda.web.controllerhandler;

import gr.cite.rda.common.JsonHandlingService;
import gr.cite.rda.common.exception.BadRequestException;
import gr.cite.rda.common.exception.ConflictException;
import gr.cite.rda.common.exception.DmpStaleException;
import gr.cite.rda.common.exception.InvalidQueryStringException;
import gr.cite.tools.exception.*;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import tools.jackson.databind.ObjectMapper;

import java.text.MessageFormat;
import java.util.Map;

@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(GlobalExceptionHandler.class));
	
	private final JsonHandlingService jsonHandlingService;

	public GlobalExceptionHandler(JsonHandlingService jsonHandlingService) {
		this.jsonHandlingService = jsonHandlingService;
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleUnexpectedErrors(Exception exception, WebRequest request) throws Exception {
		HandledException handled = this.handleException(exception, request);
		this.log(handled.getLevel(), exception, MessageFormat.format("returning code {0} and payload {1}", handled.getStatusCode(), handled.getMessage()));
		return ResponseEntity.status(handled.getStatusCode())
				.contentType(MediaType.APPLICATION_JSON)
				.body(handled.getMessage());
    }

	public void log(System.Logger.Level level, Exception e, String message) {
		if (level != null) {
			switch (level) {
				case TRACE:
					logger.trace(message, e);
					break;
				case DEBUG:
					logger.debug(message, e);
					break;
				case INFO:
					logger.info(message, e);
					break;
				case WARNING:
					logger.warn(message, e);
					break;
				case ERROR:
					logger.error(message, e);
					break;
			}
		}
	}

	public HandledException handleException(Exception exception, WebRequest request) throws Exception {
		HttpStatus statusCode;
		Map<String, Object> result;
		System.Logger.Level logLevel;

		switch (exception){
			case MyNotFoundException myNotFoundException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.NOT_FOUND;
				result = Map.ofEntries(
						Map.entry("error_message", myNotFoundException.getMessage()),
						Map.entry("error_code", "dmp_not_found")
				);
			}
			case MyUnauthorizedException myUnauthorizedException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.UNAUTHORIZED;
				result = Map.ofEntries(
						Map.entry("error_message", myUnauthorizedException.getMessage()),
						Map.entry("error_code", "authentication_required")
				);
			}
			case MyForbiddenException myForbiddenException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.FORBIDDEN;
				result = Map.ofEntries(
						Map.entry("error_message", myForbiddenException.getMessage()),
						Map.entry("error_code", "insufficient_permissions")
				);
			}
			case MyValidationException myValidationException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.BAD_REQUEST;
				result = Map.ofEntries(
						Map.entry("error_message", new ObjectMapper().writeValueAsString(myValidationException.getErrors())),
						Map.entry("error_code", "dmp_invalid")
				);
			}
			case InvalidQueryStringException invalidQueryStringException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.BAD_REQUEST;
				result = Map.ofEntries(
						Map.entry("error_message", invalidQueryStringException.getMessage()),
						Map.entry("error_code", "invalid_query_string")
				);
			}
			case BadRequestException badRequestException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.BAD_REQUEST;
				result = Map.ofEntries(
						Map.entry("error_message", badRequestException.getMessage()),
						Map.entry("error_code", "bad_request")
				);
			}
			case BindException bindException -> {
				// query-parameter binding failures (wrong type, unparseable value)
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.BAD_REQUEST;
				result = Map.ofEntries(
						Map.entry("error_message", bindException.getMessage()),
						Map.entry("error_code", "invalid_query_string")
				);
			}
			case TypeMismatchException typeMismatchException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.BAD_REQUEST;
				result = Map.ofEntries(
						Map.entry("error_message", typeMismatchException.getMessage()),
						Map.entry("error_code", "invalid_query_string")
				);
			}
			case ServletRequestBindingException servletRequestBindingException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.BAD_REQUEST;
				result = Map.ofEntries(
						Map.entry("error_message", servletRequestBindingException.getMessage()),
						Map.entry("error_code", "bad_request")
				);
			}
			case HttpMessageNotReadableException httpMessageNotReadableException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.BAD_REQUEST;
				result = Map.ofEntries(
						Map.entry("error_message", httpMessageNotReadableException.getMessage()),
						Map.entry("error_code", "bad_request")
				);
			}
			case HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
				result = Map.ofEntries(
						Map.entry("error_message", httpMediaTypeNotSupportedException.getMessage()),
						Map.entry("error_code", "unsupported_media_type")
				);
			}
			case MyApplicationException myApplicationException -> {
				logLevel = System.Logger.Level.ERROR;
				statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
				result = Map.ofEntries(
						Map.entry("error_message", myApplicationException.getMessage()),
						Map.entry("error_code", "generic_error")
				);
			}
			case HttpMediaTypeNotAcceptableException httpMediaTypeNotAcceptableException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.NOT_ACCEPTABLE;
				result = Map.ofEntries(
						Map.entry("error_message", httpMediaTypeNotAcceptableException.getMessage()),
						Map.entry("error_code", "not_acceptable")
				);
			}
			case DmpStaleException dmpStaleException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.CONFLICT;
				result = Map.ofEntries(
						Map.entry("error_message", dmpStaleException.getMessage()),
						Map.entry("error_code", "dmp_stale")
				);
			}
			case ConflictException conflictException -> {
				logLevel = System.Logger.Level.DEBUG;
				statusCode = HttpStatus.CONFLICT;
				result = Map.ofEntries(
						Map.entry("error_message", conflictException.getMessage()),
						Map.entry("error_code", "conflict")
				);
			}
			default ->  {
				logLevel = System.Logger.Level.ERROR;
				statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
				result = Map.ofEntries(
						Map.entry("error_message", exception.getMessage()),
						Map.entry("error_code", "generic_error")
				);
			}
		}
		return new HandledException(statusCode, result, logLevel);
	}
	
	public static class HandledException{
		public HttpStatus statusCode;
		public Map<String, Object> message;
		public System.Logger.Level level;

		public HandledException(HttpStatus statusCode, Map<String, Object> message, System.Logger.Level level) {
			this.statusCode = statusCode;
			this.message = message;
			this.level = level;
		}

		public HttpStatus getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(HttpStatus statusCode) {
			this.statusCode = statusCode;
		}

		public Map<String, Object> getMessage() {
			return message;
		}

		public void setMessage(Map<String, Object> message) {
			this.message = message;
		}

		public System.Logger.Level getLevel() {
			return level;
		}

		public void setLevel(System.Logger.Level level) {
			this.level = level;
		}
	}
}
