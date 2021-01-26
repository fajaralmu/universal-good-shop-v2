package com.fajar.shoppingmart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.shoppingmart.constants.FontAwesomeIcon;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Entity
@Table(name = "shop_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationProfile extends BaseEntity implements SingleImageModel {

	/**
	* 
	*/
	private static final long serialVersionUID = 4095664637854922384L;
	@Column  
	private String name;
	@Column(name = "mart_code", unique = true) 
	private String appCode;
	@Column(name = "short_description") 
	private String shortDescription;
	@Column 
	private String about;
	@Column(name = "welcoming_message") 
	private String welcomingMessage;
	@Column 
	private String address;

	@Column 
	private String contact;
	@Column 
	private String website; 
	@Column(name = "icon_url")
	private String iconUrl; 
	@Column(name = "page_icon_url")
	private String pageIcon; 
	@Column(name = "background_url")
	private String backgroundUrl;
	 @Column(name= "footer_icon_class")
	@Enumerated(EnumType.STRING) 
	private FontAwesomeIcon footerIconClass; 
	
	 @Column(name = "general_color")
	private String color;
	 @Column(name = "font_color")
	private String fontColor;
	
	@Transient
	private String assetsPath;
	
	@JsonIgnore
	@Transient
	private String FooterIconClassValue;
	public String getFooterIconClassValue() {
		if(null == footerIconClass) {
			return "fa fa-home"; 
		}
		return footerIconClass.value;
	}
	@Override
	public void setImage(String image) {
		pageIcon = image;
	}
	@Override
	public String getImage() {
		 
		return pageIcon;
	}
	
	

}
