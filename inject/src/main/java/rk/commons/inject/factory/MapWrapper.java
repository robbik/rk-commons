package rk.commons.inject.factory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapWrapper implements Map<String, Object> {
	
	private final ObjectFactory factory;
	
	public MapWrapper(ObjectFactory factory) {
		this.factory = factory;
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(Object key) {
		return factory.containsObject((String) key);
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		return factory.getObjectsOfType(Object.class).entrySet();
	}

	public Object get(Object key) {
		return factory.getObject((String) key);
	}

	public boolean isEmpty() {
		return factory.getObjectNames().isEmpty();
	}

	public Set<String> keySet() {
		return factory.getObjectNames();
	}

	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return factory.getObjectNames().size();
	}

	public Collection<Object> values() {
		return factory.getObjectsOfType(Object.class).values();
	}
	
	@Override
	public String toString() {
		return factory.getObjectsOfType(Object.class).toString();
	}
}
