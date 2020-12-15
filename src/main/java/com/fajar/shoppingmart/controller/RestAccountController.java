package com.fajar.shoppingmart.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.User;
import com.fajar.shoppingmart.service.LogProxyFactory;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/app/account")
@Slf4j
public class RestAccountController extends BaseController {

	public RestAccountController() {
		log.info("------------------RestAccountController-----------------");
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	
	@PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
	public User user(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		return sessionValidationService.getLoggedUser(httpRequest);
	}
	@PostMapping(value="/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse logout (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){   
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new WebResponse();
    }

}
