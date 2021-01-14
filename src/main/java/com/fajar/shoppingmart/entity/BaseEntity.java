package com.fajar.shoppingmart.entity;

import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Type;

import com.fajar.shoppingmart.annotation.BaseField;
import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.entity.custom.EntityUpdateInterceptor;
import com.fajar.shoppingmart.util.EntityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.extern.slf4j.Slf4j;

@Dto
@Slf4j
@MappedSuperclass
public class BaseEntity implements Serializable{

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 5713292970611528372L;
	
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@FormField
	@Type(type = "org.hibernate.type.LongType")
	@Column 
	@BaseField
	private Long id;
	
	@Column(name = "created_date")
	@JsonIgnore
//	@FormField
	private Date createdDate;
	@Column(name = "modified_date")
	@JsonIgnore
	private Date modifiedDate; 
	@Column(name = "deleted")
	@JsonIgnore
	private Date deleted;
	@javax.persistence.Transient
	private List<String> nulledFields = new ArrayList<>();
	
	public List<String> getNulledFields() {
		return nulledFields;
	}

	public void setNulledFields(List<String> nulledFields) {
		this.nulledFields = nulledFields;
	}

	public Date getCreatedDate() { 
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	 public Date isDeleted() {
		return deleted;
	}
	 public void setDeleted(Date deleted) {
		this.deleted = deleted;
	}
	 
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@PrePersist
	private void prePersist() {
		if (this.createdDate == null) {
			this.createdDate = new Date();
		}
		this.modifiedDate = new Date();
	}
 
	public void validateUniqueKeys(List<BaseEntity> entities) {}
	
	@JsonIgnore
	@Transient
	public EntityUpdateInterceptor modelUpdateInterceptor() {
		return new EntityUpdateInterceptor<BaseEntity>() {
			private static final long serialVersionUID = 2878932467536346251L;

			@Override
			public BaseEntity preUpdate(BaseEntity object) {
				return object;
			}
		};
	}
	
	public void validateNullValues () {
		for (int i = 0; i < this.nulledFields.size(); i++) {
			String fieldName = this.nulledFields.get(i);
			try {
				Field field = EntityUtil.getDeclaredField(getClass(), fieldName);
				field.set(this, null);
				log.info("Set {} NULL", field.getName());
			} catch (Exception e) {
			}
		}
	}
}
