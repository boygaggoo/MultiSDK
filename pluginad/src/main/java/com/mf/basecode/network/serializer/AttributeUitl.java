package com.mf.basecode.network.serializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AttributeUitl {

	public static int getMessageCode(Object obj) {
		return getMessageCode(obj.getClass());
	}

	public static int getMessageCode(Class<?> cls) {
		SignalCode code = getMessageAttribute(cls);
		if (code != null) {
			return code.messageCode();
		}
		return 0;
	}

	public static SignalCode getMessageAttribute(Object obj) {
		return getMessageAttribute(obj.getClass());
	}

	public static SignalCode getMessageAttribute(Class<?> cls) {
		for (Annotation anno : cls.getAnnotations()) {
			if (anno.annotationType().equals(SignalCode.class)) {
				return (SignalCode) anno;
			}
		}
		return null;
	}

	public static ByteField getFieldAttribute(Field field) {
		for (Annotation anno : field.getAnnotations()) {
			if (anno.annotationType().equals(ByteField.class)) {
				return (ByteField) anno;
			}
		}
		return null;
	}
}
