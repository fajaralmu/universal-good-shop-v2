package com.fajar.shoppingmart.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.fajar.shoppingmart.config.security.UserDetailDomain;
import com.fajar.shoppingmart.dto.KeyValue;
import com.fajar.shoppingmart.dto.NavigationMenu;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.service.SessionValidationService;
import com.fajar.shoppingmart.service.UserSessionService;
import com.fajar.shoppingmart.service.config.BindedValues;
import com.fajar.shoppingmart.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BaseController {

	private static final String MODEL_ATTR_TITLE = "title";
	private static final String MODEL_ATTR_PAGE_URL = "pageUrl";
	
	protected String basePage = "BASE_PAGE";
	@Autowired
	private SessionValidationService sessionValidationService;
	@Autowired
	private UserSessionService userSessionService;
	 
	@Autowired
	protected BindedValues bindedValues;
	
	@ModelAttribute("applicationHeaderLabel")
	public String applicationHeaderLabel(HttpServletRequest request) {
		
		return bindedValues.getApplicationHeaderLabel();
	}
	
	@ModelAttribute("applicationDescription")
	public String applicationDescription(HttpServletRequest request) {
		
		return bindedValues.getApplicationDescription();
	}
	
	@ModelAttribute("applicationFooterLabel")
	public String applicationFooterLabel(HttpServletRequest request) {
		
		return bindedValues.getApplicationFooterLabel();
	}
	 
	@ModelAttribute("isPhone")
	public boolean isPhone(HttpServletRequest request) {
		try {
			return request.getHeader("user-agent").toLowerCase().contains("android");
		} catch (Exception e) {
			return false;
		}
	}

	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	} 

	@ModelAttribute("year")
	public int getCurrentYear(HttpServletRequest request) {
		return DateUtil.getCalendarItem(new Date(), Calendar.YEAR);
	}
	@ModelAttribute("isAuthenticated")
	public boolean isAuthenticated(HttpServletRequest request) {
		return validatePrinciple(request.getUserPrincipal());
	}
	@ModelAttribute("loggedUser")
	public User loggedUser(HttpServletRequest request) {
		return sessionValidationService.getLoggedUser(request);
	}	
	@ModelAttribute("userPrincipal")
	public Object getUserPrincipal(HttpServletRequest request) {
		return sessionValidationService.getUserPrincipal(request);
	}
	
	@ModelAttribute("navigationMenus")
	public List<NavigationMenu> navigationMenus(HttpServletRequest request) {
		List<NavigationMenu> menus = NavigationMenu.defaultMenus();
		boolean authenticated = validatePrinciple(request.getUserPrincipal()); 
		List<NavigationMenu> displayedMenus = new ArrayList<>();
		
		for (NavigationMenu navigationMenu : menus) {
			if (!authenticated && navigationMenu.isAuthenticated()) {
				continue;
			}
			displayedMenus.add(navigationMenu);
		}
//		log.info("displayedMenus : {}", displayedMenus.size());
		return displayedMenus ;
	}

	@ModelAttribute("ipAndPort")
	public String getIpAddressAndPort(HttpServletRequest request) {

		String remoteAddress = request.getRemoteAddr();
		int port = request.getServerPort();

		return remoteAddress + ":" + port;
	} 
	protected static void setTitle(Model model, String title) {
		model.addAttribute(MODEL_ATTR_TITLE, title);
	}

	protected static void setPageUrl(Model model, String pageUrl) {
		model.addAttribute(MODEL_ATTR_PAGE_URL, pageUrl);
	}
	
	protected boolean validatePrinciple(Object principal) {
		return sessionValidationService.validatePrinciple(principal);
	}
	
	

	/**
	 * ====================================================== Statics
	 * ======================================================
	 * 
	 */
 
	private static void addResourcePaths(ModelAndView modelAndView, String resourceName, String... paths) {
		List<KeyValue<String, String>> resoucePaths = new ArrayList<>();
		for (int i = 0; i < paths.length; i++) {
			KeyValue<String, String> keyValue = new KeyValue<String, String>();
			keyValue.setValue(paths[i]);

			resoucePaths.add(keyValue);
			log.info("{}. Add {} to {} , value: {}", i, resourceName, modelAndView.getViewName(), paths[i]);
		}
		setModelAttribute(modelAndView, resourceName, resoucePaths);
	}

	private static void setModelAttribute(ModelAndView modelAndView, String attrName, Object attrValue) {
		if (null == attrValue) {
			return;
		}
		modelAndView.getModel().put(attrName, attrValue);
	}

	public static void addStylePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalStylePaths", paths);
	}

	
	public static void addJavaScriptResourcePaths(ModelAndView modelAndView, String... paths) {
		if (null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalScriptPaths", paths);
	}

	public static void addTitle(ModelAndView modelAndView, String title) {
		if (null == title || title.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "title", title);
	}

	public static void addPageUrl(ModelAndView modelAndView, String pageUrl) {
		if (null == pageUrl || pageUrl.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "pageUrl", pageUrl);
	}
}
