package org.project.example.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	static Logger log = LogManager.getLogger(AuthenticationFilter.class);

	private static final String REALM = "example";
	private static final String AUTHENTICATION_SCHEME = "Bearer";

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		System.out.println("Into the Authentication filter!!!!");

		Method method = resourceInfo.getResourceMethod();

		if (!method.isAnnotationPresent(PermitAll.class)) {
			// Access denied for all
			if (method.isAnnotationPresent(DenyAll.class)) {
				System.out.println("Method DenyAll!");
				abortWithUnauthorized(requestContext);
				return;
			}

			// For now on, need to check the user

			// Get the Authorization header from the request
			String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

			System.out.println("authorizationHeader:" + authorizationHeader);
			// Validate the Authorization header
			if (!isTokenBasedAuthentication(authorizationHeader)) {
				abortWithUnauthorized(requestContext);
				return;
			}

			// Extract the token from the Authorization header
			String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

			final Claims claims;

			try {

				// Validate the token
				claims = validateToken(token);
			} catch (Exception e) {
				abortWithUnauthorized(requestContext);
				return;
			}

			// Verify user authorization
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
				Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
				try {

					// Validate the user permissions
					checkPermissions(rolesSet);
				} catch (Exception e) {
					abortWithUnauthorized(requestContext);
					return;
				}

				final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
				requestContext.setSecurityContext(new SecurityContext() {

					@Override
					public Principal getUserPrincipal() {
						return new Principal() {

							@Override
							public String getName() {
								return claims.getId();
							}
						};

					}

					@Override
					public boolean isUserInRole(String role) {
						return true;
					}

					@Override
					public boolean isSecure() {
						return currentSecurityContext.isSecure();
					}

					@Override
					public String getAuthenticationScheme() {
						return AUTHENTICATION_SCHEME;
					}
				});

			}
		}
		System.out.println("Method PermitAll!");

	}

	private boolean isTokenBasedAuthentication(String authorizationHeader) {

		// Check if the Authorization header is valid
		// It must not be null and must be prefixed with "Bearer" plus a whitespace
		// The authentication scheme comparison must be case-insensitive
		return authorizationHeader != null
				&& authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
	}

	private void abortWithUnauthorized(ContainerRequestContext requestContext) {

		// Abort the filter chain with a 401 status code response
		// The WWW-Authenticate header is sent along with the response
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"").build());
	}

	private Claims validateToken(String token) throws Exception {
		// Check if the token was issued by the server and if it's not expired
		// Throw an Exception if the token is invalid
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(ApiKey.SECRET_KEY))
				.parseClaimsJws(token).getBody();
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
		System.out.println("Issuer: " + claims.getIssuer());
		System.out.println("Expiration: " + claims.getExpiration());
		return claims;

	}

	private void checkPermissions(Set<String> allowedRoles) throws Exception {
		// Check if the user contains one of the allowed roles
		// Throw an Exception if the user has not permission to execute the method
	}

}
