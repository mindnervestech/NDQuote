package com.quote.auth;


import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.quote.entities.auth.AuthUser;
import com.quote.vm.LoginVM;
import com.quote.vm.RegistrationVM;


@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	AuthenticationProvider authenticationProvider;

	@Autowired
	UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@Transactional
	public LoginResponse login(@RequestBody LoginVM user ,  HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		System.out.println("user  :: " + user.getUsername() + "  " +  user.getPassword() );
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
		AuthUser details = new AuthUser();
		details.setUsername(user.getUsername());
		token.setDetails(details);
		Authentication auth = authenticationProvider.authenticate(token);
		System.out.println("user is present");
		return new LoginResponse(TokenHandler.createTokenForUser(auth),((AuthUser)auth.getPrincipal()).getId());	
	}

	@SuppressWarnings("unused")
	private static class UserLogin {
		public String username;
		public String password;
	}

	@SuppressWarnings("unused")
	private static class LoginResponse {
		public String token;
		public Long userId;
		public LoginResponse(final String token,final Long userId) {
			this.token = token;
			this.userId=userId;
		}
	}
	
	@RequestMapping(value = "/sociallogin", method = RequestMethod.POST)
	@Transactional
	public LoginResponse socialRegistration(@RequestBody RegistrationVM  user ,  HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		AuthUser authUser = userService.getUserByUserNameAndProvider(user.getUserName(),user.getProvider());
		if(authUser == null)
		{
			authUser =new AuthUser();
			authUser.setEmail(user.getEmail());
			authUser.setUsername(user.getUserName());
			authUser.setLastName(user.getLastName());
			authUser.setFirstName(user.getFirstName());
			authUser.setProvider(user.getProvider());
			authUser.setProviderId(user.getProviderId());
			authUser.setProfile_pic(user.getProfilePic());
			authUser.setPassword("");
			authUser.setCreatedDate(new Date());
			authUser.setEnabled(true);
			userService.createUser(authUser);
			System.out.println("user not present,its created");
		}else{
			//userService.UpdateAuthUserById(authUser.getId(), user.getGcmId() ,user.getDeviceType());
			System.out.println("user is present");
		}
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authUser.getUsername(),"");
		AuthUser details = new AuthUser();
		details.setUsername(authUser.getUsername());
		token.setDetails(details);
		Authentication auth = authenticationProvider.authenticate(token);
		return new LoginResponse(TokenHandler.createTokenForUser(auth),((AuthUser)auth.getPrincipal()).getId());	
	}
}
