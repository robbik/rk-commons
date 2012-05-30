package rk.commons.inject.util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import rk.commons.inject.factory.ObjectInstantiationException;
import rk.commons.inject.factory.config.ObjectDefinition;
import rk.commons.inject.factory.support.ObjectDefinitionValueResolver;
import rk.commons.inject.factory.type.converter.TypeConverterResolver;
import rk.commons.util.ObjectUtils;
import rk.commons.util.StringUtils;

public abstract class PropertyUtils {

	public static String getObjectQName(String packageName, String objectName) {
		if (objectName == null) {
			objectName = "";
		} else {
			objectName = objectName.trim();
		}

		if (StringUtils.hasText(packageName)) {
			return packageName.trim().concat(":").concat(objectName);
		}

		return objectName;
	}

	public static String getPackageName(String objectQName) {
		if (objectQName == null) {
			objectQName = "";
		} else {
			objectQName = objectQName.trim();
		}

		if (StringUtils.hasText(objectQName)) {
			int lddi = objectQName.lastIndexOf(':');

			if (lddi < 0) {
				return "";
			} else {
				return objectQName.substring(0, lddi);
			}
		} else {
			return "";
		}
	}

	public static String applyDefaultPackageName(String packageName,
			String objectQName) {
		if (objectQName == null) {
			objectQName = "";
		} else {
			objectQName = objectQName.trim();
		}

		if (!StringUtils.hasText(packageName)) {
			return objectQName;
		}

		if (StringUtils.hasText(objectQName)) {
			int lddi = objectQName.lastIndexOf(':');

			if (lddi < 0) {
				return packageName.concat(":").concat(objectQName);
			} else {
				return objectQName;
			}
		} else {
			return packageName.concat(":");
		}
	}

	public static String applyPackageName(String packageName, String objectQName) {
		if (objectQName == null) {
			objectQName = "";
		} else {
			objectQName = objectQName.trim();
		}

		if (StringUtils.hasText(packageName)) {
			packageName = packageName.concat(":");
		} else {
			packageName = "";
		}

		if (StringUtils.hasText(objectQName)) {
			int lddi = objectQName.lastIndexOf(':');

			if (lddi < 0) {
				return packageName.concat(objectQName);
			} else {
				return packageName.concat(objectQName.substring(lddi + 1));
			}
		} else {
			return packageName;
		}
	}

	public static String generateRandomObjectQName(String packageName,
			String objectName) {
		return getObjectQName(packageName, objectName).concat("__").concat(
				StringUtils.valueOf(UUID.randomUUID()));
	}

	public static String generateRandomObjectQName(String packageName,
			ObjectDefinition definition) {

		String className;

		if (StringUtils.hasText(definition.getObjectClassName())) {
			className = definition.getObjectClassName();
		} else if (definition.getObjectClass() != null) {
			className = definition.getObjectClass().getName();
		} else {
			className = "(anonymous)";
		}

		int lastDotIndex = className.lastIndexOf('.');

		if (lastDotIndex >= 0) {
			className = className.substring(lastDotIndex + 1);
		}

		return generateRandomObjectQName(packageName, className);
	}
	
	public static void applyPropertyValues(String objectQName, Object object,
			Map<String, Object> propertyValues) {
		
		applyPropertyValues(objectQName, object, propertyValues, null, null);
	}
	
	public static void applyPropertyValues(String objectQName, Object object,
			Map<String, Object> propertyValues,
			TypeConverterResolver typeConverterResolver) {
		
		applyPropertyValues(objectQName, object, propertyValues, null, typeConverterResolver);
	}

	public static void applyPropertyValues(String objectQName, Object object,
			Map<String, Object> propertyValues,
			ObjectDefinitionValueResolver valueResolver,
			TypeConverterResolver typeConverterResolver) {

		Class<?> objectType = object.getClass();
		
		for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
			String name = entry.getKey();
			
			Object value;
			
			if (valueResolver == null) {
				value = entry.getValue();
			} else {
				value = valueResolver.resolveValueIfNecessary(entry.getValue());
			}
			
			applyPropertyValue(objectQName, object, objectType, name, value, typeConverterResolver);
		}
	}
	
	public static void applyPropertyValue(String objectQName, Object object, Class<?> objectType,
			String name,
			Object value,
			TypeConverterResolver typeConverterResolver) {

		try {
			if (name.contains(".")) {
				int dot = name.indexOf('.');
				
				String subname = name.substring(0, dot);
				String methodName = "get" + Character.toUpperCase(subname.charAt(0)) + subname.substring(1);
				
				Method getter = MethodUtils.findPublicMethod(objectType, methodName);
				if (getter == null) {
					throw new NoSuchMethodException(objectType + "." + methodName + "()");
				}
				
				Object nextBean = getter.invoke(objectType);
				
				applyPropertyValue(objectQName, nextBean, nextBean.getClass(), name.substring(dot + 1), value, typeConverterResolver);
				return;
			}
			
			String methodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
			Class<?> valueType = value.getClass();
			
			Method setter = MethodUtils.findPublicMethod(objectType, methodName, valueType);
			
			if ((setter == null) && (typeConverterResolver != null)) {
				setter = MethodUtils.findConvertablePublicMethod(objectType, methodName,
						typeConverterResolver, valueType);
				
				if (setter != null) {
					value = typeConverterResolver.resolve(valueType,
							setter.getParameterTypes()[0]).convert(value);
				}
			}
			
			if (setter == null) {
				Class<?> primitive = ObjectUtils.getPrimitiveType(valueType);
				
				if (primitive != null) {
					setter = MethodUtils.findPublicMethod(objectType, methodName, primitive);
				}
			}
			
			if (setter == null) {
				throw new NoSuchMethodException(objectType + "." + methodName + "(" + valueType + ")");
			}

			setter.invoke(object, value);
		} catch (Throwable t) {
			throw new ObjectInstantiationException(objectQName, t);
		}
	}
}