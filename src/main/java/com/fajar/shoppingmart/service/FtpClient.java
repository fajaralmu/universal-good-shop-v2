package com.fajar.shoppingmart.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.fajar.shoppingmart.util.ThreadUtil;

class FtpClient {

	final private String server;
	final private int port;
	final private String user;
	final private String password;
	private FTPClient ftp;

	// constructor
	public FtpClient(String server, int port, String user, String password) {
		this.server = server;
		this.port = port;
		this.user = user;
		this.password = password;

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

	public void close() throws IOException {
		ftp.disconnect();
	}
	public ByteArrayOutputStream getUploadedImage(String name) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ftp.retrieveFile("upload/"+name, out);
	    return out;
	}
	public void storeBase64Image(String base64image, String fileName) throws Exception {
		ByteArrayInputStream image = base64ToBufferedImage(base64image);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.storeFile("upload/" + fileName, image);
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


	private ByteArrayInputStream base64ToBufferedImage(String imageString) throws Exception {

		byte[] imageByte;

		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

		bis.close();
		return bis;
	}
}