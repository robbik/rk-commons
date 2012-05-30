package rk.commons.inject.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import rk.commons.inject.factory.type.converter.TypeConverterResolver;

public abstract class MethodHelper extends rk.commons.util.MethodHelper {

	public static Method findConvertablePublicMethod(Class<?> _class, String methodName,
			TypeConverterResolver resolver, Class<?> valueType) {
		Method found = null;
		
		while (_class != null) {
			Method[] methods = _class.getDeclaredMethods();

			for (int i = 0, n = methods.length; i < n; ++i) {
				Method method = methods[i];

				if (Modifier.isPublic(method.getModifiers()) && methodName.equals(method.getName())) {
					Class<?>[] methodParameterTypes = method.getParameterTypes();
					
					if ((methodParameterTypes != null) && (methodParameterTypes.length == 1)) {
						if (resolver.resolve(valueType, methodParameterTypes[0]) != null) {
							found = method;
							
							break;
						}
					}
				}
			}
			
			if (_class.equals(Object.class)) {
				_class = null;
			} else {
				_class = _class.getSuperclass();
			}
		}

		return found;
	}
}
