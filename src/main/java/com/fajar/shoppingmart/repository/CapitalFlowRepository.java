package com.fajar.shoppingmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.shoppingmart.entity.CapitalFlow;

public interface CapitalFlowRepository extends JpaRepository<CapitalFlow		, Long> {

	List<CapitalFlow> findByDeletedFalse();

//mysql	@Query(nativeQuery = true,
//			value = "select * from capital_flow  " + 
//					" where month(`date`) = ?1 and year(`date`) = ?2")
//postgres
	@Query(nativeQuery = true,
	value = "select * from capital_flow  " + 
			" where date_part('month', date) = ?1 and date_part('year', date) = ?2")
	List<CapitalFlow> findByPeriod(int month, int year);

}
