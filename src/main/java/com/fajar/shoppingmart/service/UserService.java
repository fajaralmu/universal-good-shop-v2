package com.fajar.shoppingmart.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.EntityRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private FileService fileService;
	@Autowired
	private EntityRepository entityRepository;

	public WebResponse updateProfile(HttpServletRequest httpServletRequest, WebRequest webRequest) {
		log.info("Update profile");
		
		final User loggedUser = sessionValidationService.getLoggedUser(httpServletRequest);
		final User user = webRequest.getUser();
		updateUserData(loggedUser, user);
		
		WebResponse response = new WebResponse();
		response.setUser(loggedUser);
		return response;
	}

	private void updateUserData(User loggedUser, User user) {
		if (user.getUsername() != null && !user.getUsername().isEmpty()) {
			loggedUser.setUsername(user.getUsername());
		}
		if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
			loggedUser.setDisplayName(user.getDisplayName());
		}
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			loggedUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
			try {
				String newName = fileService.writeImage(User.class.getSimpleName(), user.getProfileImage());
				loggedUser.setProfileImage(newName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		entityRepository.save(loggedUser);
	}

}
