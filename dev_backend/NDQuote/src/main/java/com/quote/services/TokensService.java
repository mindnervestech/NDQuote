package com.quote.services;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quote.entities.Tokens;
import com.quote.entities.XMLDocument;
import com.quote.entities.auth.AuthUser;
import com.quote.repository.AuthUserRepository;
import com.quote.repository.TokenRepository;
import com.quote.repository.XMLDocumentRepository;
import com.quote.vm.MessageVM;

@Service
public class TokensService {
	
	@Autowired
	AuthUserRepository authUserRepository;
	
	
	@Autowired
	XMLDocumentRepository xmlDocumentRepository;

	@Autowired
	TokenRepository tokenRepository;

	public MessageVM getAllTokens(Long userId) {
		AuthUser user = authUserRepository.findOne(userId);
		MessageVM messageVm = new MessageVM();
		messageVm.setData(tokenRepository.findByUser(user));
		return messageVm;
	}

	public MessageVM checkTokensValid(Long tokenId, String tokenString) {
		MessageVM messageVm = new MessageVM();
		Tokens token =  tokenRepository.findOne(tokenId);
		if (token != null) {
			if (token.getToken().equals(tokenString)) {
				token.setAuthorised(true);
				messageVm.setMessage("Valid");
			} else {
				token.setAuthorised(false);
				messageVm.setMessage("Invalid");
			}
			tokenRepository.save(token);
		} else {
			messageVm.setMessage("Invalid");
		}
		return messageVm;
	}

	public MessageVM getToken(Long userId, Long documentId) {
		MessageVM messageVm = new MessageVM();
		List<Tokens> list = new ArrayList<>();
	    XMLDocument  xmlDocument = xmlDocumentRepository.findOne(documentId);
		if (xmlDocument != null) {
			Tokens  token = xmlDocument.getToken();
			list.add(token);
		}
		messageVm.setData(list);
		return messageVm;
	}
	
}
