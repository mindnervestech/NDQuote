package com.quote.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationService {

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	private final TokenHandler tokenHandler;

	@Autowired
	public TokenAuthenticationService(UserService userService) {
		tokenHandler = new TokenHandler(userService);
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		final String token = request.getHeader(AUTH_HEADER_NAME);
		if (token != null) {
			String permission = TokenHandler.getPermissions(token);
			String role = TokenHandler.getRoles(token);
			String requestedObject = request.getMethod() + ":" + request.getRequestURI();
			System.out.println("permission-->"+permission);
			System.out.println("requestedObject-->"+requestedObject);
			System.out.println("role-->"+role);
			final UserDetails user = tokenHandler.parseUserFromToken(token);
			return new UserAuthentication(user);
		}
		return null;
	}
}

