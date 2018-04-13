package com.quote.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quote.entities.auth.AuthUser;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long>  {

//	public AuthUser getAuthUserByUserName () {
//		return null;
//	}
}
