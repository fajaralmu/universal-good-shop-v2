package com.fajar.shoppingmart.util;

import static com.fajar.shoppingmart.constants.FieldType.FIELD_TYPE_FIXED_LIST;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import org.apache.commons.lang3.SerializationUtils;

import com.fajar.shoppingmart.annotation.AdditionalQuestionField;
import com.fajar.shoppingmart.annotation.Dto;
import com.fajar.shoppingmart.annotation.FormField;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.setting.EntityElement;
import com.fajar.shoppingmart.entity.setting.EntityProperty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityUtil {

	public static EntityProperty createEntityProperty(Class<?> clazz, HashMap<String, List<?>> additionalObjectList)
			throws Exception {
		if (clazz == null || getClassAnnotation(clazz, Dto.class) == null) {
			return null;
		}

		Dto dto = (Dto) getClassAnnotation(clazz, Dto.class);
		final boolean ignoreBaseField = dto.ignoreBaseField();
		final boolean isQuestionare = dto.quistionare();

		EntityProperty entityProperty = EntityProperty.builder().ignoreBaseField(ignoreBaseField)
				.entityName(clazz.getSimpleName().toLowerCase()).isQuestionare(isQuestionare).build();
		try {

			List<Field> fieldList = getDeclaredFields(clazz);

			if (isQuestionare) {
				Map<String, List<Field>> groupedFields = sortListByQuestionareSection(fieldList);
				fieldList = CollectionUtil.mapOfListToList(groupedFields);
				Set<String> groupKeys = groupedFields.keySet();
				String[] keyNames = CollectionUtil.toArrayOfString(groupKeys.toArray());

				entityProperty.setGroupNames(keyNames);
			}
			List<EntityElement> entityElements = new ArrayList<>();
			List<String> fieldNames = new ArrayList<>();
			String fieldToShowDetail = "";

			for (Field field : fieldList) {

				final EntityElement entityElement = new EntityElement(field, entityProperty, additionalObjectList);

				if (false == entityElement.build()) {
					continue;
				}
				if (entityElement.isDetailField()) {
					fieldToShowDetail = entityElement.getId();
				}

				fieldNames.add(entityElement.getId());
				entityElements.add(entityElement);
			}

			entityProperty
					.setAlias(dto.value().isEmpty() ? StringUtil.extractCamelCase(clazz.getSimpleName()) : dto.value());
			entityProperty.setEditable(dto.editable());
			entityProperty.setElementJsonList();
			entityProperty.setElements(entityElements);
			entityProperty.setDetailFieldName(fieldToShowDetail);
			entityProperty.setDateElementsJson(MyJsonUtil.listToJson(entityProperty.getDateElements()));
			entityProperty.setFieldNames(MyJsonUtil.listToJson(fieldNames));
			entityProperty.setFieldNameList(fieldNames);
			entityProperty.setFormInputColumn(dto.formInputColumn().value);
			entityProperty.determineIdField();

			log.info("============ENTITY PROPERTY: {} ", entityProperty);

			return entityProperty;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	static boolean isIdField(Field field) {
		return field.getAnnotation(Id.class) != null;
	}

	public static void removeAttribute(Object object, String... fields) {
		for (String fieldName : fields) {
			Field field = EntityUtil.getDeclaredField(object.getClass(), fieldName);

			try {
				field.set(object, null);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * 
	 * @param _class
	 * @return String type field & non empty able
	 */
	public static List<Field> getNotEmptyAbleField(Class<? extends BaseEntity> _class) {

		List<Field> result = new ArrayList<>();
		List<Field> formFieldAnnotatedField = getFormFieldAnnotatedField(_class);
		for (int i = 0; i < formFieldAnnotatedField.size(); i++) {
			Field field = formFieldAnnotatedField.get(i);
			FormField formField = getFieldAnnotation(field, FormField.class);

			if (field.getType().equals(String.class) && !formField.emptyAble()) {
				result.add(field);
			}

		}

		return result;
	}

	public static List<Field> getFormFieldAnnotatedField(Class<? extends BaseEntity> _class) {

		List<Field> result = new ArrayList<>();

		List<Field> declaredField = getDeclaredFields(_class);
		for (int i = 0; i < declaredField.size(); i++) {
			Field field = declaredField.get(i);

			if (getFieldAnnotation(field, FormField.class) != null) {
				result.add(field);
			}

		}

		return result;
	}

	private static Map<String, List<Field>> sortListByQuestionareSection(List<Field> fieldList) {
		Map<String, List<Field>> temp = MapUtil.singleMap(AdditionalQuestionField.DEFAULT_GROUP_NAME,
				new ArrayList<>());

		String key = AdditionalQuestionField.DEFAULT_GROUP_NAME;
		for (Field field : fieldList) {
			FormField formField = field.getAnnotation(FormField.class);
			boolean isIDField = isIdField(field);

			if (null == formField) {
				continue;
			}
			AdditionalQuestionField additionalQuestionField = field.getAnnotation(AdditionalQuestionField.class);
			if (null == additionalQuestionField || isIDField) {
				key = "OTHER";
				log.debug("{} has no additionalQuestionareField", field.getName());
			} else {
				key = additionalQuestionField.value();
			}
			if (temp.get(key) == null) {
				temp.put(key, new ArrayList<>());
			}
			temp.get(key).add(field);
			log.debug("{}: {}", key, field.getName());
		}
		log.debug("QUestionare Map: {}", temp);
		return (temp);

	}

	public static <T extends Annotation> T getClassAnnotation(Class<?> entityClass, Class<T> annotation) {
		try {
			return entityClass.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T extends Annotation> T getFieldAnnotation(Field field, Class<T> annotation) {
		try {
			return field.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}

	public static Field getDeclaredField(Class<?> clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			if (field == null) {

			}
			field.setAccessible(true);
			return field;

		} catch (Exception e) {
			//log.error("Error get declared field in the class, and try access super class");
		}
		if (clazz.getSuperclass() != null) {

			try {
				//log.info("TRY ACCESS SUPERCLASS");

				Field superClassField = clazz.getSuperclass().getDeclaredField(fieldName);
				superClassField.setAccessible(true);
				return superClassField;
			} catch (Exception e) {

				log.error("FAILED Getting FIELD: " + fieldName);
				e.printStackTrace();
			}
		}

		return null;
	}

	public static List<Field> getDeclaredFields(Class<?> clazz) {
		return getDeclaredFields(clazz, true, false);
	}

	/**
	 * 
	 * @param clazz
	 * @param includeSuper
	 * @param onlyColumnField
	 * @return
	 */
	public static List<Field> getDeclaredFields(Class<?> clazz, boolean includeSuper, boolean onlyColumnField) {
		Field[] baseField = clazz.getDeclaredFields();
//
//		List<EntityElement> entityElements = new ArrayList<EntityElement>();
		List<Field> fieldList = new ArrayList<>();

		loop1: for (Field field : baseField) {

			Object column = getFieldAnnotation(field, Column.class);
			if (onlyColumnField && null == column)
				column = getFieldAnnotation(field, JoinColumn.class);

			if (onlyColumnField && column == null)
				continue loop1;

			field.setAccessible(true);
			fieldList.add(field);
		}
		if (includeSuper && clazz.getSuperclass() != null) {

			Field[] parentFields = clazz.getSuperclass().getDeclaredFields();

			loop2: for (Field field : parentFields) {
				Object column = getFieldAnnotation(field, Column.class);
				if (onlyColumnField && null == column)
					column = getFieldAnnotation(field, JoinColumn.class);

				if (onlyColumnField && column == null)
					continue loop2;

				field.setAccessible(true);
				fieldList.add(field);
			}

		}
		return fieldList;
	}

	public static Field getIdFieldOfAnObject(Field field) {
		Class<?> cls;
		if (CollectionUtil.isCollectionOfBaseEntity(field)) {
			cls = (Class<?>) CollectionUtil.getGenericTypes(field)[0];
		} else {
			cls = field.getType();
		}
		return getIdFieldOfAnObject(cls);
	}

	public static Field getIdFieldOfAnObject(Class<?> clazz) {
		log.info("Get ID FIELD FROM :" + clazz.getCanonicalName());

		if (getClassAnnotation(clazz, Entity.class) == null) {
			return null;
		}
		List<Field> fields = getDeclaredFields(clazz);

		for (Field field : fields) {

			if (field.getAnnotation(Id.class) != null) {

				return field;
			}
		}

		return null;
	}

	public static boolean isNumericField(Field field) {
		return field.getType().equals(Integer.class) || field.getType().equals(Double.class)
				|| field.getType().equals(Long.class) || field.getType().equals(BigDecimal.class)
				|| field.getType().equals(int.class)
				|| field.getType().equals(long.class)
				|| field.getType().equals(double.class)
				|| field.getType().equals(BigInteger.class);
	}

	/**
	 * copy object with option ID included or NOT
	 * 
	 * @param source
	 * @param targetClass
	 * @param withId
	 * @return
	 */
	public static <T extends BaseEntity> T copyFieldElementProperty(T source,
			boolean withId) {
		log.info("Will Copy Class :" + source.getClass().getCanonicalName());

		T targetObject = null;
		try {
			targetObject = (T) source.getClass().newInstance();

		} catch (Exception e) {
			log.error("Error when create instance");
			e.printStackTrace();
		}
		List<Field> fields = getDeclaredFields(source.getClass());

		for (Field field : fields) {

			if (field.getAnnotation(Id.class) != null && !withId) {
				continue;
			}
			if (isStaticField(field)) {
				continue;
			}

			Field currentField = getDeclaredField(source.getClass(), field.getName());

			if (currentField == null)
				continue;

			currentField.setAccessible(true);
			field.setAccessible(true);

			try {
				currentField.set(targetObject, field.get(source));

			} catch (Exception e) {
				log.error("Error set new value");
				e.printStackTrace();
			}

		}

		if (targetObject.getCreatedDate() == null) {
			targetObject.setCreatedDate(new Date());
		}
		targetObject.setModifiedDate(new Date());

		return targetObject;
	}

	public static boolean isStaticField(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	public static <T> T getObjectFromListByFieldName(final String fieldName, final Object value, final List<T> list) {

		if (null == list) {
			return null;
		}

		for (T object : list) {
			Field field = EntityUtil.getDeclaredField(object.getClass(), fieldName);
			field.setAccessible(true);
			try {
				Object fieldValue = field.get(object);

				if (fieldValue != null && fieldValue.equals(value)) {
					return (T) object;
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return null;
	}

	public static <T> boolean existInList(T o, List<T> list) {
		if (null == list) {
			log.error("LIST IS NULL");
			return false;
		}
		for (T object : list) {
			if (object.equals(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clone Serializable Object
	 * 
	 * @param <T>
	 * @param serializable
	 * @return
	 */
	public static <T extends Serializable> T cloneSerializable(T serializable) {
		try {
			return SerializationUtils.clone(serializable);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Field> getFixedListFields(Class<? extends BaseEntity> entityClass) {
		List<Field> fields = new ArrayList<>();

		List<Field> declaredFields = getDeclaredFields(entityClass);
		for (int i = 0; i < declaredFields.size(); i++) {
			final Field field = declaredFields.get(i);

			FormField formField = getFieldAnnotation(field, FormField.class);
			if (null == formField) {
				continue;
			}

			boolean superClassAvailable = field.getType().getSuperclass() != null;
			boolean isBaseEntitySubClass = superClassAvailable
					&& field.getType().getSuperclass().equals(BaseEntity.class);
			boolean isCollectionOfBaseEntity = CollectionUtil.isCollectionOfBaseEntity(field);

			if ((isBaseEntitySubClass || isCollectionOfBaseEntity) && formField.type().equals(FIELD_TYPE_FIXED_LIST)) {
				fields.add(field);
			}

		}
		return fields;
	}

	  
	public static <T> T castObject(Object o) {
		try {
			return (T) o;
		} catch (Exception e) {
			log.error("Error casting object: {}", o.getClass());
			throw e;
		}
	}

	public static boolean hasInterface(Class class1, Class _interface) {
		Class[] interfaces = class1.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if (interfaces[i].equals(_interface)) {
				return true;
			}
		}
		
		return false;
	}

}
