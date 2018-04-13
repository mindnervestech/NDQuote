package com.quote.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.quote.services.ProfileService;
import com.quote.vm.MessageVM;

@RestController
@RequestMapping("/ndquote/api/profile")
public class ProfileController {

	@Autowired
	ProfileService profileService; 
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public MessageVM getProfile(@PathVariable Long id) {
		System.out.println("User Id :: " +  id);
		return profileService.getProfile(id);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public MessageVM createProfile() {
		return null;
	}
	
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public MessageVM updatedProfile() {
		return null;
	}
	
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public MessageVM deleteProfile() {
		return null;
	}
}
