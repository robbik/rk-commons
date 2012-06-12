package rk.commons.inject.util;

import java.lang.reflect.Method;
import java.util.Map;

import rk.commons.inject.factory.ObjectInstantiationException;
import rk.commons.inject.factory.config.ObjectDefinition;
import rk.commons.inject.factory.support.ObjectDefinitionValueResolver;
import rk.commons.inject.factory.type.converter.TypeConverterResolver;
import rk.commons.util.ObjectHelper;
import rk.commons.util.StringHelper;

public abstract class PropertyHelper {

	public static String getObjectName(String objectNamePrefix, String objectNameSuffix,
			String objectName) {
		
		if (StringHelper.hasText(objectNamePrefix)) {
			objectName = objectNamePrefix.concat(objectName);
		}
		
		if (StringHelper.hasText(objectNameSuffix)) {
			objectName = objectName.concat(objectNameSuffix);
		}
		
		return objectName;
	}

	public static String generateObjectName(String objectNamePrefix, String objectNameSuffix,
			String objectName, int counter) {
		
		if (StringHelper.hasText(objectNamePrefix)) {
			objectName = objectNamePrefix.concat(objectName);
		}
		
		objectName = objectName.concat("#").concat(String.valueOf(counter));
		
		if (StringHelper.hasText(objectNameSuffix)) {
			objectName = objectName.concat(objectNameSuffix);
		}
		
		return objectName;
	}

	public static String generateObjectName(String objectNamePrefix, String objectNameSuffix,
			ObjectDefinition definition, int counter) {
		String className;

		if (StringHelper.hasText(definition.getObjectClassName())) {
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

		return generateObjectName(objectNamePrefix, objectNameSuffix, className, counter);
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
				
				Method getter = MethodHelper.findPublicMethod(objectType, methodName);
				if (getter == null) {
					throw new NoSuchMethodException(objectType + "." + methodName + "()");
				}
				
				Object nextBean = getter.invoke(objectType);
				
				applyPropertyValue(objectQName, nextBean, nextBean.getClass(), name.substring(dot + 1), value, typeConverterResolver);
				return;
			}
			
			String methodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
			Class<?> valueType = value.getClass();
			
			Method setter = MethodHelper.findPublicMethod(objectType, methodName, valueType);
			
			if ((setter == null) && (typeConverterResolver != null)) {
				setter = MethodHelper.findConvertablePublicMethod(objectType, methodName,
						typeConverterResolver, valueType);
				
				if (setter != null) {
					value = typeConverterResolver.resolve(valueType,
							setter.getParameterTypes()[0]).convert(value);
				}
			}
			
			if (setter == null) {
				Class<?> primitive = ObjectHelper.getPrimitiveType(valueType);
				
				if (primitive != null) {
					setter = MethodHelper.findPublicMethod(objectType, methodName, primitive);
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
