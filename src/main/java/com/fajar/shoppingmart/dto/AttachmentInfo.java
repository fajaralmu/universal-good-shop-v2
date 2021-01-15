package com.fajar.shoppingmart.dto;

import java.io.Serializable;

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
	private String name;
	private String extension;
	private String data;
	private String url;

}
