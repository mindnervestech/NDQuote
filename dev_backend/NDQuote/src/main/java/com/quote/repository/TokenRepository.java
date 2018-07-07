package com.quote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quote.entities.Tokens;
import com.quote.entities.auth.AuthUser;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Long> {
	List<Tokens> findByDocumentAndUserAndAuthorised(String document,AuthUser user,boolean authorised);
	List<Tokens> findByUser(AuthUser user);
}
