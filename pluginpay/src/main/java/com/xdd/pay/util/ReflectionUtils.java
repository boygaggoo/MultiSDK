package com.xdd.pay.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ReflectionUtils {
	public static Object getNewInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] args) throws Exception {
		Constructor<?> constructor = clazz.getConstructor(parameterTypes);
		return constructor.newInstance(args);
	}
	
	public static Object invokeMethod4Static(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] args) throws Exception {
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(clazz, args);
	}

	public static Object invokeMethodByInstance(Class<?> clazz, Object instance, String methodName, Class<?>[] parameterTypes, Object[] args) throws Exception {
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(instance, args);
	}
}