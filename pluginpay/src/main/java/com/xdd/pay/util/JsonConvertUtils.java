package com.xdd.pay.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonConvertUtils {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object jsonToBean(String jsonStr, Class<?> beanClass) throws Exception {
		JSONObject obj = new JSONObject(jsonStr);

		Object bean = beanClass.getConstructor().newInstance();

		Iterator<String> iterator4Key = obj.keys();
		while (iterator4Key.hasNext()) {
			String key = iterator4Key.next();
			Object val = obj.get(key);

			Field field = beanClass.getDeclaredField(key);
			Class<?> fieldType = field.getType();
			if (fieldType.isPrimitive() || isWrapClass(fieldType) || fieldType == String.class) {
				Method methodSet = beanClass.getDeclaredMethod("set" + key.substring(0, 1).toUpperCase(Locale.getDefault()) + key.substring(1), new Class[] { fieldType });
				methodSet.invoke(bean, new Object[] { val });
			} else {
				if (fieldType.isArray()) {
					System.out.println("isArray");
				} else if (List.class.isAssignableFrom(fieldType)) {
					Type type = field.getGenericType();
					if (type instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) type;
						Class<?> itemClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
						List listObj = new ArrayList();

						JSONArray listArray = null;
						try {
							listArray = new JSONArray((String) val);
						} catch (Exception e) {
							listArray = (JSONArray) val;
						}

						if (itemClass.isPrimitive() || isWrapClass(itemClass) || itemClass == String.class) {
							for (int i = 0; i < listArray.length(); i++) {
								Object valItem = listArray.get(i);
								listObj.add(valItem);
							}
						} else {
							for (int i = 0; i < listArray.length(); i++) {
								Object valItem = jsonToBean(((JSONObject) listArray.get(i)).toString(), itemClass);
								listObj.add(valItem);
							}
						}

						Method methodSet = beanClass.getDeclaredMethod("set" + key.substring(0, 1).toUpperCase(Locale.getDefault()) + key.substring(1), new Class[] { fieldType });
						methodSet.invoke(bean, new Object[] { listObj });
					}

				} else if (Map.class.isAssignableFrom(fieldType)) {
					/*Type type = field.getGenericType();
					if (type instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) type;
						Class<?> itemClass1 = (Class<?>) parameterizedType.getActualTypeArguments()[0];
						Class<?> itemClass2 = (Class<?>) parameterizedType.getActualTypeArguments()[1];
					}*/

					if (null != val && !val.equals("")) {
						Map mapObj = new HashMap();
						JSONObject mapJson = null;
						try {
							mapJson = new JSONObject((String) val);
						} catch (Exception e) {
							mapJson = (JSONObject) val;
						}
						
						Iterator<String> iterator = mapJson.keys();
						while (iterator.hasNext()) {
							String itemKey = iterator.next();
							Object itemVal = mapJson.get(itemKey);
							mapObj.put(itemKey, itemVal);
						}
						
						Method methodSet = beanClass.getDeclaredMethod("set" + key.substring(0, 1).toUpperCase(Locale.getDefault()) + key.substring(1), new Class[] { fieldType });
						methodSet.invoke(bean, new Object[] { mapObj });
					}
				} else {
					System.out.println("else");
				}
			}
		}

		return bean;
	}

	@SuppressWarnings("rawtypes")
	public static JSONObject beanToJson(Object bean) throws Exception {
		Class<?> beanClass = bean.getClass();
		JSONObject jsonObject = new JSONObject();

		Field[] fieldArray = beanClass.getDeclaredFields();
		for (Field field : fieldArray) {
			Class<?> fieldType = field.getType();
			String key = field.getName();
			if (fieldType.isPrimitive() || isWrapClass(fieldType) || fieldType == String.class) {
				Method methodGet = beanClass.getDeclaredMethod("get" + key.substring(0, 1).toUpperCase(Locale.getDefault()) + key.substring(1));
				Object val = methodGet.invoke(bean);
				jsonObject.put(key, val);
			} else {
				if (fieldType.isArray()) {
					System.out.println("isArray");
				} else if (List.class.isAssignableFrom(fieldType)) {
					JSONArray jsonArray = new JSONArray();

					Method methodGet = beanClass.getDeclaredMethod("get" + key.substring(0, 1).toUpperCase(Locale.getDefault()) + key.substring(1));
					List valList = (List) methodGet.invoke(bean);

					if (null != valList) {
						for (int i = 0; i < valList.size(); i++) {
							Object valItem = valList.get(i);
							JSONObject valObj = beanToJson(valItem);
							jsonArray.put(valObj);
						}

						jsonObject.put(key, jsonArray);
					}
				} else if (Map.class.isAssignableFrom(fieldType)) {
					Method methodGet = beanClass.getDeclaredMethod("get" + key.substring(0, 1).toUpperCase(Locale.getDefault()) + key.substring(1));
					Map valMap = (Map) methodGet.invoke(bean);

					JSONObject valObj = new JSONObject();
					for (Object keyItem : valMap.keySet()) {
						valObj.put((String) keyItem, valMap.get(keyItem));
					}

					jsonObject.put(key, valObj.toString());
				} else {
					System.out.println("else");
				}
			}
		}

		return jsonObject;
	}

	private static boolean isWrapClass(Class<?> clz) {
		try {
			return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

}