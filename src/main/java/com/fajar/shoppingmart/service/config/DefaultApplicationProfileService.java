package com.fajar.shoppingmart.service.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.FontAwesomeIcon;
import com.fajar.shoppingmart.entity.ApplicationProfile;
import com.fajar.shoppingmart.repository.AppProfileRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultApplicationProfileService {

	@Autowired
	private AppProfileRepository appProfileRepository;
	private ApplicationProfile applicationProfile;
	@PostConstruct
	public void init() {
		try {
			checkApplicationProfile();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public ApplicationProfile getApplicationProfile() {
		return applicationProfile;
	}

	private void checkApplicationProfile() {
		log.info("iiiiiiiiiiii DefaultApplicationProfileService iiiiiiiiiiiiiiiiii");
		// TODO Auto-generated method stub
		ApplicationProfile profile = appProfileRepository.findByAppCode("MY_APP");
		if (null == profile) {
			profile = saveDefaultProfile();
		}
		this.applicationProfile = profile;
	}

	private ApplicationProfile saveDefaultProfile() {
		ApplicationProfile profile = new ApplicationProfile();
		profile.setName("Nuswantoro Commerce");
		profile.setAbout("");
		profile.setWebsite("http://localhost:3000");
		profile.setIconUrl("DefaultIcon.BMP");
		profile.setColor("#1e1e1e");
		profile.setFontColor("#f5f5f5");
		profile.setBackgroundUrl("Profile_02adb5ae-40b3-4b79-bc5e-f93c0ea8644f.png");
		profile.setPageIcon("ICO_8601834213.ico");
		profile.setAppCode("MY_APP");
		profile.setContact("somabangsa@gmail.com");
		profile.setFooterIconClass(FontAwesomeIcon.COFFEE);
		profile.setAbout("About My Retail");return appProfileRepository.save(profile );
	}
}
