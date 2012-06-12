package rk.commons.inject.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rk.commons.inject.annotation.Destroy;
import rk.commons.inject.annotation.Init;
import rk.commons.inject.annotation.Inject;
import rk.commons.inject.factory.config.ObjectDefinition;
import rk.commons.inject.factory.support.FactoryObject;
import rk.commons.inject.factory.support.ObjectDefinitionRegistry;
import rk.commons.inject.factory.support.ObjectDefinitionValueResolver;
import rk.commons.inject.factory.type.converter.TypeConverterResolver;
import rk.commons.inject.util.AnnotationHelper;
import rk.commons.inject.util.PropertyHelper;
import rk.commons.loader.ResourceLoader;

public abstract class AbstractObjectFactory implements ObjectFactory, ObjectDefinitionRegistry {
	
	protected final TypeConverterResolver typeConverterResolver;

	protected final ObjectDefinitionValueResolver valueResolver;

	protected final Map<String, ObjectDefinition> definitions;
	
	protected final ResourceLoader resourceLoader;

	protected AbstractObjectFactory(ResourceLoader resourceLoader) {
		typeConverterResolver = new TypeConverterResolver();

		valueResolver = new ObjectDefinitionValueResolver(this);

		definitions = Collections.synchronizedMap(new HashMap<String, ObjectDefinition>());
		
		this.resourceLoader = resourceLoader;
	}
	
	public TypeConverterResolver getTypeConverterResolver() {
		return typeConverterResolver;
	}

	public abstract boolean containsObject(String objectName);

	public Object getObject(String objectName) {
		Object object = doGetObject(objectName);

		if (object == null) {
			object = createObject(getObjectDefinition(objectName));
		}

		if (object instanceof FactoryObject<?>) {
			object = ((FactoryObject<?>) object).getObject();
		}

		return object;
	}
	
	public <T> T getObject(String objectName, Class<T> type) {
		Object object = getObject(objectName);
		
		if (type.isInstance(object)) {
			return type.cast(object);
		}
		
		throw new ObjectNotFoundException(objectName + " with type " + type);
	}

	public abstract <T> Map<String, T> getObjectsOfType(Class<T> type);

	public abstract Set<String> getObjectNames();

	public Object createObject(final ObjectDefinition definition) {
		final String objectName = definition.getObjectName();
		
		Object object;
		
		try {
			object = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
				
				public Object run() throws Exception {
					return doCreateObject(definition);
				}
			});
		} catch (PrivilegedActionException e) {
			throw new ObjectInstantiationException(objectName, e.getException());
		} catch (Throwable cause) {
			throw new ObjectInstantiationException(objectName, cause);
		}
		
		return object;
	}
	
	protected abstract Object doGetObject(String objectName);
	
	protected Object doCreateObject(ObjectDefinition definition) throws Exception {
		String objectName = definition.getObjectName();
		
		// resolve object class
		definition.resolveClass(resourceLoader);
		
		// create object instance
		Constructor<?> ctor;
		
		try {
			ctor = definition.getObjectClass().getDeclaredConstructor();

			ctor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			ctor = definition.getObjectClass().getConstructor();
		}
		
		Object object = ctor.newInstance();
		
		// merge property values
		Map<String, Object> merged = new HashMap<String, Object>();
		
		merged.putAll(definition.getPropertyValues());
		
		// apply property values
		PropertyHelper.applyPropertyValues(objectName, object, merged, valueResolver, typeConverterResolver);
		
		// post construction
		postCreate(objectName, object);
		
		// init
		invokeInit(objectName, object);
		
		return object;
	}
	
	protected void postCreate(String objectName, Object object) throws Exception {
		Class<?> type = object.getClass();
		
		// @Inject
		List<Field> fields = AnnotationHelper.findAnnotatedFields(type, Inject.class);
		for (Field f : fields) {
			f.setAccessible(true);
			
			Class<?> ftype = f.getType();
			
			if (String.class.isAssignableFrom(ftype)) {
				f.set(object, objectName);
			} else if (ObjectFactory.class.isAssignableFrom(ftype)) {
				f.set(object, this);
			} else if (ResourceLoader.class.isAssignableFrom(ftype)) {
				f.set(object, resourceLoader);
			}
		}
	}
	
	protected void customFieldInject(String objectName, Object object, Field f, Class<?> type) throws Exception {
		throw new ClassCastException("unsupported type " + type + " for field annotation injection");
	}
	
	protected void invokeInit(String objectName, Object object) throws Exception {
		// @Init
		List<Method> methods = AnnotationHelper.findAnnotatedMethods(object.getClass(), Init.class);
		for (Method m : methods) {
			m.setAccessible(true);
			
			try {
				m.invoke(object);
			} catch (Throwable cause) {
				throw new ObjectInstantiationException(objectName, cause);
			}
		}
	}
	
	protected void invokeDestroy(Object object) {
		// @Destroy
		List<Method> methods = AnnotationHelper.findAnnotatedMethods(object.getClass(), Destroy.class);
		for (Method m : methods) {
			m.setAccessible(true);
			
			try {
				m.invoke(object);
			} catch (Throwable cause) {
				// do nothing
			}
		}
	}
	
	public abstract void destroy();
	
	public void registerObjectDefinition(ObjectDefinition definition) {
		String objectQName = definition.getObjectName();

		definitions.put(objectQName, definition);
	}

	public void removeObjectDefinition(String objectQName) {
		definitions.remove(objectQName);
	}

	public ObjectDefinition getObjectDefinition(String objectQName) {
		synchronized (definitions) {
			if (!definitions.containsKey(objectQName)) {
				throw new ObjectNotFoundException(objectQName);
			}

			return definitions.get(objectQName);
		}
	}

	public boolean containsObjectDefinition(String objectQName) {
		return definitions.containsKey(objectQName);
	}
}
