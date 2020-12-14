package com.fajar.shoppingmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.Authority;
import com.fajar.shoppingmart.entity.AuthorityType;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	Authority findTop1ByName(AuthorityType type);
 

}
