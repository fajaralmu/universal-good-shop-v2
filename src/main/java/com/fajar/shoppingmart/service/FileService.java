package com.fajar.shoppingmart.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.service.config.WebConfigService;
import com.fajar.shoppingmart.util.IconWriter;
import com.fajar.shoppingmart.util.StringUtil;
import com.fajar.shoppingmart.util.ThreadUtil;

@Service
public class FileService {

	@Autowired
	private WebConfigService webAppConfiguration;
	@Autowired
	private FtpResourceService ftpResourceService;
	@Value("${app.resources.ftpUpload}")
	private boolean ftpUpload;

	int counter = 0;

	public int getCounter() {
		return counter;
	}

	private void addCounter() {
		counter++;
	}

	public static void main(String[] args) {
		File file = new File("D:/Development/Files");
		System.out.println(file.toURI().toString());
	}

	public String writeIcon(String code, String data) throws IOException {
		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {
			return null;
		}
		// create a buffered image
		String imageString = imageData[1];
		BufferedImage image = null;
		byte[] imageByte;

		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();

		String iconName = IconWriter.writeIcon(image, getPath() + "/ICON");
//		storeFtp(imageString, imageFileName)
		return iconName;

	}

	public synchronized String writeImage(String code, String data) throws IOException {
		if (ftpUpload) {
			return writeImageFtp(code, data);
		}

		return writeImageToDisk(code, data);
	}

	public synchronized String writeImageFtp(String code, String data) throws IOException {

		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {
			return null;
		}
		// create a buffered image
		String imageString = imageData[1];

		// extract image name
		String imageIdentity = imageData[0];
		String imageType = imageIdentity.replace("data:image/", "").replace(";base64", "");
		String randomId = String.valueOf(new Date().getTime()) + StringUtil.generateRandomNumber(5) + "_"
				+ getCounter();

		String imageFileName = code + "_" + randomId + "." + imageType;
		addCounter();
		ThreadUtil.run(()-> {
			ftpResourceService.storeFtp(imageString, imageFileName);
		});
		return imageFileName;
	}

	public synchronized String writeImageToDisk(String code, String data) throws IOException {

		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {
			return null;
		}
		// create a buffered image
		String imageString = imageData[1];
		BufferedImage image = null;
		byte[] imageByte;

		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();

		// write the image to a file
		String imageIdentity = imageData[0];
		String imageType = imageIdentity.replace("data:image/", "").replace(";base64", "");
		String randomId = String.valueOf(new Date().getTime()) + StringUtil.generateRandomNumber(5) + "_"
				+ getCounter();

		String imageFileName = code + "_" + randomId + "." + imageType;
		File outputfile = new File(getPath() + "/" + imageFileName);
		ImageIO.write(image, imageType, outputfile);

		System.out.println("==output file: " + outputfile.getAbsolutePath());

		addCounter();

		return imageFileName;
	}

	private String getPath() {
		return webAppConfiguration.getUploadedImageRealPath();
	}

}
