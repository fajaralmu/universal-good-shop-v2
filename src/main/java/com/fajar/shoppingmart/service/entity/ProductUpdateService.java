package com.fajar.shoppingmart.service.entity;

import static com.fajar.shoppingmart.service.entity.EntityService.ORIGINAL_PREFFIX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.repository.ProductRepository;
import com.fajar.shoppingmart.service.transaction.ProductInventoryService;
import com.fajar.shoppingmart.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductUpdateService extends BaseEntityUpdateService<Product> {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductInventoryService productInventoryService;

	/**
	 * add & update product
	 * 
	 * @param product
	 * @param newRecord
	 * @return
	 * @throws Exception
	 */
	@Override
	public WebResponse saveEntity(Product baseEntity, boolean newRecord) throws Exception {

		Product product = (Product) copyNewElement(baseEntity, newRecord);
		Optional<Product> dbProduct = Optional.empty();
		if (!newRecord) {
			dbProduct = productRepository.findById(product.getId());
			if (!dbProduct.isPresent()) {
			 
				throw new Exception("Existing record not found");
			}
		}
		String imageData = product.getImageUrl();
		if (imageData != null && !imageData.equals("")) {
			log.info("product image will be updated");
			String imageUrl = null;
			if (newRecord) {
				imageUrl = writeNewImages(product, imageData);
			} else {
				imageUrl = updateImages(product, dbProduct, imageData);
			}
			product.setImageUrl(imageUrl);
		} else {
			log.info("Product image wont be updated");
			if (!newRecord) {
				product.setImageUrl(dbProduct.get().getImageUrl());
			}
		}

		Product newProduct = entityRepository.save(product);
		if (newRecord) {
			productInventoryService.addNewProduct(newProduct);
		}

		return WebResponse.builder().entity(newProduct).build();
	}

	private String writeNewImages(Product product, String imageData) {
		String[] rawImageList = imageData.split("~");
		if (rawImageList == null || rawImageList.length == 0) {
			return null;
		}
		List<String> imageUrls = new ArrayList<>();
		for (int i = 0; i < rawImageList.length; i++) {
			String base64Image = rawImageList[i];
			if (base64Image == null || base64Image.equals(""))
				continue;
			try {
				String imageName = fileService.writeImage(product.getClass().getSimpleName(), base64Image);
				if (null != imageName) {
					imageUrls.add(imageName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (imageUrls.size() == 0) {
			return null;
		}

		String[] arrayOfString = imageUrls.toArray(new String[] {});
		CollectionUtil.printArray(arrayOfString);

		String imageUrlArray = String.join("~", arrayOfString);
		product.setImageUrl(imageUrlArray);

		return imageUrlArray;
	}

	private String updateImages(Product product, Optional<Product> dbProduct, String imageData) {
		final String[] rawImageList = imageData.split("~");
		if (rawImageList == null || rawImageList.length == 0 || dbProduct.isPresent() == false) {
			return null;
		}
		final boolean oldValueExist = dbProduct.get().getImageUrl() != null
				&& dbProduct.get().getImageUrl().split("~").length > 0;
		final String[] oldValueStringArr = oldValueExist ? product.getImageUrl().split("~") : new String[] {};
		final List<String> imageUrls = new ArrayList<>();
		//loop
		log.info("rawImageList length: {}", rawImageList.length);
		for (int i = 0; i < rawImageList.length; i++) {
			final String rawImage = rawImageList[i];
			if (rawImage == null || rawImage.equals(""))
				continue;
			String imageName = null;
			if (isBase64(rawImage)) {
				try {
					imageName = fileService.writeImage(product.getClass().getSimpleName(), rawImage);
					log.info("saved base64 image {}", imageName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

				if (oldValueExist && inArray(rawImage, oldValueStringArr)) {
					imageName = rawImage;
				}
			}

			if (imageName != null) {
				imageUrls.add(imageName);
			}
		}
		if (imageUrls.size() == 0) {
			return null;
		}

		String[] arrayOfString = imageUrls.toArray(new String[] {});
		CollectionUtil.printArray(arrayOfString);

		String imageUrlArray = String.join("~", arrayOfString);
		product.setImageUrl(imageUrlArray);

		return imageUrlArray;
	}

	private boolean inArray(String imageName, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (imageName.equals(array[i]))
				return true;
		}
		
		return false;
	}

	private boolean isBase64(String rawImage) {

		return rawImage.startsWith("data:image");
	}
}
