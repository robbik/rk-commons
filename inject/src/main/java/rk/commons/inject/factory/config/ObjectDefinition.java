package rk.commons.inject.factory.config;

import java.util.HashMap;
import java.util.Map;

import rk.commons.loader.ResourceLoader;
import rk.commons.util.StringHelper;

public class ObjectDefinition {

	protected String objectName;

	protected String objectClassName;

	protected Class<?> objectClass;
	
	protected Map<String, Object> propertyValues;

	protected String initMethod;

	protected String destroyMethod;

	public ObjectDefinition() {
		propertyValues = new HashMap<String, Object>();
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectClassName() {
		return objectClassName;
	}

	public void setObjectClassName(String objectClassName) {
		this.objectClassName = objectClassName;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}
	
	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}
	
	public Map<String, Object> getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(Map<String, Object> propertyValues) {
		this.propertyValues = propertyValues;
	}

	public String getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}

	public String getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	public void resolveClass(ResourceLoader resourceLoader) {
		if (objectClass == null) {
			if (!StringHelper.hasText(objectClassName)) {
				throw new IllegalArgumentException(
						"object class name or object class must be specified for object " + objectName);
			}

			try {
				objectClass = resourceLoader.loadClass(objectClassName);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(
						"unable to load object class name '" + objectClassName + "' for object " + objectName);
			}
		}
	}
}
