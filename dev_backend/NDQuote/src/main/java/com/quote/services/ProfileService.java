package com.quote.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quote.entities.auth.AuthUser;
import com.quote.repository.AuthUserRepository;
import com.quote.vm.MessageVM;
import com.quote.vm.UserProfileVM;

@Service
public class ProfileService {

	@Autowired
	AuthUserRepository authUserRepository; 
	
	public MessageVM getProfile (Long id) {
		AuthUser user = authUserRepository.getOne(id);
		MessageVM messageVM = new MessageVM();
		if (user != null) {
			UserProfileVM profileVM = new UserProfileVM();
			profileVM.setId(user.getId());
			profileVM.setEmail(user.getEmail());
			profileVM.setFirstName(user.getFirstName());
			profileVM.setLastName(user.getLastName());
			profileVM.setProfilePic(user.getProfile_pic());
			messageVM.setData(profileVM);
			messageVM.setCode("200");
			messageVM.setMessage("User information.");
		} else {
			messageVM.setCode("404");
			messageVM.setMessage("User not found.");
		}
		return messageVM;
	}
	public MessageVM createProfile(UserProfileVM user) {
		return null;
	}
	public MessageVM updateProfile(UserProfileVM user) {
		return null;
	}
	public MessageVM deleteProfile(Long id) {
		AuthUser user = authUserRepository.getOne(id);
		MessageVM messageVM = new MessageVM();
		if (user != null) {
			authUserRepository.delete(user);
			messageVM.setData(user);
			messageVM.setCode("200");
			messageVM.setMessage("User deleted successfully.");
		} else {
			messageVM.setCode("404");
			messageVM.setMessage("User not found for delete.");
		}
		return messageVM;
	}
}
