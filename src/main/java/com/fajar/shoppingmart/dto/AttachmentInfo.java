package com.fajar.shoppingmart.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fajar.shoppingmart.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8953767869805765435L;
	private static final int MAX_SIZE = 9000; //9KB
	private String name;
	private String extension;
	private String data;
	private String url;
	private int total;
	private int order;
	
	public static List<AttachmentInfo> extractAttachmentInfos(String data, String name, String extension) {
		List<AttachmentInfo> result = new ArrayList<AttachmentInfo>();
		int fileSize = StringUtil.base64StringFileSize(data);
		if (fileSize <= MAX_SIZE) {
			AttachmentInfo attachmentInfo = new AttachmentInfo();
			attachmentInfo.setData(data);
			attachmentInfo.setTotal(1);
			attachmentInfo.setOrder(1);
			attachmentInfo.setName(name);
			result.add(attachmentInfo);
			return result;
		}
		
		int division = fileSize/MAX_SIZE;
		String[] dividedString = StringUtil.divideStringInto(data, division);
		for (int i = 0; i < dividedString.length; i++) {
			AttachmentInfo attachmentInfo = new AttachmentInfo();
			attachmentInfo.setData(dividedString[i]);
			attachmentInfo.setTotal(division);
			attachmentInfo.setOrder(i + 1);
			attachmentInfo.setName(name);
			result.add(attachmentInfo);
		}
		
		return result ;
	}

}
