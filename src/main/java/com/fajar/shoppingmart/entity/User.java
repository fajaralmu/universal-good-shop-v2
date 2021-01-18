package com.fajar.shoppingmart.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.constants.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Dto(commonManagementPage = false, updateService = "userUpdateService")
@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3896877759244837620L;
	@Column(unique = true)
	@FormField
	private String username;
	@Column(name = "display_name")
	@FormField
	private String displayName;
	@Column
	@FormField
//	@JsonIgnore
	private String password;

	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "profile_image")
	private String profileImage;

	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER) 
	@JoinTable(name = "user_authority", 
			joinColumns = { @JoinColumn(name = "user_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "authority_id") }) 
	@Default
	private Set<Authority> authorities = new HashSet<>();

	public void addAuthority(Authority authority) {
		authorities.add(authority);
	}
	 
	@Transient
	@JsonIgnore
	private String requestId;

	@Transient
	@JsonIgnore
	private Date processingDate; // for transaction

	 

}
