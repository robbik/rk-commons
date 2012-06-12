package rk.commons.inject.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AnnotationHelper {

	public static List<Field> findAnnotatedFields(Class<?> type, Class<? extends Annotation> annotationType) {
		List<Field> list = new ArrayList<Field>();
		
		while (type != null) {
			Field[] fields = type.getDeclaredFields();
			
			for (int i = 0, n = fields.length; i < n; ++i) {
				Field f = fields[i];
				
				if (f.isAnnotationPresent(annotationType)) {
					list.add(f);
				}
			}
			
			if (type.equals(Object.class)) {
				type = null;
			} else {
				type = type.getSuperclass();
			}
		}
		
		return list;
	}

	public static List<Method> findAnnotatedMethods(Class<?> type, Class<? extends Annotation> annotationType) {
		List<Method> list = new ArrayList<Method>();
		
		while (type != null) {
			Method[] methods = type.getDeclaredMethods();
			
			for (int i = 0, n = methods.length; i < n; ++i) {
				Method m = methods[i];
				
				if (m.isAnnotationPresent(annotationType)) {
					list.add(m);
				}
			}
			
			if (type.equals(Object.class)) {
				type = null;
			} else {
				type = type.getSuperclass();
			}
		}
		
		return list;
	}
}
