package com.fajar.shoppingmart.controller;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.shoppingmart.service.ResourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("assets")
public class ResourcesController extends BaseController{
	
	@Autowired
	private ResourceService resourceService;

	@RequestMapping(value = { "/images/{name:.+}"}) 
	public void images(@PathVariable(name="name") String name, HttpServletRequest request, HttpServletResponse response) throws Exception  { 
		log.info("get image name: {}", name);
		BufferedImage image = resourceService.getImage(name);
		String[] nameSplitted = name.split("\\.");
		String type = nameSplitted[nameSplitted.length-1];
		ImageIO.write(image, type, response.getOutputStream());
	}
}
