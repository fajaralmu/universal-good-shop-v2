package com.fajar.shoppingmart.service.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fajar.shoppingmart.entity.Authority;
import com.fajar.shoppingmart.entity.AuthorityType;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.repository.AuthorityRepository;
import com.fajar.shoppingmart.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultUserService {
	@Autowired
	private UserRepository userRepository;  
	@Autowired
	private AuthorityRepository authorityRepository;
	
	private BCryptPasswordEncoder passwordEncoder;
	
	public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	@PostConstruct
	public void init() {
		try {
			checkUserAuthorities();
			checkUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * don't invoke this method when tables has not been created yet
	 */
	private void checkUserAuthorities() {
		Authority auth = authorityRepository.findTop1ByName(AuthorityType.ROLE_ADMIN);
		if (null == auth) {
			auth = Authority.createAdmin();
			authorityRepository.save(auth);
		}
		Authority authUser = authorityRepository.findTop1ByName(AuthorityType.ROLE_USER);
		if (null == authUser) {
			authUser = Authority.createUser();
			authorityRepository.save(authUser);
		}
	}

	/**
	 * don't invoke this method when tables has not been created yet
	 */
	private void checkUser() {
		log.info("___________checkUser");
		List<User> adminUser = userRepository.getByAuthority(AuthorityType.ROLE_ADMIN.toString());
		if (adminUser == null || adminUser.isEmpty()) {
			generateDefaultAdmin();
		}
	}
	
	private void generateDefaultAdmin() {
		Authority adminAuth = authorityRepository.findTop1ByName(AuthorityType.ROLE_ADMIN);
		if (null == adminAuth) {
			log.info("___________null == adminAuth");
			return;
		}
		
		User user = new User();
		Authority auth = new Authority();
		auth.setId(adminAuth.getId());
		user.addAuthority(auth );
		user.setPassword(passwordEncoder.encode("123"));
		user.setUsername("admin");
		user.setDisplayName("Application Admin");
		
		log.info("___________userRepository.save(user)");
		userRepository.save(user);
	}
}