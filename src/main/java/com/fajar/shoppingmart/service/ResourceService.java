package com.fajar.shoppingmart.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceService {

	public BufferedImage getImage(String name) throws Exception {
		log.info("get image: {}", name);
		FtpClient ftpClient = new FtpClient();
		ftpClient.open();
		ByteArrayOutputStream output = ftpClient.getUploadedImage(name);
		byte[] data = output.toByteArray();
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		return ImageIO.read(input);
	}
}
