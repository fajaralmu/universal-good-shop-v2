package com.fajar.shoppingmart.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FtpResourceService {
	/**app.resources.ftpServer=localhost
			app.resources.ftpPort=21
			app.resources.ftpUsername=anonymous
			app.resources.password= **/
	@Value("${app.resources.ftpServer}")
	private String ftpServer;
	@Value("${app.resources.ftpPort}")
	private int ftpPort;
	@Value("${app.resources.ftpUser}")
	private String ftpUser;
	@Value("${app.resources.ftpPassword}")
	private String ftpPassword;

	public BufferedImage getImage(String name) throws Exception {
		
		FtpClient ftpClient = new FtpClient(ftpServer, ftpPort, ftpUser, ftpPassword);
		ftpClient.open();
		ByteArrayOutputStream output = ftpClient.getUploadedImage(name);
		
		return ImageIO.read(toInputStream(output));
	}

	private ByteArrayInputStream toInputStream(ByteArrayOutputStream output) {
		byte[] data = output.toByteArray();
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		return input;
	}
	
	
	
	public void storeFtp(String imageString, String imageFileName)  {
		log.info("store ftp: {}", imageFileName);
		try {
			FtpClient ftpClient = new FtpClient(ftpServer, ftpPort, ftpUser, ftpPassword);
			ftpClient.open();
			ftpClient.storeBase64Image(imageString, imageFileName);
			ftpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
