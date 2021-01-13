package com.fajar.shoppingmart.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.fajar.shoppingmart.util.ThreadUtil;

class FtpClient {

	private String server;
	private int port;
	private String user = "anonymous";
	private String password = "";
	private FTPClient ftp;

	// constructor
	public FtpClient() {
		server = "localhost";
		port = 21;

	}

	void open() throws IOException {
		ftp = new FTPClient();

		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		ftp.connect(server, port);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new IOException("Exception in connecting to FTP Server");
		}

		ftp.login(user, password);
	}

	void close() throws IOException {
		ftp.disconnect();
	}

	public void storeBase64Image(String base64image, String fileName) throws Exception {
		ByteArrayInputStream image = base64ToBufferedImage(base64image);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.storeFile("upload/" + fileName, image);
		completePendingCommand();
	}

	public void storeBase64Image(ByteArrayInputStream inputStream, String fileName) throws Exception {
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.storeFile("upload/" + fileName, inputStream);
		completePendingCommand();
	}

	void completePendingCommand() {
		ThreadUtil.run(() -> {
			try {
				ftp.completePendingCommand();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
			}
		});
	}

	public void logout() {
		try {
			ftp.logout();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private ByteArrayInputStream base64ToBufferedImage(String imageString) throws Exception {

		byte[] imageByte;

		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

		bis.close();
		return bis;
	}
}