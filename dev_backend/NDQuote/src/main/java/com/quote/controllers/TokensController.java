package com.quote.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.quote.services.TokensService;
import com.quote.vm.MessageVM;

@RestController
@RequestMapping("/ndquote/api")
public class TokensController {

	@Autowired
	TokensService tokensService;
	
	@RequestMapping(value = "/tokens/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public MessageVM getAllTokens(@PathVariable Long userId) {
		return tokensService.getAllTokens(userId);
	}
	
	@RequestMapping(value = "/tokens/{userId}/{documentId}", method = RequestMethod.GET)
	@ResponseBody
	public MessageVM getToken(@PathVariable Long userId,@PathVariable Long documentId) {
		return tokensService.getToken(userId,documentId);
	}
	
	
	@RequestMapping(value = "tokens/check/{tokenId}/{tokenString}", method = RequestMethod.GET)
	@ResponseBody
	public MessageVM checkTokensValid(@PathVariable Long tokenId,@PathVariable String tokenString) {
		return tokensService.checkTokensValid(tokenId,tokenString);
	}
}
