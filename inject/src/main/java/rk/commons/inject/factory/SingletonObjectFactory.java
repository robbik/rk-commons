package rk.commons.inject.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rk.commons.inject.factory.config.ObjectDefinition;
import rk.commons.loader.ResourceLoader;

public class SingletonObjectFactory extends AbstractObjectFactory {

	private final Map<String, Object> singletons;

	public SingletonObjectFactory(ResourceLoader resourceLoader) {
		super(resourceLoader);
		
		singletons = Collections.synchronizedMap(new HashMap<String, Object>());
	}

	public boolean containsObject(String objectQName) {
		return singletons.containsKey(objectQName);
	}

	public Object doGetObject(String objectQName) {
		return singletons.get(objectQName);
	}

	@SuppressWarnings("unchecked")
	public <T> Map<String, T> getObjectsOfType(Class<T> type) {
		Map<String, T> objects = new HashMap<String, T>();

		for (Map.Entry<String, Object> entry : singletons.entrySet()) {
			if (type.isInstance(entry.getValue())) {
				objects.put(entry.getKey(), (T) entry.getValue());
			}
		}

		return objects;
	}

	public Set<String> getObjectNames() {
		Set<String> set = new HashSet<String>();
		set.addAll(singletons.keySet());
		set.addAll(definitions.keySet());

		return set;
	}
	
	@Override
	protected Object doCreateObject(ObjectDefinition definition) throws Exception {
		String objectQName = definition.getObjectName();
		
		Object object = singletons.get(objectQName);
		
		if (object == null) {
			object = super.doCreateObject(definition);
			
			singletons.put(objectQName, object);
		}
		
		return object;
	}
	
	public void destroy() {
		for (Object object : singletons.values()) {
			invokeDestroy(object);
		}
	}
}
