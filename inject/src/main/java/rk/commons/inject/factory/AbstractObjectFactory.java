package rk.commons.inject.factory;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rk.commons.inject.factory.config.ObjectDefinition;
import rk.commons.inject.factory.support.FactoryObject;
import rk.commons.inject.factory.support.InitializingObject;
import rk.commons.inject.factory.support.ObjectDefinitionRegistry;
import rk.commons.inject.factory.support.ObjectDefinitionValueResolver;
import rk.commons.inject.factory.support.ObjectFactoryAware;
import rk.commons.inject.factory.support.ObjectQNameAware;
import rk.commons.inject.factory.support.ResourceLoaderAware;
import rk.commons.inject.factory.type.converter.TypeConverterResolver;
import rk.commons.inject.util.PropertyHelper;
import rk.commons.loader.ResourceLoader;
import rk.commons.util.StringHelper;

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

	public abstract boolean containsObject(String objectQName);

	public Object getObject(String objectQName) {
		Object object = doGetObject(objectQName);

		if (object == null) {
			object = createObject(getObjectDefinition(objectQName));
		}

		if (object instanceof FactoryObject<?>) {
			object = ((FactoryObject<?>) object).getObject();
		}

		return object;
	}

	public abstract <T> Map<String, T> getObjectsOfType(Class<T> type);

	public abstract Set<String> getObjectQNames();

	public Object createObject(final ObjectDefinition definition) {
		final String objectQName = definition.getObjectQName();
		
		Object object;
		
		try {
			object = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
				
				public Object run() throws Exception {
					return doCreateObject(definition);
				}
			});
		} catch (PrivilegedActionException e) {
			throw new ObjectInstantiationException(objectQName, e.getException());
		} catch (Throwable cause) {
			throw new ObjectInstantiationException(objectQName, cause);
		}
		
		return object;
	}
	
	protected abstract Object doGetObject(String objectQName);
	
	protected Object doCreateObject(ObjectDefinition definition) throws Exception {
		String objectQName = definition.getObjectQName();
		
		String extendedObjectQName = definition.getExtends();
		
		ObjectDefinition exdefinition;
		
		if (StringHelper.hasText(extendedObjectQName, true)) {
			exdefinition = getObjectDefinition(extendedObjectQName);
		} else {
			exdefinition = null;
		}
		
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
		
		if (exdefinition != null) {
			merged.putAll(exdefinition.getPropertyValues());
		}
		
		merged.putAll(definition.getPropertyValues());
		
		// apply property values
		PropertyHelper.applyPropertyValues(objectQName, object, merged, valueResolver, typeConverterResolver);
		
		// post construction
		if (object instanceof ObjectQNameAware) {
			((ObjectQNameAware) object).setObjectQName(objectQName);
		}
		
		if (object instanceof ObjectFactoryAware) {
			((ObjectFactoryAware) object).setObjectFactory(this);
		}
		
		if (object instanceof ResourceLoaderAware) {
			((ResourceLoaderAware) object).setResourceLoader(resourceLoader);
		}
		
		if (object instanceof InitializingObject) {
			try {
				((InitializingObject) object).initialize();
			} catch (Throwable cause) {
				throw new ObjectInstantiationException(objectQName, cause);
			}
		}
		
		return object;
	}
	
	public abstract void destroy();
	
	public void registerObjectDefinition(ObjectDefinition definition) {
		String objectQName = definition.getObjectQName();

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
