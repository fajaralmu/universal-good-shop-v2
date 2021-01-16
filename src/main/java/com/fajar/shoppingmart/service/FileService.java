package com.fajar.shoppingmart.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fajar.shoppingmart.dto.AttachmentInfo;
import com.fajar.shoppingmart.service.config.WebConfigService;
import com.fajar.shoppingmart.util.IconWriter;
import com.fajar.shoppingmart.util.StringUtil;
import com.fajar.shoppingmart.util.ThreadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileService {

	@Autowired
	private WebConfigService webAppConfiguration;
	@Autowired
	private FtpResourceService ftpResourceService;
	@Value("${app.resources.uploadType}")
	private String uploadType;
	@Value("${app.resources.apiUploadEndpoint}")
	private String apiUploadEndpoint; 
	private HttpHeaders headers = new HttpHeaders();
	static ObjectMapper mapper = new ObjectMapper();
	@Autowired
	private ProgressService progressService;
	@PostConstruct
	public void init() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		 
	}

	int counter = 0;

	public int getCounter() {
		return counter;
	}

	private void addCounter() {
		counter++;
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
		return writeImage(code, data, null);
	}
		public synchronized String writeImage(String code, String data, HttpServletRequest httpServletRequest) throws IOException {
		log.info("#uploadType: {}", uploadType);
		if ("ftp".equals(uploadType)) {
			return writeImageFtp(code, data);
		}
		if ("api".equals(uploadType)) {
			return writeImageApi(code, data, httpServletRequest);
		}

		return writeImageToDisk(code, data);
	}

	private String writeImageApi(String code, String data, HttpServletRequest httpServletRequest) {
		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {
			return null;
		}

		String imageString = imageData[1];

		// extract image name
		String imageIdentity = imageData[0];
		String imageType = imageIdentity.replace("data:image/", "").replace(";base64", "");
		String randomId = String.valueOf(new Date().getTime()) + StringUtil.generateRandomNumber(5) + "_"
				+ getCounter();

		String imageFileName = code + "_" + randomId + "." + imageType;
		addCounter();
		 
		System.out.println("Post file to :" + apiUploadEndpoint);
		try {
			List<AttachmentInfo> attachments = AttachmentInfo.extractAttachmentInfos(data, imageFileName, imageType);
			for (int i = 0; i < attachments.size(); i++) {
				String response = uploadViaAPIv2( attachments.get(i), apiUploadEndpoint);
				System.out.println("response: "+ i+ " => " + response);
				if (null != httpServletRequest) {
					progressService.sendProgress(1, attachments.size(), 80, httpServletRequest);
				}
			}
			if (null != httpServletRequest) {
				progressService.sendComplete(httpServletRequest);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageFileName;
	} 

	public static void main(String[] args) throws Exception {
		AttachmentInfo request = (AttachmentInfo.builder().name("TEST.jpg").data("dddd").extension("jpg").build());
	}

	public static String uploadViaAPIv2(  AttachmentInfo request, String url) {

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		String response;
		RestTemplate rt = new RestTemplate();
		try {
			map.add("partialData", request.getData());
			map.add("order", request.getOrder());
			map.add("total", request.getTotal());
			map.add("name", request.getName());
			map.add("extension", request.getExtension());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
			ResponseEntity<String> responseEntity = rt.postForEntity(url, requestEntity, String.class);
			log.info("code: {}", responseEntity.getStatusCode());
			response = responseEntity.getBody();

		} catch (HttpStatusCodeException e) {
			e.printStackTrace();
			response = e.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
			response = e.getMessage();
		}
		return response;
	}

	public synchronized String writeImageFtp(String code, String data) throws IOException {

		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {
			return null;
		}
		
		String imageString = imageData[1];

		// extract image name
		String imageIdentity = imageData[0];
		String imageType = imageIdentity.replace("data:image/", "").replace(";base64", "");
		String randomId = String.valueOf(new Date().getTime()) + StringUtil.generateRandomNumber(5) + "_"
				+ getCounter();

		String imageFileName = code + "_" + randomId + "." + imageType;
		addCounter();
		ThreadUtil.run(() -> {
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
