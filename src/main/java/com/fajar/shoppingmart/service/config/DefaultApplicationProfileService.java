package com.fajar.shoppingmart.service.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.FontAwesomeIcon;
import com.fajar.shoppingmart.dto.WebRequest;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.ApplicationProfile;
import com.fajar.shoppingmart.repository.AppProfileRepository;
import com.fajar.shoppingmart.repository.EntityRepository;
import com.fajar.shoppingmart.service.FileService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultApplicationProfileService {

	@Autowired
	private AppProfileRepository appProfileRepository;
	@Autowired
	private EntityRepository entityRepository; 
	@Autowired
	private FileService fileService;

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
	
	public WebResponse updateApplicationProfile(HttpServletRequest httpServletRequest, WebRequest webRequest) {
		log.info("Update application profile");
		
		final ApplicationProfile actualAppProfile = getApplicationProfile();
		final ApplicationProfile applicationProfile = webRequest.getProfile();
		updateApplicationProfileData(actualAppProfile, applicationProfile);
		
		WebResponse response = new WebResponse();
		response.setApplicationProfile(actualAppProfile);
		return response;
	}
	private void updateApplicationProfileData(ApplicationProfile actualAppProfile,
			ApplicationProfile appProfile) {
		 
		if (notEmpty(appProfile.getName())) {
			actualAppProfile.setName(appProfile.getName());
		}
		if (notEmpty(appProfile.getWelcomingMessage())) {
			actualAppProfile.setWelcomingMessage(appProfile.getWelcomingMessage());
		}
		if (notEmpty(appProfile.getShortDescription())) {
			actualAppProfile.setShortDescription(appProfile.getShortDescription());
		}
		if (notEmpty(appProfile.getAbout())) {
			actualAppProfile.setAbout(appProfile.getAbout());
		}
		if (notEmpty(appProfile.getColor())) {
			actualAppProfile.setColor(appProfile.getColor());
		}
		if (notEmpty(appProfile.getFontColor())) {
			actualAppProfile.setFontColor(appProfile.getFontColor());
		}
		if (notEmpty(appProfile.getBackgroundUrl()) && appProfile.getBackgroundUrl().startsWith("data:image")) {
			try {
				String backgroundUrl = fileService.writeImage(ApplicationProfile.class.getSimpleName(), appProfile.getBackgroundUrl());
				actualAppProfile.setBackgroundUrl(backgroundUrl );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
//		if (notEmpty(appProfile.getName())) {
//			actualAppProfile.setName(appProfile.getName());
//		}
		
		entityRepository.save(actualAppProfile);
	}
	private boolean notEmpty(String val) {
		return null != val && val.isEmpty() == false;
	}
}
