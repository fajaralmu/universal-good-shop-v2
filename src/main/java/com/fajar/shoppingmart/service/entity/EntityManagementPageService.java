package com.fajar.shoppingmart.service.entity;

import static com.fajar.shoppingmart.util.MvcUtil.constructCommonModel;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.setting.EntityManagementConfig;
import com.fajar.shoppingmart.entity.setting.EntityProperty;
import com.fajar.shoppingmart.repository.EntityRepository;
import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityManagementPageService {

	@Autowired
	private EntityRepository entityRepository;
	 
	public Model setModel(HttpServletRequest request, Model model, String key) throws Exception {

		EntityManagementConfig entityConfig = entityRepository.getConfig(key);

		if (null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key (" + key + ")!");
		}

		HashMap<String, List<?>> additionalListObject = getFixedListObjects(entityConfig.getEntityClass());
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityConfig.getEntityClass(),
				additionalListObject);
		model = constructCommonModel(request, entityProperty, model, entityConfig.getEntityClass().getSimpleName(),
				"management"); 
		 
		return model;
	}

	private HashMap<String, List<?>> getFixedListObjects(Class<? extends BaseEntity> entityClass) {
		HashMap<String, List<?>> listObject = new HashMap<>();
		try {
			List<Field> fixedListFields = EntityUtil.getFixedListFields(entityClass);

			for (int i = 0; i < fixedListFields.size(); i++) {
				Field field = fixedListFields.get(i);
				Class<? extends BaseEntity> type;

				if (CollectionUtil.isCollectionOfBaseEntity(field)) {
					Type classType = CollectionUtil.getGenericTypes(field)[0];
					type = (Class<? extends BaseEntity>) classType;

				} else {
					type = (Class<? extends BaseEntity>) field.getType();
				}
				log.info("(populating fixed list values) findALL FOR type: {}", type);
				List<? extends BaseEntity> list = entityRepository.findAll(type);
				listObject.put(field.getName(), CollectionUtil.convertList(list));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listObject;
	}

	 
	public WebResponse getManagementPages() {
		
		List<Object> result = new ArrayList<>();
		result.add(entityRepository.getConfig("product").setIconClassName("fas fa-cubes"));
		result.add(entityRepository.getConfig("category").setIconClassName("fas fa-tags"));
		result.add(entityRepository.getConfig("unit").setIconClassName("fas fa-balance-scale"));
		result.add(entityRepository.getConfig("customer").setIconClassName("fas fa-users"));
		result.add(entityRepository.getConfig("supplier").setIconClassName("fas fa-dolly-flatbed"));
		result.add(entityRepository.getConfig("transaction").setIconClassName("fas fa-book")); 
		result.add(entityRepository.getConfig("cashbalance").setIconClassName("fas fa-book")); 
//		result.add(entityRepository.getConfig("page")); 
//		result.add(entityRepository.getConfig("menu")); 
		
		return WebResponse.builder().generalList(result).build();
	}

}
