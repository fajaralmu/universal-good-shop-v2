package com.fajar.shoppingmart.externalapp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.fajar.shoppingmart.util.CollectionUtil;
import com.fajar.shoppingmart.util.EntityUtil;
public class TypeScriptModelCreators {
	final static String inputDir = "D:\\Development\\Eclipse\\universal-good-shop-v2\\src\\"
			+ "main\\java\\com\\fajar\\shoppingmart\\entity\\setting\\";
	final static String outputDir = "D:\\Development\\Front End\\universalshop\\good-shop-v2\\src\\models\\";
	public static void main(String[] args) {
		List<String> names = getJavaFiles();
		List<Class> classes = getJavaClasses("com.fajar.shoppingmart.entity.setting", names);
		for (Class class1 : classes) {
			String content = printClass(class1);
			writeFile(outputDir+class1.getSimpleName()+".ts", content);
		}
	}
	private static List<Class> getJavaClasses(String packageName, List<String> fileNames) {
		
		List<Class> classes = new ArrayList<>();
		for (String name : fileNames) {
			Class _class;
			try {
				_class = Class.forName(packageName + "." + name);
				classes.add(_class);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return classes ;
	}

	private static void writeFile(String fileName, String content) {
		try {
			FileUtils.writeStringToFile(new File(fileName), content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static List<String> getJavaFiles() {
		File file = new File(inputDir );
		File[] listFiles = file.listFiles();

		List<String> fileNames = new ArrayList<String>();
		for (File file2 : listFiles) {
			String fileName = file2.getName();
			if (fileName.endsWith(".java") == false) {
				continue;
			}
			System.out.println(file2.getName());
			fileNames.add(fileName.replace(".java", ""));
		}
		
		return fileNames;
	}

	private static String printClass(Class<?> clazz) {
		System.out.println("============= "+clazz.getCanonicalName()+" ============\n");
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n");
		Class<?> superClass = clazz.getSuperclass();
		String superClassDeclaration =  "";
		List<Field> fields = EntityUtil.getDeclaredFields(clazz, false, false);
		Map<String,Object> importedClassNames = new HashMap<String, Object>();
		
		if (null != superClass && superClass.getCanonicalName().startsWith("com.fajar")) {
			superClassDeclaration = " extends "+superClass.getSimpleName();
			importedClassNames.put(superClass.getSimpleName(), true);
		}
		stringBuilder.append("export default class "+clazz.getSimpleName() +superClassDeclaration+"{\n");
		for (Field field : fields) {
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			if (isStatic) {
				continue;
			}
			if( field.getType().isAnnotation()) {
				continue;
			}
			String fieldType = "any";
			boolean isNumeric = false;
			try {
				isNumeric = EntityUtil.isNumericField(field);
			} catch (Exception e) {
			}
			if (isNumeric) {
				fieldType = "number";
			}else if (field.getType().isEnum()) {
				fieldType = "String";
			} else if (field.getType().getCanonicalName().startsWith("com.fajar")) {
				fieldType = field.getType().getSimpleName();
				importedClassNames.put(fieldType, true);
			} else if(field.getType().isArray()) {
				fieldType = "[]";
			} else if(EntityUtil.hasInterface(field.getType(), Collection.class)) {
				fieldType = "[]";
//				Type[] genericTypes = CollectionUtil.getGenericTypes(field);
//				Type genericType = genericTypes[0];
//				fieldType = "Set<"
			} else if(EntityUtil.hasInterface(field.getType(), Map.class) || field.getType().equals(Map.class)) {
				fieldType = "{}";
			} else if (field.getType().equals(Object.class) || field.getType().equals(Class.class)) {
				
			} 
			else {
				fieldType = field.getType().getSimpleName();
			}
			stringBuilder.append("\t"+field.getName()+"?:"+fieldType +";\n");
		}
		
		stringBuilder.append("\n}\n");
		
		StringBuilder importSb = new StringBuilder();
		for (String string : importedClassNames.keySet()) {
			importSb.append("import "+string+ " from './"+string+"';\n");
		}
		System.out.println(importSb.toString());
		System.out.println(stringBuilder.toString());
		return importSb.toString()+stringBuilder.toString();
		
	}
}
