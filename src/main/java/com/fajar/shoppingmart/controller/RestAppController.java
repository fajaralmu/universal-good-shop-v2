package com.fajar.shoppingmart.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.shoppingmart.dto.WebResponse;

@CrossOrigin
@RestController
public class RestAppController extends BaseController {
	Logger log = LoggerFactory.getLogger(RestAppController.class);

	public RestAppController() {
		log.info("------------------RestAppController #1-----------------");
	}

	@PostConstruct
	public void init() {
//		LogProxyFactory.setLoggers(this);
	}

	@GetMapping(value = "/app/api/get", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse apiPrivate(HttpServletRequest request) {
		return new WebResponse();
	}

	@GetMapping(value = "/public/api/get", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebResponse apiPublic(HttpServletRequest request) {
		return new WebResponse();
	}

}
