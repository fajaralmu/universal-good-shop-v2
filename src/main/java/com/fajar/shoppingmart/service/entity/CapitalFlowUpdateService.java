package com.fajar.shoppingmart.service.entity;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.CapitalFlow;
import com.fajar.shoppingmart.service.financial.CashBalanceService;

@Service
public class CapitalFlowUpdateService extends BaseEntityUpdateService<CapitalFlow>{ 
 
	@Autowired
	private CashBalanceService cashBalanceService;
	
	@Override
	public WebResponse saveEntity(CapitalFlow entity, boolean newRecord, HttpServletRequest httpServletRequest) {
		CapitalFlow capital = (CapitalFlow) copyNewElement(entity, newRecord);
		
//		if(newRecord) {
//			return WebResponse.failed("Unable to update!");
//		}
		 
		CapitalFlow newEntity = entityRepository.save(capital); 
		cashBalanceService.updateCashBalance(newEntity);
		
		return WebResponse.builder().entity(newEntity).build();
	}
}
