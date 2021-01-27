package com.fajar.shoppingmart.service.entity;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.repository.ProductRepository;
import com.fajar.shoppingmart.service.resources.ImageUploadService;
import com.fajar.shoppingmart.service.transaction.ProductInventoryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductUpdateService extends BaseEntityUpdateService<Product> {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductInventoryService productInventoryService;
	@Autowired
	private ImageUploadService imageUploadService;
	/**
	 * add & update product
	 * 
	 * @param product
	 * @param newRecord
	 * @return
	 * @throws Exception
	 */
	@Override
	public WebResponse saveEntity(Product baseEntity, boolean newRecord, HttpServletRequest httpServletRequest) throws Exception {
	 
		Product product =  copyNewElement(baseEntity, newRecord);
		Optional<Product> dbProduct = Optional.empty();
		if (!newRecord) {
			dbProduct = productRepository.findById(product.getId());
			if (!dbProduct.isPresent()) {
			 
				throw new Exception("Existing record not found");
			}
		}
		final String imageData = product.getImageNames();
		if (imageData != null && !imageData.equals("")) {
			log.info("product image will be updated");
			String imageUrl = null;
			if (newRecord) {
				imageUrl = imageUploadService.writeNewImages(product, httpServletRequest);
			} else {
				imageUrl = imageUploadService.updateImages(product, dbProduct.get(), httpServletRequest);
			}
			product.setImageNames(imageUrl);
		} else {
			log.info("Product image wont be updated");
			if (!newRecord) {
				product.setImageNamesArray(dbProduct.get().getImageNamesArray());
			}
		}

		Product newProduct = entityRepository.save(product);
		if (newRecord) {
			productInventoryService.addNewProduct(newProduct);
		}

		return WebResponse.builder().entity(newProduct).build();
	}
 
}
