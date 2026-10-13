package gr.cite.rda.web.config;

import gr.cite.rda.authorization.AffiliatedAuthorizationRequirement;
import gr.cite.rda.authorization.AffiliatedResource;
import gr.cite.rda.web.authorization.AffiliatedAuthorizationHandler;
import gr.cite.commons.web.authz.handler.AuthorizationHandler;
import gr.cite.commons.web.authz.policy.AuthorizationRequirement;
import gr.cite.commons.web.authz.policy.AuthorizationRequirementMapper;
import gr.cite.commons.web.authz.policy.AuthorizationResource;
import gr.cite.commons.web.authz.policy.resolver.AuthorizationPolicyConfigurer;
import gr.cite.commons.web.authz.policy.resolver.AuthorizationPolicyResolverStrategy;
import gr.cite.commons.web.oidc.configuration.WebSecurityProperties;
import gr.cite.rda.authorization.OwnedAuthorizationRequirement;
import gr.cite.rda.authorization.OwnedResource;
import gr.cite.rda.web.authorization.OwnedAuthorizationHandler;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final WebSecurityProperties webSecurityProperties;
	private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;
	private final Filter apiKeyFilter;
	private final OwnedAuthorizationHandler ownedAuthorizationHandler;
	private final AffiliatedAuthorizationHandler affiliatedAuthorizationHandler;

	@Autowired
	public SecurityConfiguration(WebSecurityProperties webSecurityProperties,
                                 @Qualifier("tokenAuthenticationResolver") AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
                                 @Qualifier("apiKeyFilter") Filter apiKeyFilter,
                                 @Qualifier("ownedAuthorizationHandler") OwnedAuthorizationHandler ownedAuthorizationHandler,
                                 @Qualifier("affiliatedAuthorizationHandler") AffiliatedAuthorizationHandler affiliatedAuthorizationHandler) {
		this.webSecurityProperties = webSecurityProperties;
		this.authenticationManagerResolver = authenticationManagerResolver;
		this.apiKeyFilter = apiKeyFilter;
		this.ownedAuthorizationHandler = ownedAuthorizationHandler;
		this.affiliatedAuthorizationHandler = affiliatedAuthorizationHandler;
    }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		HttpSecurity tempHttp = http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(httpSecurityCorsConfigurer -> {})
				.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
				.addFilterBefore(apiKeyFilter, AbstractPreAuthenticatedProcessingFilter.class)
				.authorizeHttpRequests(authRequest ->
						// get method permit all is defined only for rda madmp endpoints
						authRequest.requestMatchers(HttpMethod.GET, buildAntPatterns(webSecurityProperties.getAllowedEndpoints())).permitAll()
								.requestMatchers(buildAntPatterns(webSecurityProperties.getAuthorizedEndpoints())).authenticated())
				.sessionManagement( sessionManagementConfigurer-> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER))
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint(this.errorBodyWriter(HttpServletResponse.SC_UNAUTHORIZED, "authentication_required", "Authentication required to perform the specified request"))
						.accessDeniedHandler((request, response, accessDeniedException) ->
								this.writeErrorBody(response, HttpServletResponse.SC_FORBIDDEN, "insufficient_permissions", "The current user has insufficient permissions to perform this action")))
				.oauth2ResourceServer(oauth2 -> oauth2
						.authenticationManagerResolver(authenticationManagerResolver)
						.authenticationEntryPoint(this.errorBodyWriter(HttpServletResponse.SC_UNAUTHORIZED, "authentication_required", "Authentication required to perform the specified request"))
						.accessDeniedHandler((request, response, accessDeniedException) ->
								this.writeErrorBody(response, HttpServletResponse.SC_FORBIDDEN, "insufficient_permissions", "The current user has insufficient permissions to perform this action")));

		return tempHttp.build();
	}

	private AuthenticationEntryPoint errorBodyWriter(int status, String errorCode, String errorMessage) {
		return (request, response, authException) -> this.writeErrorBody(response, status, errorCode, errorMessage);
	}

	private void writeErrorBody(HttpServletResponse response, int status, String errorCode, String errorMessage) throws IOException {
		if (response.isCommitted()) return;
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"error_code\":\"" + errorCode + "\",\"error_message\":\"" + errorMessage + "\"}");
	}

	@Bean
	AuthorizationPolicyConfigurer authorizationPolicyConfigurer() {
		return new AuthorizationPolicyConfigurer() {

			@Override
			public AuthorizationPolicyResolverStrategy strategy() {
				return AuthorizationPolicyResolverStrategy.STRICT_CONSENSUS_BASED;
			}

			//Here you can register your custom authorization handlers, which will get used as well as the existing ones
			//This is optional and can be omitted
			//If not set / set to null, only the default authorization handlers will be used
			@Override
			public List<AuthorizationHandler<? extends AuthorizationRequirement>> addCustomHandlers() {
				return List.of(affiliatedAuthorizationHandler, ownedAuthorizationHandler);
			}

			//Here you can register your custom authorization requirements (if any)
			//This is optional and can be omitted
			//If not set / set to null, only the default authorization requirements will be used
			@Override
			public List<? extends AuthorizationRequirement> extendRequirements() {
				return List.of(
//                        new TimeOfDayAuthorizationRequirement(new TimeOfDay("08:00","16:00"), true)
				);
			}

			//Here you can select handlers you want to disable by providing the classes they are implemented by
			//You can disable any handler (including any custom one)
			//This is optional and can be omitted
			//If not set / set to null, all the handlers will be invoked, based on their requirement support
			//In the example below, the default client handler will be ignored by the resolver
			@Override
			public List<Class<? extends AuthorizationHandler<? extends AuthorizationRequirement>>> disableHandlers() {
				return new ArrayList<>();
			}
		};
	}

	@Bean
	AuthorizationRequirementMapper authorizationRequirementMapper() {
		return new AuthorizationRequirementMapper() {
			@Override
			public AuthorizationRequirement map(AuthorizationResource resource, boolean matchAll, String[] permissions) {
				Class<?> type = resource.getClass();
				if (!AuthorizationResource.class.isAssignableFrom(type)) throw new IllegalArgumentException("resource");

				if (OwnedResource.class.equals(type)) {
					return new OwnedAuthorizationRequirement();
				}
				if (AffiliatedResource.class.equals(type)) {
					return new AffiliatedAuthorizationRequirement(matchAll, permissions);
				}
				throw new IllegalArgumentException("resource");
			}
		};
	}

	private String[] buildAntPatterns(Set<String> endpoints) {
		if (endpoints == null) {
			return new String[0];
		}
		return endpoints.stream()
				.filter(endpoint -> endpoint != null && !endpoint.isBlank())
				.map(endpoint -> "/" + stripUnnecessaryCharacters(endpoint) + "/**")
				.toArray(String[]::new);
	}

	private String stripUnnecessaryCharacters(String endpoint) {
		endpoint = endpoint.strip();
		if (endpoint.startsWith("/")) {
			endpoint = endpoint.substring(1);
		}
		if (endpoint.endsWith("/")) {
			endpoint = endpoint.substring(0, endpoint.length() - 1);
		}
		return endpoint;
	}
}
