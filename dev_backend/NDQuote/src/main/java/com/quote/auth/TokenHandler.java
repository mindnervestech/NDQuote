package com.quote.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.quote.entities.auth.AuthUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public final class TokenHandler {

	public static String SECRET_KEY = UUID.randomUUID().toString();
    private final UserService userService;

    public TokenHandler(UserService userService) {
        this.userService = userService;
    }

    public UserDetails parseUserFromToken(String token) {
        String username = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().get("subject").toString();
                //.getSubject();
        return userService.loadUserByUsername(username);
    }

    public static String createTokenForUser(Authentication user) {
    	Map<String, Object> map = new HashMap<>();
    	map.put("userId", ((AuthUser)user.getPrincipal()).getId());
    	map.put("roles", user.getAuthorities().toString());
    	map.put("permissions",((AuthUser)user.getPrincipal()).getPermissions().toString());
    	map.put("subject",user.getName());
    	//map.put("learnerName",((AuthUser)user.getPrincipal()).getFirstName());
        return Jwts.builder()
                .setSubject(user.getName())
                //.claim("permissions",((AuthUser)user.getDetails()).getPermissions())
                //.claim("roles", user.getAuthorities().toString())
                .setClaims(map)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    
    public static String getRoles(String token) {
    	Claims claims = Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(token)
        .getBody();
    	return claims.get("roles").toString();
    }
    
    public static Long getUserId(String token) {
    	Claims claims = Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(token)
        .getBody();
    	return Long.parseLong(claims.get("userId").toString());
    }
   
    public static String createTokenForUser(String user) {
        return Jwts.builder()
                .setSubject(user)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

	public static String getPermissions(String token) {
		Claims claims = Jwts.parser()
		        .setSigningKey(SECRET_KEY)
		        .parseClaimsJws(token)
		        .getBody();
		    	return claims.get("permissions").toString();
		
	}
}
