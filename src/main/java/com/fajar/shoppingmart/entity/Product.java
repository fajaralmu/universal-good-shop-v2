package com.fajar.shoppingmart.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.constants.FieldType;
import com.fajar.shoppingmart.constants.FormInputColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN, updateService =  "productUpdateService")
@Entity
@Table(name = "product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Product extends BaseEntity implements MultipleImageModel {

	/**
	* 
	*/
	private static final long serialVersionUID = 3494963248002164943L;
	@Column(unique = true)
	@FormField(lableName = "Product Code")
	private String code;
	@Column(unique = true)
	@FormField
	private String name;
	@Column
	@FormField(type= FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_CURRENCY)
	@Type(type = "org.hibernate.type.LongType")
	private long price;
	@Column
	@FormField
	private String type;
	
	@Column(name = "image_url" )
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, multipleImage = true, defaultValue = "Default.BMP")
	private String imageNames; // type:BLOB
	
	@JoinColumn(name = "unit_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Unit unit;
	
	@JoinColumn(name = "category_id", nullable = false)
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST)
	private Category category;

	@Transient
	private boolean newProduct;
	@Transient
	@Setter(value = AccessLevel.NONE)
	private int count;
	@Transient
	private List<Supplier> suppliers;
	
	public void setCount(int count) {
		log.debug(this.name+" Count: "+ count);
		this.count = count;
	}
	 

	@Override
	public void setImageNamesArray(String[] image) {
		if (null == image) {
			return;
		}
		this.imageNames = String.join("~", image);
	}

	@JsonIgnore
	@Override
	public String[] getImageNamesArray() {
		if (null == imageNames) { return new String[] {};}
		System.out.println("imageNames.split(\"~\") length: "+imageNames.split("~").length);
		return imageNames.split("~");
	}

}
