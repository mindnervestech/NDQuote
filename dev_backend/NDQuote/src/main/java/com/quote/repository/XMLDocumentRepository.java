package com.quote.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quote.entities.XMLDocument;
import com.quote.entities.auth.AuthUser;

@Repository
public interface XMLDocumentRepository extends JpaRepository<XMLDocument, Long>{
	List<XMLDocument> findByUser(AuthUser user); 
	List<XMLDocument> findByInfNFeId(String infNFeId); 
	List<XMLDocument> findByDocumentAndEmail(String document,String email);
}
