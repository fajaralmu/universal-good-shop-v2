package com.fajar.shoppingmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.ApplicationProfile;

public interface AppProfileRepository extends JpaRepository<ApplicationProfile, Long> {
 

	ApplicationProfile findByAppCode(String appCode); 

}
