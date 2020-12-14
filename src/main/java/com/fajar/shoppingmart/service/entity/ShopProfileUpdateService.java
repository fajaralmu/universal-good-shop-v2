package com.fajar.shoppingmart.service.entity;

import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.ApplicationProfile;

@Service
public class ShopProfileUpdateService extends BaseEntityUpdateService<ApplicationProfile>{
 
	
	@Override
	public WebResponse saveEntity(ApplicationProfile baseEntity, boolean newRecord){
		ApplicationProfile shopProfile = (ApplicationProfile) copyNewElement(baseEntity, newRecord);
		 
		validateEntityFields(shopProfile, newRecord);
//		if (base64Image != null && !base64Image.equals("")) {
//			try {
//				String imageName = fileService.writeImage(baseEntity.getClass().getSimpleName(), base64Image);
//				shopProfile.setIconUrl(imageName);
//			} catch (IOException e) {
//
//				shopProfile.setIconUrl(null);
//				e.printStackTrace();
//			}
//		} else {
//			if (!newRecord) {
//				Optional<Profile> dbShopProfile = shopProfileRepository.findById(shopProfile.getId());
//				if (dbShopProfile.isPresent()) {
//					shopProfile.setIconUrl(dbShopProfile.get().getIconUrl());
//				}
//			}
//		}
		ApplicationProfile newShopProfile = entityRepository.save(shopProfile);
		return WebResponse.builder().entity(newShopProfile).build();
	}
	
}

