package com.fajar.shoppingmart.entity;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

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
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.entity.custom.EntityUpdateInterceptor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Dto
@MappedSuperclass
public class BaseEntity implements Serializable{

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 5713292970611528372L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	@Column(name = "deleted_date")
	@JsonIgnore
	private Date deletedDate;
	@Column(name = "deleted")
	@JsonIgnore
	private boolean deleted;
	 

	 

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

	 public boolean isDeleted() {
		return deleted;
	}
	 public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	 public Date getDeletedDate() {
		return deletedDate;
	}
	 public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
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
}